package com.isaiahnoelpulidosalazar.inpsandroid.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.appcompat.content.res.AppCompatResources;

/** Drawable and Bitmap comparison helpers. */
public final class Drawables {

    private Drawables() {
        throw new UnsupportedOperationException("No instances");
    }

    /**
     * Pixel-level comparison of a drawable resource with a bitmap. Non-bitmap drawables (vectors,
     * shapes) are rasterised at the bitmap's size before comparison.
     *
     * <p>{@link Bitmap.Config#HARDWARE} or null-config bitmaps are copied to
     * {@link Bitmap.Config#ARGB_8888} before reading pixels. Returns {@code false} on null inputs
     * or {@link IllegalArgumentException}.</p>
     */
    public static boolean equalsBitmap(Context context, int resourceId, Bitmap bitmap) {
        if (context == null || bitmap == null) return false;
        Drawable drawable = AppCompatResources.getDrawable(context, resourceId);
        if (drawable == null) return false;

        Bitmap comparable = bitmap;
        Bitmap copied = null;
        try {
            Bitmap.Config config = bitmap.getConfig();
            if (config == null || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && config == Bitmap.Config.HARDWARE)) {
                copied = bitmap.copy(Bitmap.Config.ARGB_8888, false);
                if (copied == null) return false;
                comparable = copied;
            }

            if (drawable instanceof BitmapDrawable) {
                Bitmap b = ((BitmapDrawable) drawable).getBitmap();
                return b != null && b.sameAs(comparable);
            }

            Bitmap.Config renderConfig = comparable.getConfig() != null
                    ? comparable.getConfig()
                    : Bitmap.Config.ARGB_8888;
            Bitmap rendered = Bitmap.createBitmap(comparable.getWidth(), comparable.getHeight(), renderConfig);
            try {
                Canvas canvas = new Canvas(rendered);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                return rendered.sameAs(comparable);
            } finally {
                rendered.recycle();
            }
        } catch (IllegalArgumentException e) {
            return false;
        } finally {
            if (copied != null) {
                copied.recycle();
            }
        }
    }
}
