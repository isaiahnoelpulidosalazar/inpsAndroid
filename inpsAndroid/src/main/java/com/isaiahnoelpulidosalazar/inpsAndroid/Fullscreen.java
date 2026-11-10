package com.isaiahnoelpulidosalazar.inpsAndroid;

import android.app.Activity;
import android.view.Window;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

/**
 * Immersive fullscreen toggle built on {@link WindowInsetsControllerCompat} (the
 * {@code SYSTEM_UI_FLAG_*} flags used in v1 are deprecated since API 30).
 *
 * <pre>{@code
 * Fullscreen.enable(this);   // hide bars; they reappear transiently on edge swipe
 * Fullscreen.disable(this);
 * }</pre>
 */
public final class Fullscreen {

    private Fullscreen() {
        throw new UnsupportedOperationException("Fullscreen cannot be instantiated");
    }

    /** Hides status/navigation bars (and the AppCompat action bar when present). */
    public static void enable(Activity activity) {
        Window window = activity.getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
        controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        controller.hide(WindowInsetsCompat.Type.systemBars());
        setActionBarVisible(activity, false);
    }

    /** Restores the system bars (and the AppCompat action bar when present). */
    public static void disable(Activity activity) {
        Window window = activity.getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, true);
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
        controller.show(WindowInsetsCompat.Type.systemBars());
        setActionBarVisible(activity, true);
    }

    private static void setActionBarVisible(Activity activity, boolean visible) {
        if (!(activity instanceof AppCompatActivity)) return;
        ActionBar bar = ((AppCompatActivity) activity).getSupportActionBar();
        if (bar == null) return; // v1 relied on catching an NPE here
        if (visible) bar.show(); else bar.hide();
    }
}
