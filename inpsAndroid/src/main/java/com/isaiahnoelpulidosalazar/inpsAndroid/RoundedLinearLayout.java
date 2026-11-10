package com.isaiahnoelpulidosalazar.inpsAndroid;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.isaiahnoelpulidosalazar.inpsandroid.R;

/**
 * A {@link LinearLayout} with a rounded background. XML attributes: {@code app:cornerRadius}
 * (default 8dp) and {@code app:isTranslucent} (default false).
 */
public class RoundedLinearLayout extends LinearLayout {

    public RoundedLinearLayout(@NonNull Context context) { this(context, null); }

    public RoundedLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs) { this(context, attrs, 0); }

    public RoundedLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public RoundedLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        boolean translucent = false;
        float radius = RoundedViewHelper.dp(context, RoundedViewHelper.DEFAULT_LAYOUT_RADIUS_DP);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundedLinearLayout, defStyleAttr, 0);
            try {
                translucent = a.getBoolean(R.styleable.RoundedLinearLayout_isTranslucent, false);
                radius = a.getDimension(R.styleable.RoundedLinearLayout_cornerRadius, radius);
            } finally {
                a.recycle();
            }
        }
        setBackground(RoundedViewHelper.createBackground(context, translucent, radius));
    }
}
