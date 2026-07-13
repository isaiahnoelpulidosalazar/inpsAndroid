package com.isaiahnoelpulidosalazar.inpsAndroid;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

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

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        int cornerRadius = 16;
        int backgroundColor = Color.BLUE;

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundedButton);
            cornerRadius = a.getDimensionPixelSize(R.styleable.RoundedButton_buttonCornerRadius, cornerRadius);
            backgroundColor = a.getColor(R.styleable.RoundedButton_buttonBackgroundColor, backgroundColor);
            a.recycle();
        }

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(cornerRadius);
        shape.setColor(backgroundColor);

        setBackground(shape);
    }
}