# FlippableImageView

Package: `com.isaiahnoelpulidosalazar.inpsandroid.ui`

`FlippableImageView` is an `AppCompatImageView` that animates a 3D Y-axis card flip between a front image and a back image using hardware-layered `ViewPropertyAnimator` transitions.

## XML Attributes

| Attribute | Values | Default |
|---|---|---|
| `app:inpsReverseBackImage` | `boolean` | `false` |
| `app:inpsFlipSpeed` | `slow` (600 ms), `normal` (300 ms), `fast` (150 ms) | `normal` |

## API Highlights

- `setFrontImage(@DrawableRes int)` / `setFrontImage(@Nullable Drawable)` / `setFrontBitmap(@Nullable Bitmap)`
- `setBackImage(@DrawableRes int)` / `setBackImage(@Nullable Drawable)` / `setBackBitmap(@Nullable Bitmap)`
- `flip()` (animated, reverses smoothly if called mid-flip) and `instantFlip()`
- `setSpeed(Speed)`, `getSpeed()`, `setReverseBackImage(boolean)`, `isFlipped()`
- Preserves `flipped` state across configuration changes via `onSaveInstanceState` / `onRestoreInstanceState`

## Usage

```xml
<com.isaiahnoelpulidosalazar.inpsandroid.ui.FlippableImageView
    android:id="@+id/card"
    android:layout_width="96dp"
    android:layout_height="96dp"
    android:src="@drawable/card_front"
    app:inpsFlipSpeed="normal"
    app:inpsReverseBackImage="false" />
```

```java
card.setBackImage(R.drawable.card_back);
card.setOnClickListener(v -> card.flip());
```
