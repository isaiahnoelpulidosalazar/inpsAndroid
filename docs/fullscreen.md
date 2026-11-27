# Fullscreen

The `Fullscreen` helper provides one-line activation and deactivation of immersive fullscreen mode using `WindowInsetsControllerCompat` (the `SYSTEM_UI_FLAG_*` flags used in v1 are deprecated since API 30).

## Purpose & Scope

The purpose of this utility is to provide:
- One-line toggling of immersive mode, with bars reappearing transiently on edge swipe
- Coordinated hiding and restoration of the status bar, navigation bar, and AppCompat action bar (when present)

`enable` makes the window edge-to-edge (`setDecorFitsSystemWindows(false)`), so handle insets yourself in layouts that must avoid the bars.

## API Reference

### static void enable(Activity activity)

Hides the system bars and the action bar. Any `Activity` is accepted; the action bar is only touched for an `AppCompatActivity`.

```java
Fullscreen.enable(this);
```

### static void disable(Activity activity)

Restores the system bars and the action bar.

```java
Fullscreen.disable(this);
```
