package com.isaiahnoelpulidosalazar.inpsandroid.core.concurrent;

import android.os.Handler;
import android.os.Looper;

/**
 * Posts work to the Android main (UI) thread using a shared {@link Handler} bound explicitly to
 * {@link Looper#getMainLooper()}.
 *
 * <p>Unlike allocating {@code new Handler()} without a looper, this class is safe to invoke from
 * any background worker thread.</p>
 */
public final class MainThread {

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    private MainThread() {
        throw new UnsupportedOperationException("No instances");
    }

    /**
     * Checks whether the current thread is the Android main thread.
     *
     * @return {@code true} if the calling thread owns {@link Looper#getMainLooper()}
     */
    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * Enqueues {@code runnable} to run on the main thread's message queue.
     *
     * @param runnable task to execute on the main thread
     */
    public static void post(Runnable runnable) {
        HANDLER.post(runnable);
    }

    /**
     * Enqueues {@code runnable} to run on the main thread after a delay.
     *
     * @param runnable task to execute on the main thread
     * @param delayMs  delay in milliseconds before execution
     */
    public static void postDelayed(Runnable runnable, long delayMs) {
        HANDLER.postDelayed(runnable, delayMs);
    }

    /**
     * Runs {@code runnable} immediately if already on the main thread, or posts it to the main
     * looper otherwise.
     *
     * @param runnable task to execute on the main thread
     */
    public static void run(Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            HANDLER.post(runnable);
        }
    }

    /**
     * Removes any pending posts of {@code runnable} from the main thread queue.
     *
     * @param runnable previously posted task reference to cancel
     */
    public static void cancel(Runnable runnable) {
        HANDLER.removeCallbacks(runnable);
    }
}
