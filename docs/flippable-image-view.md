# FlippableImageView

`FlippableImageView` is an `AppCompatImageView` that flips between a front and a back image like a card, using a hardware-accelerated `ViewPropertyAnimator`.

## Purpose & Scope

The purpose of this component is to provide:
- Smooth 3D Y-axis flips with three speeds, interruptible mid-animation (flipping again reverses from the current angle)
- Dual image management for any `Drawable`, `Bitmap`, or drawable resource
- Optional mirroring of the back image so it reads correctly after rotation, applied as a view scale with no bitmap copies

The displayed side is derived from the live rotation and switches at the edge-on point (90 degrees). Animations are cancelled when the view detaches from the window.

## XML Attributes

```xml
<com.isaiahnoelpulidosalazar.inpsAndroid.FlippableImageView
    android:layout_width="120dp"
    android:layout_height="160dp"
    android:src="@drawable/card_front"
    app:reverseBackImage="true"
    app:flipSpeed="fast" />   <!-- slow | normal | fast -->
```

## API Reference

### FlippableImageView(Context context) / (Context, AttributeSet) / (Context, AttributeSet, int)

Standard constructors. An `android:src` set in XML becomes the front image. The v1 constructors that accepted `Speed` and `boolean` arguments still exist but are deprecated.

```java
FlippableImageView fiv = new FlippableImageView(context);
```

### enum Speed

Full 180-degree flip duration: `SLOW` = 600 ms, `NORMAL` = 300 ms (default), `FAST` = 150 ms.

```java
fiv.setSpeed(FlippableImageView.Speed.FAST);
```

### void setSpeed(Speed speed) / Speed getSpeed()

Sets or reads the animation speed.

```java
FlippableImageView.Speed s = fiv.getSpeed();
```

### void setFrontImage(int resourceId) / (Bitmap) / (Drawable)

Sets the front image, shown immediately when the front side is visible.

```java
fiv.setFrontImage(R.drawable.card_front);
```

### void setBackImage(int resourceId) / (Bitmap) / (Drawable)

Sets the back image, shown immediately when the back side is visible.

```java
fiv.setBackImage(R.drawable.card_back);
```

### void setReverseBackImage(boolean reverseBackImage)

Mirrors the back image horizontally. Applies immediately.

```java
fiv.setReverseBackImage(true);
```

### Drawable getFrontDrawable() / Drawable getBackDrawable()

Returns the current front or back drawable, or `null`.

```java
Drawable back = fiv.getBackDrawable();
```

### Bitmap getFrontImage() / Bitmap getBackImage()

**Deprecated.** Return the bitmap (back image un-mirrored), or `null` for non-bitmap drawables. Use the drawable getters instead.

```java
Bitmap bmp = fiv.getFrontImage();
```

### void flip()

Animated 180-degree flip around the Y axis. Calling it mid-flip reverses smoothly from the current angle.

```java
fiv.flip();
```

### void instantFlip()

Toggles sides immediately with no animation, swapping the image.

```java
fiv.instantFlip();
```

### boolean isFlipped()

Returns `true` once a flip to the back side has been requested or completed (the requested state, not the instantaneous angle).

```java
boolean flipped = fiv.isFlipped();
```
