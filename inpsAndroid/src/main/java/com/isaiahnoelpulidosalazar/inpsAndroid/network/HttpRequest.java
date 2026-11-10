package com.isaiahnoelpulidosalazar.inpsAndroid.network;

import com.isaiahnoelpulidosalazar.inpsAndroid.concurrent.MainThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Minimal HTTP client replacing the deprecated AsyncTask-based {@code URLRequest}.
 *
 * <pre>{@code
 * HttpRequest.get("https://api.example.com/data").enqueue(new HttpRequest.Callback() {
 *     public void onSuccess(HttpRequest.Response r) { textView.setText(r.body); }
 *     public void onError(Exception e) { Log.e("TAG", "failed", e); }
 * });
 * }</pre>
 *
 * <p>Requests run on a shared bounded pool; callbacks are delivered on the main thread.
 * Non-2xx responses are reported through {@link Callback#onSuccess} with {@link Response#code}
 * set; {@link Callback#onError} is for I/O failures. Remember cleartext HTTP needs
 * {@code usesCleartextTraffic} on API 28+.</p>
 */
public final class HttpRequest {

    /** Receives results on the main thread. */
    public interface Callback {
        void onSuccess(Response response);
        void onError(Exception error);
    }

    /** Status code and decoded (UTF-8) body, including error bodies for 4xx/5xx. */
    public static final class Response {
        public final int code;
        public final String body;

        Response(int code, String body) {
            this.code = code;
            this.body = body;
        }

        public boolean isSuccessful() { return code >= 200 && code < 300; }
    }

    private static final ThreadPoolExecutor EXECUTOR =
            new ThreadPoolExecutor(4, 4, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    static { EXECUTOR.allowCoreThreadTimeOut(true); }

    private final String url;
    private final String method;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private String body;
    private int timeoutMs = 15_000;

    private HttpRequest(String method, String url) {
        this.method = method;
        this.url = url;
    }

    public static HttpRequest get(String url) { return new HttpRequest("GET", url); }

    public static HttpRequest post(String url, String body) { return new HttpRequest("POST", url).body(body); }

    public static HttpRequest of(String method, String url) { return new HttpRequest(method, url); }

    public HttpRequest header(String name, String value) { headers.put(name, value); return this; }

    /** UTF-8 request body (sent for any method that allows one). */
    public HttpRequest body(String body) { this.body = body; return this; }

    /** Connect and read timeout in milliseconds (default 15,000). */
    public HttpRequest timeoutMs(int timeoutMs) { this.timeoutMs = timeoutMs; return this; }

    /** Blocking call. Must not be used on the main thread. */
    public Response execute() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        try {
            connection.setRequestMethod(method);
            connection.setConnectTimeout(timeoutMs);
            connection.setReadTimeout(timeoutMs);
            for (Map.Entry<String, String> h : headers.entrySet()) {
                connection.setRequestProperty(h.getKey(), h.getValue());
            }
            if (body != null) {
                byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
                connection.setDoOutput(true);
                connection.setFixedLengthStreamingMode(bytes.length);
                try (OutputStream out = connection.getOutputStream()) {
                    out.write(bytes);
                }
            }
            int code = connection.getResponseCode();
            InputStream in = code >= 400 ? connection.getErrorStream() : connection.getInputStream();
            return new Response(code, in == null ? "" : readAll(in));
        } finally {
            connection.disconnect();
        }
    }

    /** Runs the request in the background and reports to {@code callback} on the main thread. */
    public Future<?> enqueue(final Callback callback) {
        return EXECUTOR.submit(new Runnable() {
            @Override public void run() {
                try {
                    final Response response = execute();
                    MainThread.post(new Runnable() { @Override public void run() { callback.onSuccess(response); } });
                } catch (final Exception e) {
                    MainThread.post(new Runnable() { @Override public void run() { callback.onError(e); } });
                }
            }
        });
    }

    private static String readAll(InputStream in) throws IOException {
        try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();
            char[] buf = new char[8192];
            int n;
            while ((n = reader.read(buf)) != -1) sb.append(buf, 0, n);
            return sb.toString();
        }
    }
}
