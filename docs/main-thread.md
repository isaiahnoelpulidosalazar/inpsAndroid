# MainThread

The `MainThread` utility posts work to the Android main (UI) thread through one shared `Handler` bound explicitly to `Looper.getMainLooper()`.

## Purpose & Scope

The purpose of this utility is to provide:
- Safe posting from any thread, including threads without a `Looper`
- Built-in delayed execution and cancellation without `Handler` boilerplate
- A replacement for the deprecated `Actions` class (capture arguments in a lambda instead)

`MainThread` is a `final` class with a private constructor and cannot be instantiated.

## API Reference

### static boolean isMainThread()

Returns `true` if the calling thread is the main thread.

```java
if (!MainThread.isMainThread()) { /* on a worker thread */ }
```

### static void post(Runnable runnable)

Posts the runnable to the main thread's queue.

```java
MainThread.post(() -> textView.setText("Done"));
```

### static void postDelayed(Runnable runnable, long delayMs)

Posts the runnable to the main thread's queue after the given delay in milliseconds.

```java
MainThread.postDelayed(() -> banner.setVisibility(View.GONE), 1000);
```

### static void run(Runnable runnable)

Runs the runnable immediately when already on the main thread, otherwise posts it.

```java
MainThread.run(() -> progress.setProgress(50));
```

### static void cancel(Runnable runnable)

Removes pending posts of the given runnable.

```java
MainThread.cancel(hideBanner);
```
