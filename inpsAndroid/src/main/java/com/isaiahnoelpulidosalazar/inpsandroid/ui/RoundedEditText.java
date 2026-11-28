package com.isaiahnoelpulidosalazar.inpsandroid.ui;

import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import com.isaiahnoelpulidosalazar.inpsandroid.R;

/**
 * An {@link AppCompatEditText} with a translucent, pill-shaped background. Optional {@code
 * app:inpsCornerRadius}.
 */
public class RoundedEditText extends AppCompatEditText {

  /**
   * Programmatic constructor creating a translucent rounded input field.
   *
   * @param context Android context
   */
  public RoundedEditText(@NonNull Context context) {
    super(context);
    init(null, 0);
  }

  /**
   * XML inflation constructor reading {@code app:inpsCornerRadius} if present.
   *
   * @param context Android context
   * @param attrs XML attribute set
   */
  public RoundedEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(attrs, 0);
  }

  /**
   * Styled XML inflation constructor.
   *
   * @param context Android context
   * @param attrs XML attribute set
   * @param defStyleAttr default theme style attribute
   */
  public RoundedEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(attrs, defStyleAttr);
  }

  private void init(@Nullable AttributeSet attrs, int defStyleAttr) {
    RoundedViewHelper.apply(
        this,
        attrs,
        defStyleAttr,
        R.styleable.InpsRoundedEditText,
        R.styleable.InpsRoundedEditText_inpsCornerRadius,
        -1,
        true,
        RoundedViewHelper.KEEP_DRAWABLE_RADIUS,
        false,
        false,
        false);
  }
}
