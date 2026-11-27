# WebRequest

Package: `com.isaiahnoelpulidosalazar.inpsandroid.core.network`

`WebRequest` is a minimal HTTP client built on `HttpURLConnection`. Asynchronous requests run on a shared pool bounded at **4 threads** and a queue of **64 pending requests**. When the queue is full, `RejectedExecutionException` is delivered to `Callback.onError(Exception)` on the main thread.

## Features

- Bounded worker pool (4 threads, queue capacity 64)
- Cancellable `WebRequest.Call` handles (`cancel()` interrupts the worker and suppresses callbacks on both worker and main threads)
- Request helpers: `get(url)`, `post(url, body)`, `of(method, url)`, `header(name, value)`, `body(text)`, `json(text)`, `timeoutMs(ms)`, `maxResponseBytes(bytes)`
- Defaults to `text/plain; charset=utf-8` when a body is sent without an explicit `Content-Type` header (`json()` sets `application/json; charset=utf-8`)
- Never sets `doOutput(true)` on `GET`, `HEAD`, or bodyless requests
- Default maximum response size is `8 MB` (`WebRequest.DEFAULT_MAX_RESPONSE_BYTES`), throwing `IOException` if exceeded
- Parses the response `charset` from `Content-Type` with UTF-8 fallback
- Non-2xx status codes are delivered to `Callback.onSuccess(Response)` with `response.code` and `response.isSuccessful()`
- Each `execute()` closes its connection via `HttpURLConnection.disconnect()`, trading keep-alive connection reuse for simplicity

## Usage

```java
import com.isaiahnoelpulidosalazar.inpsandroid.core.network.WebRequest;

WebRequest.Call call = WebRequest.of("POST", "https://api.example.com/items")
        .header("Authorization", "Bearer " + token)
        .json("{\"name\":\"inpsAndroid\"}")
        .timeoutMs(10_000)
        .maxResponseBytes(1024 * 1024)
        .enqueue(new WebRequest.Callback() {
            @Override
            public void onSuccess(WebRequest.Response response) {
                if (response.isSuccessful()) {
                    outputView.setText(response.body);
                }
            }

            @Override
            public void onError(Exception error) {
                errorView.setText(error.getMessage());
            }
        });

// Cancel in onDestroy / lifecycle cleanup
if (!call.isCancelled()) {
    call.cancel();
}
```

## Blocking Execution

For work already running on a background thread, call `execute()` directly:

```java
WebRequest.Response response = WebRequest.get("https://api.example.com/health").execute();
```

> Do not call `execute()` on the Android main thread.
