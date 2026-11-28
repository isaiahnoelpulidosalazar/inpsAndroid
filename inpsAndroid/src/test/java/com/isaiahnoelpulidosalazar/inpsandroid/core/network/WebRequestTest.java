package com.isaiahnoelpulidosalazar.inpsandroid.core.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import android.os.Looper;
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
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class WebRequestTest {

  private ServerSocket serverSocket;
  private ExecutorService serverExecutor;
  private String baseUrl;
  private volatile boolean running;
  private volatile CountDownLatch slowResponseLatch;

  @Before
  public void setUp() throws IOException {
    WebRequest.setExecutor(null);
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
        BufferedReader reader =
            new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        OutputStream out = socket.getOutputStream()) {

      String requestLine = reader.readLine();
      if (requestLine == null) return;
      String[] parts = requestLine.split(" ");
      String method = parts.length > 0 ? parts[0] : "GET";
      String path = parts.length > 1 ? parts[1] : "/";

      String testHeader = null;
      String contentTypeHeader = null;
      int contentLength = 0;
      String line;
      while ((line = reader.readLine()) != null && !line.isEmpty()) {
        int colon = line.indexOf(':');
        if (colon > 0) {
          String name = line.substring(0, colon).trim().toLowerCase(Locale.ROOT);
          String val = line.substring(colon + 1).trim();
          if ("x-test".equals(name)) {
            testHeader = val;
          } else if ("content-type".equals(name)) {
            contentTypeHeader = val;
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

      int status = 200;
      String statusText = "OK";
      String respContentType = "text/plain; charset=utf-8";
      byte[] respBytes;

      if ("/hello".equals(path)) {
        respBytes = (method + ":world").getBytes(StandardCharsets.UTF_8);
      } else if ("/echo".equals(path)) {
        status = 201;
        statusText = "Created";
        respBytes =
            (testHeader + "|" + contentTypeHeader + "|" + bodyBuilder)
                .getBytes(StandardCharsets.UTF_8);
      } else if ("/latin1".equals(path)) {
        respContentType = "text/plain; charset=ISO-8859-1";
        respBytes = "caf\u00e9".getBytes(StandardCharsets.ISO_8859_1);
      } else if ("/large".equals(path)) {
        respBytes = new byte[1024];
      } else if ("/slow".equals(path)) {
        CountDownLatch latch = slowResponseLatch;
        if (latch != null) {
          try {
            latch.await(2, TimeUnit.SECONDS);
          } catch (InterruptedException ignored) {
          }
        }
        respBytes = "slow".getBytes(StandardCharsets.UTF_8);
      } else {
        status = 404;
        statusText = "Not Found";
        respBytes = "not found".getBytes(StandardCharsets.UTF_8);
      }

      String headers =
          "HTTP/1.1 "
              + status
              + " "
              + statusText
              + "\r\n"
              + "Content-Type: "
              + respContentType
              + "\r\n"
              + "Content-Length: "
              + respBytes.length
              + "\r\n"
              + "Connection: close\r\n\r\n";
      out.write(headers.getBytes(StandardCharsets.US_ASCII));
      out.write(respBytes);
      out.flush();
    } catch (IOException ignored) {
    }
  }

  @After
  public void tearDown() throws IOException {
    WebRequest.setExecutor(null);
    running = false;
    if (slowResponseLatch != null) slowResponseLatch.countDown();
    if (serverSocket != null) serverSocket.close();
    if (serverExecutor != null) serverExecutor.shutdownNow();
  }

  @Test
  public void testGetWithoutBodyStaysGetEvenIfBodySet() throws IOException {
    WebRequest.Response getResp = WebRequest.get(baseUrl + "/hello").timeoutMs(5000).execute();
    assertEquals(200, getResp.code);
    assertTrue(getResp.isSuccessful());
    assertEquals("GET:world", getResp.body);

    WebRequest.Response getWithIgnoredBody =
        WebRequest.get(baseUrl + "/hello").body("should-not-flip-to-post").execute();
    assertEquals("GET:world", getWithIgnoredBody.body);
  }

  @Test
  public void testJsonAndDefaultContentTypeAndErrorCode() throws IOException {
    WebRequest.Response jsonResp =
        WebRequest.of("POST", baseUrl + "/echo").header("X-Test", "ok").json("{\"k\":1}").execute();
    assertEquals(201, jsonResp.code);
    assertEquals("ok|application/json; charset=utf-8|{\"k\":1}", jsonResp.body);

    WebRequest.Response defaultCtResp =
        WebRequest.post(baseUrl + "/echo", "plain-body").header("X-Test", "p").execute();
    assertEquals("p|text/plain; charset=utf-8|plain-body", defaultCtResp.body);

    WebRequest.Response errResp = WebRequest.of("GET", baseUrl + "/missing").execute();
    assertEquals(404, errResp.code);
    assertFalse(errResp.isSuccessful());
    assertEquals("not found", errResp.body);
  }

  @Test
  public void testResponseCharsetAndMaxResponseBytesCap() throws IOException {
    WebRequest.Response latin1Resp = WebRequest.get(baseUrl + "/latin1").execute();
    assertEquals("caf\u00e9", latin1Resp.body);

    assertThrows(
        IllegalArgumentException.class,
        () -> WebRequest.get(baseUrl + "/large").maxResponseBytes(0));
    assertThrows(
        IOException.class,
        () -> WebRequest.get(baseUrl + "/large").maxResponseBytes(512).execute());
  }

  @Test
  public void testAsyncEnqueueAndCancelSuppressesCallbacks() throws Exception {
    slowResponseLatch = new CountDownLatch(1);
    AtomicBoolean called = new AtomicBoolean(false);

    WebRequest.Call call =
        WebRequest.get(baseUrl + "/slow")
            .enqueue(
                new WebRequest.Callback() {
                  @Override
                  public void onSuccess(WebRequest.Response response) {
                    called.set(true);
                  }

                  @Override
                  public void onError(Exception error) {
                    called.set(true);
                  }
                });

    call.cancel();
    assertTrue(call.isCancelled());
    slowResponseLatch.countDown();
    Thread.sleep(100);
    Shadows.shadowOf(Looper.getMainLooper()).idle();

    assertFalse(called.get());
  }

  @Test
  public void testQueueFullRejectionReportsErrorOnMainThread() throws Exception {
    WebRequest.setExecutor(command -> {
      throw new RejectedExecutionException("queue full");
    });

    CountDownLatch latch = new CountDownLatch(1);
    AtomicReference<Exception> errRef = new AtomicReference<>();

    WebRequest.get(baseUrl + "/hello")
        .enqueue(
            new WebRequest.Callback() {
              @Override
              public void onSuccess(WebRequest.Response response) {
                latch.countDown();
              }

              @Override
              public void onError(Exception error) {
                errRef.set(error);
                latch.countDown();
              }
            });

    long deadline = System.currentTimeMillis() + 2000;
    while (latch.getCount() > 0 && System.currentTimeMillis() < deadline) {
      Shadows.shadowOf(Looper.getMainLooper()).idle();
      latch.await(20, TimeUnit.MILLISECONDS);
    }

    assertNotNull(errRef.get());
    assertTrue(errRef.get() instanceof RejectedExecutionException);
  }

  @Test
  public void testAsyncEnqueueSuccessOnMainThread() throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    AtomicReference<WebRequest.Response> respRef = new AtomicReference<>();
    AtomicReference<Exception> errRef = new AtomicReference<>();

    WebRequest.get(baseUrl + "/hello")
        .enqueue(
            new WebRequest.Callback() {
              @Override
              public void onSuccess(WebRequest.Response response) {
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
    assertEquals("GET:world", respRef.get().body);
  }
}
