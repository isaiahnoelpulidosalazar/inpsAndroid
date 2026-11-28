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

    /**
     * Programmatic constructor creating a rounded {@link LinearLayout} with an 8dp corner radius.
     *
     * @param context Android context
     */
    public RoundedLinearLayout(@NonNull Context context) {
        this(context, null);
    }

    /**
     * XML inflation constructor reading {@code app:inpsCornerRadius} and {@code app:inpsIsTranslucent}.
     *
     * @param context Android context
     * @param attrs   XML attribute set
     */
    public RoundedLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Styled XML inflation constructor.
     *
     * @param context      Android context
     * @param attrs        XML attribute set
     * @param defStyleAttr default theme style attribute
     */
    public RoundedLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    /**
     * Full styled XML inflation constructor with explicit style resource.
     *
     * @param context      Android context
     * @param attrs        XML attribute set
     * @param defStyleAttr default theme style attribute
     * @param defStyleRes  default style resource
     */
    public RoundedLinearLayout(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr,
            int defStyleRes
    ) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }

    private void init(@Nullable AttributeSet attrs, int defStyleAttr) {
        RoundedViewHelper.apply(
                this,
                attrs,
                defStyleAttr,
                R.styleable.InpsRoundedLinearLayout,
                R.styleable.InpsRoundedLinearLayout_inpsCornerRadius,
                R.styleable.InpsRoundedLinearLayout_inpsIsTranslucent,
                false,
                RoundedViewHelper.dp(getContext(), RoundedViewHelper.DEFAULT_LAYOUT_RADIUS_DP),
                true,
                false,
                false
        );
    }
}
