package com.isaiahnoelpulidosalazar.inpsAndroid;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.isaiahnoelpulidosalazar.inpsandroid.R;

public class RoundedButton extends AppCompatButton {

    public RoundedButton(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public RoundedButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RoundedButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        GradientDrawable background = (GradientDrawable) ContextCompat.getDrawable(
                context,
                R.drawable.generic_rounded_primary_background
        ).mutate();

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundedButton);
            if (a.hasValue(R.styleable.RoundedButton_cornerRadius)) {
                float radius = a.getDimension(R.styleable.RoundedButton_cornerRadius, 0f);
                background.setCornerRadius(radius);
            }
            a.recycle();
        }

        int backgroundColor = Color.BLACK;
        ColorStateList colorStateList = background.getColor();
        if (colorStateList != null) {
            backgroundColor = colorStateList.getDefaultColor();
        }

        int rippleColor;
        if (ColorUtils.calculateLuminance(backgroundColor) < 0.5) {
            rippleColor = Color.argb(64, 255, 255, 255);
        } else {
            rippleColor = Color.argb(31, 0, 0, 0);
        }

        GradientDrawable mask = (GradientDrawable) background.getConstantState().newDrawable().mutate();

        RippleDrawable rippleDrawable = new RippleDrawable(
                ColorStateList.valueOf(rippleColor),
                background,
                mask
        );

        setBackground(rippleDrawable);
    }
}