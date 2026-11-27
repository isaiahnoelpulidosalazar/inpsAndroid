package com.isaiahnoelpulidosalazar.inpsandroid.core.network;

import com.isaiahnoelpulidosalazar.inpsandroid.core.concurrent.MainThread;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Minimal HTTP client.
 *
 * <pre>{@code
 * WebRequest.Call call = WebRequest.get("https://api.example.com/data").enqueue(new WebRequest.Callback() {
 *     public void onSuccess(WebRequest.Response r) { textView.setText(r.body); }
 *     public void onError(Exception e) { Log.e("TAG", "failed", e); }
 * });
 * }</pre>
 *
 * <p>Asynchronous requests run on a shared pool bounded at 4 threads and a queue of 64 pending
 * requests; when full, {@link RejectedExecutionException} is delivered to
 * {@link Callback#onError} on the main thread. Callbacks are delivered on the main thread and
 * never fire after {@link Call#cancel()}. Non-2xx responses are reported through
 * {@link Callback#onSuccess} with {@link Response#code} set; {@link Callback#onError} is for I/O
 * and queue-rejection failures. Each {@link #execute()} call closes its underlying connection via
 * {@link HttpURLConnection#disconnect()}, trading keep-alive connection reuse for simplicity.</p>
 */
public final class WebRequest {

    /** Default maximum response size (8 MB). */
    public static final int DEFAULT_MAX_RESPONSE_BYTES = 8 * 1024 * 1024;

    /** Receives results on the main thread. */
    public interface Callback {
        void onSuccess(Response response);
        void onError(Exception error);
    }

    /** Handle for an enqueued {@link WebRequest}. */
    public interface Call {
        /** Cancels the request, interrupting the worker if running and suppressing callbacks. */
        void cancel();
        /** @return {@code true} if {@link #cancel()} has been called. */
        boolean isCancelled();
    }

    /** Status code and decoded body, including error bodies for 4xx/5xx. */
    public static final class Response {
        public final int code;
        public final String body;

        Response(int code, String body) {
            this.code = code;
            this.body = body;
        }

        public boolean isSuccessful() { return code >= 200 && code < 300; }
    }

    private static final ThreadPoolExecutor DEFAULT_EXECUTOR = createDefaultExecutor();
    private static volatile Executor executor = DEFAULT_EXECUTOR;

    private static ThreadPoolExecutor createDefaultExecutor() {
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                4, 4, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<>(64));
        pool.allowCoreThreadTimeOut(true);
        return pool;
    }

    /** Package-private test hook for injecting a custom executor; pass {@code null} to restore default. */
    static void setExecutor(Executor customExecutor) {
        executor = customExecutor != null ? customExecutor : DEFAULT_EXECUTOR;
    }

    private final String url;
    private final String method;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private String body;
    private int timeoutMs = 15_000;
    private int maxResponseBytes = DEFAULT_MAX_RESPONSE_BYTES;

    private WebRequest(String method, String url) {
        this.method = Objects.requireNonNull(method, "method");
        this.url = Objects.requireNonNull(url, "url");
    }

    public static WebRequest get(String url) { return new WebRequest("GET", url); }

    public static WebRequest post(String url, String body) { return new WebRequest("POST", url).body(body); }

    public static WebRequest of(String method, String url) { return new WebRequest(method, url); }

    public WebRequest header(String name, String value) {
        Objects.requireNonNull(name, "name");
        headers.put(name, value);
        return this;
    }

    /** Sets the UTF-8 request body. */
    public WebRequest body(String body) {
        this.body = body;
        return this;
    }

    /** Sets {@code Content-Type} to {@code application/json; charset=utf-8} and the request body. */
    public WebRequest json(String body) {
        removeHeaderIgnoreCase("Content-Type");
        headers.put("Content-Type", "application/json; charset=utf-8");
        this.body = body;
        return this;
    }

    /** Connect and read timeout in milliseconds (default 15,000). */
    public WebRequest timeoutMs(int timeoutMs) {
        this.timeoutMs = timeoutMs;
        return this;
    }

    /** Sets the maximum response size in bytes (default 8 MB). */
    public WebRequest maxResponseBytes(int maxResponseBytes) {
        if (maxResponseBytes <= 0) {
            throw new IllegalArgumentException("maxResponseBytes must be positive: " + maxResponseBytes);
        }
        this.maxResponseBytes = maxResponseBytes;
        return this;
    }

    /** Blocking call. Must not be used on the main thread. */
    public Response execute() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        try {
            connection.setRequestMethod(method);
            connection.setConnectTimeout(timeoutMs);
            connection.setReadTimeout(timeoutMs);

            boolean sendBody = body != null && allowsBody(method);
            if (sendBody && !hasHeaderIgnoreCase("Content-Type")) {
                connection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
            }
            for (Map.Entry<String, String> h : headers.entrySet()) {
                connection.setRequestProperty(h.getKey(), h.getValue());
            }
            if (sendBody) {
                byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
                connection.setDoOutput(true);
                connection.setFixedLengthStreamingMode(bytes.length);
                try (OutputStream out = connection.getOutputStream()) {
                    out.write(bytes);
                }
            }

            int code = connection.getResponseCode();
            InputStream in = code >= 400 ? connection.getErrorStream() : connection.getInputStream();
            Charset charset = parseCharset(connection.getContentType());
            String responseBody = in == null ? "" : readAllBounded(in, maxResponseBytes, charset);
            return new Response(code, responseBody);
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Runs the request on the bounded background pool and delivers the result to {@code callback}
     * on the main thread.
     */
    public Call enqueue(final Callback callback) {
        Objects.requireNonNull(callback, "callback");
        final CallHandle call = new CallHandle();
        final FutureTask<Void> task = new FutureTask<>(() -> {
            if (call.isCancelled()) return null;
            try {
                final Response response = execute();
                if (call.isCancelled()) return null;
                MainThread.post(() -> {
                    if (!call.isCancelled()) {
                        callback.onSuccess(response);
                    }
                });
            } catch (final Exception e) {
                if (call.isCancelled()) return null;
                MainThread.post(() -> {
                    if (!call.isCancelled()) {
                        callback.onError(e);
                    }
                });
            }
            return null;
        });
        call.setFuture(task);
        try {
            executor.execute(task);
        } catch (final RejectedExecutionException e) {
            if (!call.isCancelled()) {
                MainThread.post(() -> {
                    if (!call.isCancelled()) {
                        callback.onError(e);
                    }
                });
            }
        }
        return call;
    }

    private static boolean allowsBody(String method) {
        return !"GET".equalsIgnoreCase(method) && !"HEAD".equalsIgnoreCase(method);
    }

    private boolean hasHeaderIgnoreCase(String target) {
        for (String key : headers.keySet()) {
            if (target.equalsIgnoreCase(key)) return true;
        }
        return false;
    }

    private void removeHeaderIgnoreCase(String target) {
        headers.entrySet().removeIf(entry -> target.equalsIgnoreCase(entry.getKey()));
    }

    private static Charset parseCharset(String contentType) {
        if (contentType == null) return StandardCharsets.UTF_8;
        String[] parts = contentType.split(";");
        for (int i = 1; i < parts.length; i++) {
            String param = parts[i].trim();
            if (param.toLowerCase(Locale.ROOT).startsWith("charset=")) {
                String value = param.substring("charset=".length()).trim();
                if ((value.startsWith("\"") && value.endsWith("\""))
                        || (value.startsWith("'") && value.endsWith("'"))) {
                    if (value.length() >= 2) {
                        value = value.substring(1, value.length() - 1).trim();
                    }
                }
                if (!value.isEmpty()) {
                    try {
                        return Charset.forName(value);
                    } catch (IllegalArgumentException ignored) {
                        return StandardCharsets.UTF_8;
                    }
                }
            }
        }
        return StandardCharsets.UTF_8;
    }

    private static String readAllBounded(InputStream in, int maxBytes, Charset charset) throws IOException {
        try (InputStream stream = in) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int total = 0;
            int n;
            while ((n = stream.read(buf)) != -1) {
                total += n;
                if (total > maxBytes) {
                    throw new IOException("Response body exceeds maxResponseBytes (" + maxBytes + ")");
                }
                out.write(buf, 0, n);
            }
            return new String(out.toByteArray(), charset);
        }
    }

    private static final class CallHandle implements Call {
        private final AtomicBoolean cancelled = new AtomicBoolean(false);
        private volatile Future<?> future;

        void setFuture(Future<?> future) {
            this.future = future;
        }

        @Override
        public void cancel() {
            if (cancelled.compareAndSet(false, true)) {
                Future<?> f = future;
                if (f != null) {
                    f.cancel(true);
                }
            }
        }

        @Override
        public boolean isCancelled() {
            return cancelled.get();
        }
    }
}
