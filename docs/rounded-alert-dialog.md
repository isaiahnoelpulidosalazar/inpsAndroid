# RoundedAlertDialog

`RoundedAlertDialog` is a builder-style rounded dialog with a title, optional scrollable content, and two action buttons.

## Purpose & Scope

The purpose of this component is to provide:
- A rounded card design (24dp corners over a transparent window) for alert dialogs
- A scrollable content area capped at 480dp tall
- Configuration methods that may be called in any order, such as setting a click listener before its button text

The buttons stay hidden until `setupLeftButton` or `setupRightButton` is called, and the content area stays hidden until `addView` is called.

## API Reference

### RoundedAlertDialog(Context context)

Creates a dialog bound to the given context.

```java
RoundedAlertDialog dialog = new RoundedAlertDialog(context);
```

### RoundedAlertDialog setTitle(CharSequence title)

Sets the header title.

```java
dialog.setTitle("Confirm Action");
```

### RoundedAlertDialog addView(View view)

Sets the scrollable content. It **replaces** any previously added view, since a `ScrollView` holds one child.

```java
dialog.addView(customView);
```

### RoundedAlertDialog setCancelable(boolean cancelable)

Controls whether the dialog can be dismissed by back press or outside touch.

```java
dialog.setCancelable(false);
```

### RoundedAlertDialog setupLeftButton(String text) / setupLeftButton(String text, int color)

Shows the left button with the given label and an optional text color.

```java
dialog.setupLeftButton("Cancel", Color.GRAY);
```

### RoundedAlertDialog setupRightButton(String text) / setupRightButton(String text, int color)

Shows the right button with the given label and an optional text color.

```java
dialog.setupRightButton("OK", Color.RED);
```

### RoundedAlertDialog setupLeftButtonOnClick(View.OnClickListener listener)

Attaches a click listener to the left button.

```java
dialog.setupLeftButtonOnClick(v -> dialog.dismiss());
```

### RoundedAlertDialog setupRightButtonOnClick(View.OnClickListener listener)

Attaches a click listener to the right button.

```java
dialog.setupRightButtonOnClick(v -> doSubmit());
```

### void show()

Builds the dialog once and shows it. Calling it while already showing does nothing.

```java
dialog.show();
```

### boolean isShowing()

Returns `true` while the dialog is visible.

```java
boolean visible = dialog.isShowing();
```

### void dismiss()

Dismisses the dialog. Safe to call before `show()`.

```java
dialog.dismiss();
```

### void hide()

**Deprecated.** Calls `cancel()`, so cancel listeners fire. Use `dismiss()` instead.

```java
dialog.hide();
```
