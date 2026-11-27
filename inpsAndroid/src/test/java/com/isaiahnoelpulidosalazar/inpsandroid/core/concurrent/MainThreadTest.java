package com.isaiahnoelpulidosalazar.inpsandroid.core.concurrent;

import android.os.Looper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class MainThreadTest {

    @Test
    public void testIsMainThreadAndRunImmediate() {
        assertTrue(MainThread.isMainThread());

        AtomicBoolean ran = new AtomicBoolean(false);
        MainThread.run(() -> ran.set(true));
        assertTrue(ran.get());
    }

    @Test
    public void testPostDelayedAndCancel() {
        AtomicInteger count = new AtomicInteger(0);
        Runnable cancelled = count::incrementAndGet;
        Runnable kept = count::incrementAndGet;

        MainThread.postDelayed(cancelled, 100);
        MainThread.postDelayed(kept, 100);
        MainThread.cancel(cancelled);

        Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(200));
        assertEquals(1, count.get());
    }

    @Test
    public void testPostFromBackgroundThread() throws InterruptedException {
        AtomicBoolean ranOnMain = new AtomicBoolean(false);
        AtomicBoolean bgIsMain = new AtomicBoolean(true);

        Thread bg = new Thread(() -> {
            bgIsMain.set(MainThread.isMainThread());
            MainThread.run(() -> ranOnMain.set(MainThread.isMainThread()));
        });
        bg.start();
        bg.join(2000);

        assertFalse(bgIsMain.get());
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        assertTrue(ranOnMain.get());
    }
}
