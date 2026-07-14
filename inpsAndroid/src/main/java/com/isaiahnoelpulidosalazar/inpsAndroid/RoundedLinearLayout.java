package com.isaiahnoelpulidosalazar.inpsAndroid;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.isaiahnoelpulidosalazar.inpsandroid.R;

public class RoundedLinearLayout extends LinearLayout {
    public RoundedLinearLayout(Context context) {
        super(context);
        init(context, null);
    }

    public RoundedLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RoundedLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public RoundedLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        GradientDrawable background = (GradientDrawable) ContextCompat.getDrawable(
                context,
                R.drawable.generic_rounded_primary_background
        ).mutate();

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundedLinearLayout);
            if (a.hasValue(R.styleable.RoundedLinearLayout_isTranslucent)) {
                boolean isTranslucent = a.getBoolean(R.styleable.RoundedLinearLayout_isTranslucent, false);
                background = (GradientDrawable) ContextCompat.getDrawable(
                        context,
                        isTranslucent ? R.drawable.generic_rounded_translucent_background : R.drawable.generic_rounded_primary_background
                ).mutate();
            }
            if (a.hasValue(R.styleable.RoundedLinearLayout_cornerRadius)) {
                float radius = a.getDimension(R.styleable.RoundedLinearLayout_cornerRadius, 0f);
                background.setCornerRadius(radius);
            }
            a.recycle();
        }

        setBackground(background);
    }
}
