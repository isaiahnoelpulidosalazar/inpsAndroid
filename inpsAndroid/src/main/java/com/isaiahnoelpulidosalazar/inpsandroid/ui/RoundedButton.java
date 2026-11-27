package com.isaiahnoelpulidosalazar.inpsandroid.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.isaiahnoelpulidosalazar.inpsandroid.R;

/**
 * An {@link AppCompatButton} with a rounded primary-colour background, a contrast-aware ripple,
 * and automatic black/white text colour derived from background luminance. Respects an explicit
 * {@code android:background} in XML when provided. Optional {@code app:inpsCornerRadius}.
 */
public class RoundedButton extends AppCompatButton {

    /**
     * Programmatic constructor creating a rounded button with default primary styling.
     *
     * @param context Android context
     */
    public RoundedButton(@NonNull Context context) {
        super(context);
        init(null, 0);
    }

    /**
     * XML inflation constructor reading {@code app:inpsCornerRadius} if present.
     *
     * @param context Android context
     * @param attrs   XML attribute set
     */
    public RoundedButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    /**
     * Styled XML inflation constructor.
     *
     * @param context      Android context
     * @param attrs        XML attribute set
     * @param defStyleAttr default theme style attribute
     */
    public RoundedButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(@Nullable AttributeSet attrs, int defStyleAttr) {
        RoundedViewHelper.apply(
                this,
                attrs,
                defStyleAttr,
                R.styleable.RoundedButton,
                R.styleable.RoundedButton_inpsCornerRadius,
                -1,
                false,
                RoundedViewHelper.KEEP_DRAWABLE_RADIUS,
                false,
                true,
                true
        );
    }
}
