package com.isaiahnoelpulidosalazar.inpsandroid.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.isaiahnoelpulidosalazar.inpsandroid.R;

/**
 * A {@link LinearLayout} with a rounded background that clips child views to its rounded outline.
 * XML attributes: {@code app:inpsCornerRadius} (default 8dp) and {@code app:inpsIsTranslucent}
 * (default false).
 */
public class RoundedLinearLayout extends LinearLayout {

    public RoundedLinearLayout(@NonNull Context context) { this(context, null); }

    public RoundedLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs) { this(context, attrs, 0); }

    public RoundedLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public RoundedLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }

    private void init(@Nullable AttributeSet attrs, int defStyleAttr) {
        RoundedViewHelper.apply(
                this,
                attrs,
                defStyleAttr,
                R.styleable.RoundedLinearLayout,
                R.styleable.RoundedLinearLayout_inpsCornerRadius,
                R.styleable.RoundedLinearLayout_inpsIsTranslucent,
                false,
                RoundedViewHelper.dp(getContext(), RoundedViewHelper.DEFAULT_LAYOUT_RADIUS_DP),
                true,
                false,
                false
        );
    }
}
