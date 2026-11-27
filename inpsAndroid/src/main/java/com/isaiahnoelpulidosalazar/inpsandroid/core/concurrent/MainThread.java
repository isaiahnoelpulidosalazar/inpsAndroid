package com.isaiahnoelpulidosalazar.inpsandroid.core.concurrent;

import android.os.Handler;
import android.os.Looper;

/**
 * Posts work to the Android main thread using a shared {@link Handler} bound explicitly to
 * {@link Looper#getMainLooper()}.
 */
public final class MainThread {

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    private MainThread() {
        throw new UnsupportedOperationException("No instances");
    }

    /** @return {@code true} if the calling thread is the main thread. */
    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /** Posts {@code runnable} to the main thread's queue. */
    public static void post(Runnable runnable) {
        HANDLER.post(runnable);
    }

    /** Posts {@code runnable} to the main thread's queue after {@code delayMs} milliseconds. */
    public static void postDelayed(Runnable runnable, long delayMs) {
        HANDLER.postDelayed(runnable, delayMs);
    }

    /** Runs {@code runnable} immediately when already on the main thread, otherwise posts it. */
    public static void run(Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            HANDLER.post(runnable);
        }
    }

    /** Removes pending posts of {@code runnable}. */
    public static void cancel(Runnable runnable) {
        HANDLER.removeCallbacks(runnable);
    }
}
