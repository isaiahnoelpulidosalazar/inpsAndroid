# Drawables

Package: `com.isaiahnoelpulidosalazar.inpsandroid.ui`

`Drawables` provides pixel-level comparison between a drawable resource and a `Bitmap`. Non-bitmap drawables (such as vectors or shapes) are rasterized at the target bitmap's dimensions before comparison.

## Hardware Bitmap Handling

- `Bitmap.Config.HARDWARE` or null-config bitmaps are copied to `Bitmap.Config.ARGB_8888` before pixel comparison
- Returns `false` if `context` or `bitmap` is `null`, the drawable resource cannot be loaded, or an `IllegalArgumentException` occurs

## Usage

```java
import com.isaiahnoelpulidosalazar.inpsandroid.ui.Drawables;

boolean matches = Drawables.equalsBitmap(context, R.drawable.ic_badge, capturedBitmap);
```
