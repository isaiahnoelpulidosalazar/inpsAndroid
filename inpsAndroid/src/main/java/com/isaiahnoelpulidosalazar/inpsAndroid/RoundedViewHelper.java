package com.isaiahnoelpulidosalazar.inpsAndroid;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;

import androidx.core.content.ContextCompat;

import com.isaiahnoelpulidosalazar.inpsandroid.R;

import java.util.Objects;

/** Shared background factory for the Rounded* views (removes four copies of the same init code). */
final class RoundedViewHelper {

    /** Pass as the radius to keep the shape drawable's own corner radius. */
    static final float KEEP_DRAWABLE_RADIUS = -1f;

    /** Default corner radius for the layout widgets (v1 used 8 <i>pixels</i> when inflated from XML). */
    static final float DEFAULT_LAYOUT_RADIUS_DP = 8f;

    private RoundedViewHelper() { }

    static GradientDrawable createBackground(Context context, boolean translucent, float radiusPx) {
        int res = translucent
                ? R.drawable.generic_rounded_translucent_background
                : R.drawable.generic_rounded_primary_background;
        GradientDrawable d = (GradientDrawable) Objects.requireNonNull(ContextCompat.getDrawable(context, res)).mutate();
        if (radiusPx >= 0f) d.setCornerRadius(radiusPx);
        return d;
    }

    static float dp(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
