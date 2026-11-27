package com.isaiahnoelpulidosalazar.inpsAndroid.network;

import android.os.Looper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class HttpRequestTest {

    private ServerSocket serverSocket;
    private ExecutorService serverExecutor;
    private String baseUrl;
    private volatile boolean running;

    @Before
    public void setUp() throws IOException {
        serverSocket = new ServerSocket(0, 16, InetAddress.getByName("127.0.0.1"));
        running = true;
        serverExecutor = Executors.newCachedThreadPool();
        serverExecutor.submit(() -> {
            while (running) {
                try {
                    Socket client = serverSocket.accept();
                    serverExecutor.submit(() -> handleClient(client));
                } catch (IOException ignored) {
                    if (!running) break;
                }
            }
        });
        baseUrl = "http://127.0.0.1:" + serverSocket.getLocalPort();
    }

    private void handleClient(Socket client) {
        try (Socket socket = client;
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             OutputStream out = socket.getOutputStream()) {

            String requestLine = reader.readLine();
            if (requestLine == null) return;
            String[] parts = requestLine.split(" ");
            String path = parts.length > 1 ? parts[1] : "/";

            String testHeader = null;
            int contentLength = 0;
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                int colon = line.indexOf(':');
                if (colon > 0) {
                    String name = line.substring(0, colon).trim().toLowerCase(Locale.ROOT);
                    String val = line.substring(colon + 1).trim();
                    if ("x-test".equals(name)) {
                        testHeader = val;
                    } else if ("content-length".equals(name)) {
                        contentLength = Integer.parseInt(val);
                    }
                }
            }

            StringBuilder bodyBuilder = new StringBuilder();
            if (contentLength > 0) {
                char[] buf = new char[contentLength];
                int read = 0;
                while (read < contentLength) {
                    int n = reader.read(buf, read, contentLength - read);
                    if (n == -1) break;
                    read += n;
                }
                bodyBuilder.append(buf, 0, read);
            }

            int status;
            String statusText;
            String responseBody;
            if ("/hello".equals(path)) {
                status = 200;
                statusText = "OK";
                responseBody = "world";
            } else if ("/echo".equals(path)) {
                status = 201;
                statusText = "Created";
                responseBody = testHeader + ":" + bodyBuilder;
            } else {
                status = 404;
                statusText = "Not Found";
                responseBody = "not found";
            }

            byte[] respBytes = responseBody.getBytes(StandardCharsets.UTF_8);
            String headers = "HTTP/1.1 " + status + " " + statusText + "\r\n"
                    + "Content-Length: " + respBytes.length + "\r\n"
                    + "Connection: close\r\n\r\n";
            out.write(headers.getBytes(StandardCharsets.US_ASCII));
            out.write(respBytes);
            out.flush();
        } catch (IOException ignored) {
        }
    }

    @After
    public void tearDown() throws IOException {
        running = false;
        if (serverSocket != null) {
            serverSocket.close();
        }
        if (serverExecutor != null) {
            serverExecutor.shutdownNow();
        }
    }

    @Test
    public void testSyncGetPostAndErrorCode() throws IOException {
        HttpRequest.Response getResp = HttpRequest.get(baseUrl + "/hello").timeoutMs(5000).execute();
        assertEquals(200, getResp.code);
        assertTrue(getResp.isSuccessful());
        assertEquals("world", getResp.body);

        HttpRequest.Response postResp = HttpRequest.post(baseUrl + "/echo", "payload")
                .header("X-Test", "ok")
                .execute();
        assertEquals(201, postResp.code);
        assertTrue(postResp.isSuccessful());
        assertEquals("ok:payload", postResp.body);

        HttpRequest.Response errResp = HttpRequest.of("GET", baseUrl + "/missing").execute();
        assertEquals(404, errResp.code);
        assertFalse(errResp.isSuccessful());
        assertEquals("not found", errResp.body);
    }

    @Test
    public void testAsyncEnqueueDeliversOnMainThread() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<HttpRequest.Response> respRef = new AtomicReference<>();
        AtomicReference<Exception> errRef = new AtomicReference<>();

        HttpRequest.get(baseUrl + "/hello").enqueue(new HttpRequest.Callback() {
            @Override
            public void onSuccess(HttpRequest.Response response) {
                respRef.set(response);
                latch.countDown();
            }

            @Override
            public void onError(Exception error) {
                errRef.set(error);
                latch.countDown();
            }
        });

        long deadline = System.currentTimeMillis() + 5000;
        while (latch.getCount() > 0 && System.currentTimeMillis() < deadline) {
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            latch.await(20, TimeUnit.MILLISECONDS);
        }

        assertNull(errRef.get());
        assertNotNull(respRef.get());
        assertEquals(200, respRef.get().code);
        assertEquals("world", respRef.get().body);
    }
}
