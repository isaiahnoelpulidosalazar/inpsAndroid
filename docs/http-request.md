# HttpRequest

`HttpRequest` is a minimal HTTP client that runs requests on a shared bounded thread pool and delivers results to the main thread. It replaces the deprecated `URLRequest`.

## Purpose & Scope

The purpose of this utility is to provide:
- Asynchronous HTTP execution without `AsyncTask` or manual executor management
- Callbacks delivered on the main thread through `MainThread`
- Real status codes and error bodies instead of string conventions
- Configurable headers, UTF-8 body, and connect/read timeouts (default 15,000 ms)

Requests use a pool of 4 threads that time out after 30 seconds idle. Non-2xx responses are reported through `onSuccess` with `Response.code` set; `onError` is reserved for I/O failures. Cleartext HTTP requires `usesCleartextTraffic` on API 28+.

## API Reference

### HttpRequest.Callback

Interface receiving results on the main thread, with `onSuccess(Response response)` and `onError(Exception error)`.

```java
HttpRequest.Callback callback = new HttpRequest.Callback() {
    @Override public void onSuccess(HttpRequest.Response r) { textView.setText(r.body); }
    @Override public void onError(Exception e) { Log.e("TAG", "failed", e); }
};
```

### HttpRequest.Response

Holds `int code` and `String body` (UTF-8, including error bodies for 4xx/5xx). `isSuccessful()` returns `true` for codes 200-299.

```java
if (r.isSuccessful()) Log.d("TAG", r.body);
else Log.w("TAG", "HTTP " + r.code + ": " + r.body);
```

### static HttpRequest get(String url)

Creates a `GET` request.

```java
HttpRequest req = HttpRequest.get("https://api.example.com/data");
```

### static HttpRequest post(String url, String body)

Creates a `POST` request with a UTF-8 body.

```java
HttpRequest req = HttpRequest.post(url, "{\"a\":1}").header("Content-Type", "application/json");
```

### static HttpRequest of(String method, String url)

Creates a request with an arbitrary HTTP method.

```java
HttpRequest req = HttpRequest.of("DELETE", url);
```

### HttpRequest header(String name, String value)

Adds a request header. Returns the same instance for chaining.

```java
req.header("Accept", "application/json");
```

### HttpRequest body(String body)

Sets the UTF-8 request body, sent for any method that allows one.

```java
req.body("{\"name\":\"Alice\"}");
```

### HttpRequest timeoutMs(int timeoutMs)

Sets the connect and read timeout in milliseconds (default `15000`).

```java
req.timeoutMs(10_000);
```

### Response execute() throws IOException

Runs the request as a blocking call. Must not be used on the main thread.

```java
HttpRequest.Response r = HttpRequest.get(url).execute(); // background thread only
```

### Future<?> enqueue(Callback callback)

Runs the request in the background and reports to the callback on the main thread. The returned `Future` can be used to cancel.

```java
HttpRequest.get("https://api.example.com/data").enqueue(callback);
```
