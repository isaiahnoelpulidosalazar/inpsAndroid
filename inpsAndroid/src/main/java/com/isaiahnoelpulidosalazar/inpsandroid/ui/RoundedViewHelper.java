package com.isaiahnoelpulidosalazar.inpsandroid.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import com.isaiahnoelpulidosalazar.inpsandroid.R;
import java.util.Objects;

/** Shared attribute parsing, background, ripple, and outline helper for {@code Rounded*} views. */
final class RoundedViewHelper {

  /** Sentinel radius instructing {@link #createBackground} to preserve the XML drawable radius. */
  static final float KEEP_DRAWABLE_RADIUS = -1f;

  /** Default corner radius in dp applied to rounded layout containers. */
  static final float DEFAULT_LAYOUT_RADIUS_DP = 8f;

  private RoundedViewHelper() {
    throw new UnsupportedOperationException("No instances");
  }

  /**
   * Parses XML styling attributes, builds a rounded background (with optional ripple and contrast
   * text coloring), and configures outline clipping on {@code view}.
   */
  static void apply(
      View view,
      @Nullable AttributeSet attrs,
      int defStyleAttr,
      int[] styleable,
      int cornerRadiusAttrIndex,
      int isTranslucentAttrIndex,
      boolean defaultTranslucent,
      float defaultRadiusPx,
      boolean clipToOutline,
      boolean applyRipple,
      boolean respectXmlBackground) {
    Context context = view.getContext();
    if (respectXmlBackground && hasExplicitXmlBackground(context, attrs)) {
      if (clipToOutline) {
        view.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        view.setClipToOutline(true);
      }
      return;
    }

    boolean translucent = defaultTranslucent;
    float radius = defaultRadiusPx;
    if (attrs != null) {
      TypedArray a = context.obtainStyledAttributes(attrs, styleable, defStyleAttr, 0);
      try {
        if (isTranslucentAttrIndex >= 0) {
          translucent = a.getBoolean(isTranslucentAttrIndex, defaultTranslucent);
        }
        radius = a.getDimension(cornerRadiusAttrIndex, defaultRadiusPx);
      } finally {
        a.recycle();
      }
    }

    GradientDrawable background = createBackground(context, translucent, radius);
    if (applyRipple) {
      ColorStateList fill = background.getColor();
      int fillColor = fill != null ? fill.getDefaultColor() : Color.BLACK;
      boolean isDark = ColorUtils.calculateLuminance(fillColor) < 0.5;
      int ripple = isDark ? Color.argb(64, 255, 255, 255) : Color.argb(31, 0, 0, 0);

      GradientDrawable mask =
          (GradientDrawable)
              Objects.requireNonNull(background.getConstantState()).newDrawable().mutate();
      view.setBackground(new RippleDrawable(ColorStateList.valueOf(ripple), background, mask));

      if (view instanceof TextView) {
        ((TextView) view).setTextColor(isDark ? Color.WHITE : Color.BLACK);
      }
    } else {
      view.setBackground(background);
    }

    if (clipToOutline) {
      view.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
      view.setClipToOutline(true);
    }
  }

  private static boolean hasExplicitXmlBackground(Context context, @Nullable AttributeSet attrs) {
    if (attrs == null) return false;
    TypedArray a = context.obtainStyledAttributes(attrs, new int[] {android.R.attr.background});
    try {
      return a.hasValue(0);
    } finally {
      a.recycle();
    }
  }

  /** Creates a mutable rounded {@link GradientDrawable} from the theme-aware library shape. */
  static GradientDrawable createBackground(Context context, boolean translucent, float radiusPx) {
    int res =
        translucent
            ? R.drawable.inps_rounded_translucent_background
            : R.drawable.inps_rounded_primary_background;
    GradientDrawable d =
        (GradientDrawable) Objects.requireNonNull(ContextCompat.getDrawable(context, res)).mutate();
    if (radiusPx >= 0f) d.setCornerRadius(radiusPx);
    return d;
  }

  /** Converts display-independent pixels ({@code dp}) to raw pixels for {@code context}. */
  static float dp(Context context, float dp) {
    return dp * context.getResources().getDisplayMetrics().density;
  }
}
