package com.isaiahnoelpulidosalazar.inpsandroid.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;
import com.isaiahnoelpulidosalazar.inpsandroid.R;
import java.util.Objects;

/**
 * An {@link AppCompatImageView} that flips between a front and a back image like a card using
 * hardware-layered {@link android.view.ViewPropertyAnimator} animations and state restoration.
 *
 * <p>XML: {@code app:inpsReverseBackImage="true"} and {@code app:inpsFlipSpeed="slow|normal|fast"}.
 */
public class FlippableImageView extends AppCompatImageView {

  /** Animation speed: full 180-degree flip duration. */
  public enum Speed {
    /** 600ms full 180-degree flip duration. */
    SLOW(600),
    /** 300ms full 180-degree flip duration. */
    NORMAL(300),
    /** 150ms full 180-degree flip duration. */
    FAST(150);

    final long durationMs;

    Speed(long durationMs) {
      this.durationMs = durationMs;
    }
  }

  private static final float CAMERA_DISTANCE_FACTOR = 8000f;

  private Drawable frontImage;
  private Drawable backImage;
  private boolean flipped;
  private boolean showingBack;
  private boolean reverseBackImage;
  private Speed speed = Speed.NORMAL;

  /**
   * Programmatic constructor using default {@link Speed#NORMAL}.
   *
   * @param context Android context
   */
  public FlippableImageView(@NonNull Context context) {
    this(context, null);
  }

  /**
   * XML inflation constructor reading {@code app:inpsReverseBackImage} and {@code
   * app:inpsFlipSpeed}.
   *
   * @param context Android context
   * @param attrs XML attribute set
   */
  public FlippableImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  /**
   * Styled XML inflation constructor.
   *
   * @param context Android context
   * @param attrs XML attribute set
   * @param defStyleAttr default theme style attribute
   */
  @SuppressLint("CustomViewStyleable")
  public FlippableImageView(
      @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    // Moves the virtual camera back along Z so 3D perspective foreshortening during Y rotation is
    // subtle.
    setCameraDistance(CAMERA_DISTANCE_FACTOR * context.getResources().getDisplayMetrics().density);
    frontImage = getDrawable();
    if (attrs != null) {
      TypedArray a =
          context.obtainStyledAttributes(
              attrs, R.styleable.InpsFlippableImageView, defStyleAttr, 0);
      try {
        reverseBackImage =
            a.getBoolean(R.styleable.InpsFlippableImageView_inpsReverseBackImage, false);
        Speed[] speeds = Speed.values();
        int index =
            a.getInt(R.styleable.InpsFlippableImageView_inpsFlipSpeed, Speed.NORMAL.ordinal());
        speed = speeds[Math.max(0, Math.min(index, speeds.length - 1))];
      } finally {
        a.recycle();
      }
    }
  }

  // ---- configuration -------------------------------------------------------------------------

  /**
   * Configures the flip animation duration preset.
   *
   * @param speed desired {@link Speed} preset
   */
  public void setSpeed(@NonNull Speed speed) {
    this.speed = Objects.requireNonNull(speed, "speed");
  }

  /**
   * Returns the current flip animation speed preset.
   *
   * @return current {@link Speed}
   */
  public Speed getSpeed() {
    return speed;
  }

  /**
   * When {@code true} the back image is mirrored horizontally so it reads correctly after the
   * 180-degree rotation.
   *
   * @param reverseBackImage {@code true} to mirror the back image along X
   */
  public void setReverseBackImage(boolean reverseBackImage) {
    this.reverseBackImage = reverseBackImage;
    applySide();
  }

  /**
   * Sets the front image from a drawable resource ID.
   *
   * @param resourceId drawable resource identifier
   */
  public void setFrontImage(@DrawableRes int resourceId) {
    setFrontImage(AppCompatResources.getDrawable(getContext(), resourceId));
  }

  /**
   * Sets the front image from a {@link Bitmap} (or clears it when {@code null}).
   *
   * @param bitmap front bitmap, or {@code null}
   */
  public void setFrontBitmap(@Nullable Bitmap bitmap) {
    setFrontImage(bitmap == null ? null : new BitmapDrawable(getResources(), bitmap));
  }

  /**
   * Sets the front image and shows it immediately when the front side is visible.
   *
   * @param drawable front drawable, or {@code null}
   */
  public void setFrontImage(@Nullable Drawable drawable) {
    frontImage = drawable;
    if (!showingBack) applySide();
  }

  /**
   * Sets the back image from a drawable resource ID.
   *
   * @param resourceId drawable resource identifier
   */
  public void setBackImage(@DrawableRes int resourceId) {
    setBackImage(AppCompatResources.getDrawable(getContext(), resourceId));
  }

  /**
   * Sets the back image from a {@link Bitmap} (or clears it when {@code null}).
   *
   * @param bitmap back bitmap, or {@code null}
   */
  public void setBackBitmap(@Nullable Bitmap bitmap) {
    setBackImage(bitmap == null ? null : new BitmapDrawable(getResources(), bitmap));
  }

  /**
   * Sets the back image and shows it immediately when the back side is visible.
   *
   * @param drawable back drawable, or {@code null}
   */
  public void setBackImage(@Nullable Drawable drawable) {
    backImage = drawable;
    if (showingBack) applySide();
  }

  /**
   * Returns the currently configured front drawable.
   *
   * @return front drawable, or {@code null}
   */
  @Nullable
  public Drawable getFrontDrawable() {
    return frontImage;
  }

  /**
   * Returns the currently configured back drawable.
   *
   * @return back drawable, or {@code null}
   */
  @Nullable
  public Drawable getBackDrawable() {
    return backImage;
  }

  // ---- flipping ------------------------------------------------------------------------------

  /** Flips instantly without animation. */
  public void instantFlip() {
    animate().cancel();
    flipped = !flipped;
    setRotationY(flipped ? 180f : 0f);
    syncSide();
  }

  /** Animated flip. Calling again mid-flip reverses smoothly from the current angle. */
  public void flip() {
    flipped = !flipped;
    final float target = flipped ? 180f : 0f;
    long duration = (long) (speed.durationMs * Math.abs(target - getRotationY()) / 180f);
    animate().cancel();
    animate()
        .rotationY(target)
        .setDuration(duration)
        .withLayer()
        .setUpdateListener(animation -> syncSide())
        .withEndAction(
            () -> {
              syncSide();
              animate().setUpdateListener(null);
            })
        .start();
  }

  /**
   * Checks which face is currently targeted.
   *
   * @return {@code true} when flipped to the back side
   */
  public boolean isFlipped() {
    return flipped;
  }

  @Override
  protected Parcelable onSaveInstanceState() {
    Parcelable superState = super.onSaveInstanceState();
    SavedState state = new SavedState(superState);
    state.flipped = flipped;
    return state;
  }

  @Override
  protected void onRestoreInstanceState(Parcelable state) {
    if (!(state instanceof SavedState)) {
      super.onRestoreInstanceState(state);
      return;
    }
    SavedState saved = (SavedState) state;
    super.onRestoreInstanceState(saved.getSuperState());
    flipped = saved.flipped;
    showingBack = flipped;
    setRotationY(flipped ? 180f : 0f);
    applySide();
  }

  @Override
  protected void onDetachedFromWindow() {
    animate().cancel();
    super.onDetachedFromWindow();
  }

  /** Swaps the drawn side when the rotation passes the edge-on point (90 degrees). */
  private void syncSide() {
    boolean back = getRotationY() > 90f;
    if (back != showingBack) {
      showingBack = back;
      applySide();
    }
  }

  private void applySide() {
    setImageDrawable(showingBack ? backImage : frontImage);
    setScaleX(showingBack && reverseBackImage ? -1f : 1f);
  }

  private static final class SavedState extends BaseSavedState {
    boolean flipped;

    SavedState(Parcelable superState) {
      super(superState);
    }

    private SavedState(Parcel in) {
      super(in);
      flipped = in.readInt() == 1;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
      super.writeToParcel(out, flags);
      out.writeInt(flipped ? 1 : 0);
    }

    public static final Creator<SavedState> CREATOR =
        new Creator<SavedState>() {
          @Override
          public SavedState createFromParcel(Parcel in) {
            return new SavedState(in);
          }

          @Override
          public SavedState[] newArray(int size) {
            return new SavedState[size];
          }
        };
  }
}
