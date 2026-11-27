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

    public RoundedButton(@NonNull Context context) {
        super(context);
        init(null, 0);
    }

    public RoundedButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

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
