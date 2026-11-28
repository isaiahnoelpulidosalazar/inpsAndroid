package com.isaiahnoelpulidosalazar.inpsandroid.ui;

import android.app.Activity;
import android.view.View;
import android.view.Window;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import java.util.WeakHashMap;

/**
 * Immersive fullscreen toggle built on {@link WindowInsetsControllerCompat}.
 *
 * <pre>{@code
 * Fullscreen.enable(this);   // hide bars; they reappear transiently on edge swipe
 * Fullscreen.disable(this);  // restores prior decorFitsSystemWindows and bar visibility
 * }</pre>
 */
public final class Fullscreen {

  private static final class State {
    final boolean wasDecorFitsSystemWindows;
    final boolean wasBarsVisible;
    final boolean wasActionBarShowing;

    State(boolean wasDecorFitsSystemWindows, boolean wasBarsVisible, boolean wasActionBarShowing) {
      this.wasDecorFitsSystemWindows = wasDecorFitsSystemWindows;
      this.wasBarsVisible = wasBarsVisible;
      this.wasActionBarShowing = wasActionBarShowing;
    }
  }

  private static final WeakHashMap<Window, State> SAVED_STATES = new WeakHashMap<>();

  private Fullscreen() {
    throw new UnsupportedOperationException("No instances");
  }

  /**
   * Hides status and navigation bars (and the AppCompat action bar when present), saving the
   * window's prior {@code decorFitsSystemWindows} and bar visibility for {@link
   * #disable(Activity)}.
   *
   * @param activity host activity whose window enters immersive fullscreen
   */
  public static void enable(Activity activity) {
    Window window = activity.getWindow();
    if (!SAVED_STATES.containsKey(window)) {
      View decor = window.getDecorView();
      @SuppressWarnings("deprecation")
      boolean wasDecorFits =
          (decor.getWindowSystemUiVisibility()
                  & (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                      | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION))
              == 0;
      WindowInsetsCompat rootInsets = ViewCompat.getRootWindowInsets(decor);
      boolean wasBarsVisible =
          rootInsets == null
              || rootInsets.isVisible(WindowInsetsCompat.Type.statusBars())
              || rootInsets.isVisible(WindowInsetsCompat.Type.navigationBars());
      boolean wasActionBarShowing = isActionBarShowing(activity);
      SAVED_STATES.put(window, new State(wasDecorFits, wasBarsVisible, wasActionBarShowing));
    }

    WindowCompat.setDecorFitsSystemWindows(window, false);
    hideSystemBars(window);
    setActionBarVisible(activity, false);
  }

  /**
   * Restores the {@code decorFitsSystemWindows} value, system bar visibility, and AppCompat action
   * bar state captured when {@link #enable(Activity)} was called.
   *
   * @param activity host activity whose prior window chrome should be restored
   */
  public static void disable(Activity activity) {
    Window window = activity.getWindow();
    State previous = SAVED_STATES.remove(window);
    boolean decorFits = previous == null || previous.wasDecorFitsSystemWindows;
    boolean showBars = previous == null || previous.wasBarsVisible;
    boolean showActionBar = previous == null || previous.wasActionBarShowing;

    WindowCompat.setDecorFitsSystemWindows(window, decorFits);
    WindowInsetsControllerCompat controller =
        WindowCompat.getInsetsController(window, window.getDecorView());
    if (showBars) {
      controller.show(WindowInsetsCompat.Type.systemBars());
    } else {
      controller.hide(WindowInsetsCompat.Type.systemBars());
    }
    setActionBarVisible(activity, showActionBar);
  }

  /**
   * Optional helper for {@link Activity#onWindowFocusChanged(boolean)} that re-hides the system
   * bars when window focus returns while fullscreen is active.
   *
   * @param activity host activity receiving focus change
   * @param hasFocus current window focus state
   */
  public static void reapplyOnWindowFocusChanged(Activity activity, boolean hasFocus) {
    if (!hasFocus) return;
    Window window = activity.getWindow();
    if (SAVED_STATES.containsKey(window)) {
      hideSystemBars(window);
    }
  }

  private static void hideSystemBars(Window window) {
    WindowInsetsControllerCompat controller =
        WindowCompat.getInsetsController(window, window.getDecorView());
    controller.setSystemBarsBehavior(
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
    controller.hide(WindowInsetsCompat.Type.systemBars());
  }

  private static boolean isActionBarShowing(Activity activity) {
    if (!(activity instanceof AppCompatActivity)) return false;
    ActionBar bar = ((AppCompatActivity) activity).getSupportActionBar();
    return bar != null && bar.isShowing();
  }

  private static void setActionBarVisible(Activity activity, boolean visible) {
    if (!(activity instanceof AppCompatActivity)) return;
    ActionBar bar = ((AppCompatActivity) activity).getSupportActionBar();
    if (bar == null) return;
    if (visible) {
      bar.show();
    } else {
      bar.hide();
    }
  }
}
