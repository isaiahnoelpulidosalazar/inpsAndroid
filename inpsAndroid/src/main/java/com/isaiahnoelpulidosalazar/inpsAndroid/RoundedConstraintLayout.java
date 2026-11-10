package com.isaiahnoelpulidosalazar.inpsAndroid;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.isaiahnoelpulidosalazar.inpsandroid.R;

/**
 * A {@link ConstraintLayout} with a rounded background. XML attributes: {@code app:cornerRadius}
 * (default 8dp) and {@code app:isTranslucent} (default false).
 */
public class RoundedConstraintLayout extends ConstraintLayout {

    public RoundedConstraintLayout(@NonNull Context context) { this(context, null); }

    public RoundedConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs) { this(context, attrs, 0); }

    public RoundedConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public RoundedConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        boolean translucent = false;
        float radius = RoundedViewHelper.dp(context, RoundedViewHelper.DEFAULT_LAYOUT_RADIUS_DP);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundedConstraintLayout, defStyleAttr, 0);
            try {
                translucent = a.getBoolean(R.styleable.RoundedConstraintLayout_isTranslucent, false);
                radius = a.getDimension(R.styleable.RoundedConstraintLayout_cornerRadius, radius);
            } finally {
                a.recycle();
            }
        }
        setBackground(RoundedViewHelper.createBackground(context, translucent, radius));
    }
}
