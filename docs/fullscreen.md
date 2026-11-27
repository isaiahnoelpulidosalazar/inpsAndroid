# Fullscreen

Package: `com.isaiahnoelpulidosalazar.inpsandroid.ui`

`Fullscreen` toggles immersive fullscreen using `WindowInsetsControllerCompat` and `WindowCompat`. It saves each `Window`'s prior `decorFitsSystemWindows`, system bar visibility, and AppCompat `ActionBar` visibility in a `WeakHashMap` and restores them cleanly on `disable(Activity)`.

## API Reference

| Method | Description |
|---|---|
| `Fullscreen.enable(Activity activity)` | Captures prior window state, sets `decorFitsSystemWindows(false)`, hides status/navigation bars with transient swipe behavior, and hides the AppCompat `ActionBar` if present. |
| `Fullscreen.disable(Activity activity)` | Restores the captured `decorFitsSystemWindows`, system bar visibility, and `ActionBar` state. |
| `Fullscreen.reapplyOnWindowFocusChanged(Activity activity, boolean hasFocus)` | Re-hides system bars when window focus returns while fullscreen is active. |

## Usage

```java
import com.isaiahnoelpulidosalazar.inpsandroid.ui.Fullscreen;

Fullscreen.enable(this);
// Later...
Fullscreen.disable(this);

@Override
public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    Fullscreen.reapplyOnWindowFocusChanged(this, hasFocus);
}
```
