package com.isaiahnoelpulidosalazar.inpsAndroid.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.appcompat.content.res.AppCompatResources;

/** Drawable / Bitmap helpers. */
public final class Drawables {

    private Drawables() {
        throw new UnsupportedOperationException("Drawables cannot be instantiated");
    }

    /**
     * Pixel-level comparison of a drawable resource with a bitmap.
     *
     * <p>Non-bitmap drawables (vectors, shapes) are rasterised at the bitmap's size first
     * (v1 returned {@code false} for them unconditionally).</p>
     */
    public static boolean equalsBitmap(Context context, int resourceId, Bitmap bitmap) {
        if (context == null || bitmap == null) return false;
        Drawable drawable = AppCompatResources.getDrawable(context, resourceId);
        if (drawable == null) return false;

        if (drawable instanceof BitmapDrawable) {
            Bitmap b = ((BitmapDrawable) drawable).getBitmap();
            return b != null && b.sameAs(bitmap);
        }
        Bitmap.Config config = bitmap.getConfig() != null ? bitmap.getConfig() : Bitmap.Config.ARGB_8888;
        Bitmap rendered = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), config);
        try {
            Canvas canvas = new Canvas(rendered);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return rendered.sameAs(bitmap);
        } finally {
            rendered.recycle();
        }
    }
}
