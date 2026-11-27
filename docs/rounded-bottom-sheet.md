# RoundedBottomSheet

**Package:** `com.isaiahnoelpulidosalazar.inpsandroid.ui`

Builder-style modal bottom sheet backed by `AppCompatDialog` with top-rounded corners (`24dp` default), an optional pill drag handle, interactive swipe-to-dismiss gestures, edge-to-edge navigation bar and IME inset padding, scrollable body content, and dual `RoundedButton` action buttons.

## Features

- **Top-Rounded Surface & Clipping:** Uses `ViewOutlineProvider.BACKGROUND` with `clipToOutline(true)` and configurable top corner radius (`setCornerRadiusDp(float)`).
- **Interactive Swipe-to-Dismiss:** Dragging downward on the handle or header translates the sheet smoothly and cancels when dragged past `28%` of its height or flicked downward (`setDraggable(boolean)`).
- **System Insets Aware:** Automatically applies `WindowInsetsCompat.Type.navigationBars()` and `ime()` bottom padding so action buttons and input fields remain clear of gesture bars and keyboards.
- **Parent-Safe Custom Content:** `addView(View)` detaches the incoming view from any existing parent before placing it inside the constrained `ScrollView`. Quick text-only sheets can use `setMessage(CharSequence)`.
- **Dual Actions:** Hidden until configured via `setupSecondaryButton(...)` (outlined ripple) and `setupPrimaryButton(...)` (solid primary `RoundedButton`).

## Usage

```java
RoundedBottomSheet sheet = new RoundedBottomSheet(context)
        .setTitle("Export Notes")
        .setSubtitle("Choose your preferred format")
        .addView(customOptionsLayout);

sheet.setupSecondaryButton("Cancel")
        .setupSecondaryButtonOnClick(v -> sheet.dismiss());

sheet.setupPrimaryButton("Export")
        .setupPrimaryButtonOnClick(v -> {
            runExport();
            sheet.dismiss();
        });

sheet.show();
```

## API Reference

| Method | Description |
|---|---|
| `setTitle(CharSequence \| @StringRes int)` | Sets the sheet header title (passing `null`/empty hides it). |
| `setSubtitle(CharSequence \| @StringRes int)` | Sets an optional subtitle beneath the title. |
| `setMessage(CharSequence \| @StringRes int)` | Convenience method that displays padded text in the scrollable body. |
| `addView(View)` | Detaches `view` from any prior parent and sets it as the scrollable content. |
| `setShowDragHandle(boolean)` / `isDragHandleVisible()` | Shows or hides the top drag handle pill (default `true`). |
| `setDraggable(boolean)` / `isDraggable()` | Enables or disables swipe-to-dismiss on the handle/header (default `true`). |
| `setCornerRadiusDp(float)` | Adjusts top-left and top-right surface corner radius in dp (default `24dp`). |
| `setCancelable(boolean)` / `setCanceledOnTouchOutside(boolean)` | Controls back-press, drag, and scrim dismissal both before and after `show()`. |
| `setupPrimaryButton(...)` / `setupSecondaryButton(...)` | Configures button labels, optional text colors, and visibility. |
| `setupPrimaryButtonOnClick(...)` / `setupSecondaryButtonOnClick(...)` | Attaches click listeners to the sheet action buttons. |
| `show()` / `dismiss()` / `dismissImmediately()` / `isShowing()` | Shows or closes the sheet (animating vertically when laid out). |
