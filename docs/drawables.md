# Drawables

The `Drawables` utility provides drawable and bitmap helpers. It replaces `Check.equalDrawableAndBitmap`.

## Purpose & Scope

The purpose of this utility is to provide:
- Pixel-level verification of a drawable resource against a `Bitmap`
- Support for non-bitmap drawables (vectors, shapes), which are rasterised at the bitmap's size before comparison

## API Reference

### static boolean equalsBitmap(Context context, int resourceId, Bitmap bitmap)

Compares a drawable resource with a bitmap using `Bitmap.sameAs`. Returns `false` if the context or bitmap is `null`, or if the resource cannot be loaded.

```java
boolean match = Drawables.equalsBitmap(context, R.drawable.logo, bitmap);
```
