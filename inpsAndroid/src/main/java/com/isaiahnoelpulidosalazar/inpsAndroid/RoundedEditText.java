package com.isaiahnoelpulidosalazar.inpsAndroid;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.isaiahnoelpulidosalazar.inpsandroid.R;

/**
 * An {@link AppCompatEditText} with a translucent, pill-shaped background.
 * Optional {@code app:cornerRadius}.
 */
public class RoundedEditText extends AppCompatEditText {

    public RoundedEditText(@NonNull Context context) { super(context); init(context, null, 0); }

    public RoundedEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public RoundedEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        float radius = RoundedViewHelper.KEEP_DRAWABLE_RADIUS;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundedEditText, defStyleAttr, 0);
            try {
                radius = a.getDimension(R.styleable.RoundedEditText_cornerRadius, radius);
            } finally {
                a.recycle();
            }
        }
        setBackground(RoundedViewHelper.createBackground(context, true, radius));
    }
}
