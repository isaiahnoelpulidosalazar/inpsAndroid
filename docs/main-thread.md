# MainThread

Package: `com.isaiahnoelpulidosalazar.inpsandroid.core.concurrent`

`MainThread` posts work to the Android main looper using a shared `Handler` bound explicitly to `Looper.getMainLooper()`.

## API Reference

| Method | Description |
|---|---|
| `MainThread.isMainThread()` | Returns `true` if the calling thread is the main thread. |
| `MainThread.post(Runnable runnable)` | Posts `runnable` to the main thread's message queue. |
| `MainThread.postDelayed(Runnable runnable, long delayMs)` | Posts `runnable` to the main thread after `delayMs` milliseconds. |
| `MainThread.run(Runnable runnable)` | Runs `runnable` immediately if already on the main thread; otherwise posts it. |
| `MainThread.cancel(Runnable runnable)` | Removes pending posts of `runnable` from the main thread queue. |

## Usage

```java
import com.isaiahnoelpulidosalazar.inpsandroid.core.concurrent.MainThread;

Runnable refreshUi = () -> statusView.setText("Done");

// Safe from any background thread
MainThread.run(refreshUi);

// Schedule and optionally cancel delayed work
MainThread.postDelayed(refreshUi, 500L);
MainThread.cancel(refreshUi);
```
