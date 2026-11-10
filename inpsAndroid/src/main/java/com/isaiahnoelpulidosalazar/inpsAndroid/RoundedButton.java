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
import androidx.core.graphics.ColorUtils;

import com.isaiahnoelpulidosalazar.inpsandroid.R;

import java.util.Objects;

/**
 * An {@link AppCompatButton} with a rounded primary-colour background and a ripple whose colour
 * is chosen from the background luminance. Optional {@code app:cornerRadius}.
 */
public class RoundedButton extends AppCompatButton {

    public RoundedButton(@NonNull Context context) { super(context); init(context, null, 0); }

    public RoundedButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public RoundedButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        float radius = RoundedViewHelper.KEEP_DRAWABLE_RADIUS;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundedButton, defStyleAttr, 0);
            try {
                radius = a.getDimension(R.styleable.RoundedButton_cornerRadius, radius);
            } finally {
                a.recycle();
            }
        }
        GradientDrawable background = RoundedViewHelper.createBackground(context, false, radius);

        ColorStateList fill = background.getColor();
        int fillColor = fill != null ? fill.getDefaultColor() : Color.BLACK;
        int ripple = ColorUtils.calculateLuminance(fillColor) < 0.5
                ? Color.argb(64, 255, 255, 255)
                : Color.argb(31, 0, 0, 0);

        GradientDrawable mask = (GradientDrawable) Objects.requireNonNull(background.getConstantState())
                .newDrawable().mutate();
        setBackground(new RippleDrawable(ColorStateList.valueOf(ripple), background, mask));
    }
}
