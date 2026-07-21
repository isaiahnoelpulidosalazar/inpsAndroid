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

/**
 * A custom {@link LinearLayout} that supports rounded corners.
 * Allows customization of the corner radius and background opacity (solid vs translucent)
 * via custom styleable attributes.
 */
public class RoundedLinearLayout extends LinearLayout {

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in.
     */
    public RoundedLinearLayout(Context context) {
        super(context);
        init(context, null);
    }

    /**
     * Constructor that is called when inflating a view from XML.
     *
     * @param context The Context the view is running in.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    public RoundedLinearLayout(Context context, @Nullable AttributeSet attrs) {
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
    public RoundedLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style from a theme attribute or style resource.
     *
     * @param context      The Context the view is running in.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style resource.
     * @param defStyleRes  A resource identifier of a style resource that supplies default values for the view.
     */
    public RoundedLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    /**
     * Initializes the layout's background, applying rounded corners and translucency settings
     * based on the XML attributes.
     *
     * @param context The Context the view is running in.
     * @param attrs   The attributes of the XML tag, or null.
     */
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
            float radius = a.getDimension(R.styleable.RoundedLinearLayout_cornerRadius, 8f);
            background.setCornerRadius(radius);
            a.recycle();
        }

        setBackground(background);
    }
}