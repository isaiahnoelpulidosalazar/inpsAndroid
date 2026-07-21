package com.isaiahnoelpulidosalazar.inpsAndroid;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import com.isaiahnoelpulidosalazar.inpsandroid.R;

/**
 * A custom {@link AppCompatEditText} that features a default translucent, 
 * rounded background. Supports custom corner radius configuration via XML.
 */
public class RoundedEditText extends AppCompatEditText {

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in.
     */
    public RoundedEditText(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    /**
     * Constructor that is called when inflating a view from XML.
     *
     * @param context The Context the view is running in.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    public RoundedEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style from a theme attribute.
     *
     * @param context      The Context the view is running in.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style resource.
     */
    public RoundedEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * Initializes the edit text's background, loading the default translucent shape
     * drawable and applying any custom corner radius provided.
     *
     * @param context The Context the view is running in.
     * @param attrs   The attributes of the XML tag, or null.
     */
    void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        GradientDrawable background = (GradientDrawable) ContextCompat.getDrawable(
                context,
                R.drawable.generic_rounded_translucent_background
        ).mutate();

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundedEditText);
            if (a.hasValue(R.styleable.RoundedEditText_cornerRadius)) {
                float radius = a.getDimension(R.styleable.RoundedEditText_cornerRadius, 0f);
                background.setCornerRadius(radius);
            }
            a.recycle();
        }

        setBackground(background);
    }
}