package com.isaiahnoelpulidosalazar.inpsAndroid;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;

import com.isaiahnoelpulidosalazar.inpsandroid.R;

import java.util.Objects;

/**
 * An {@link AppCompatImageView} that flips between a front and a back image like a card.
 *
 * <p>v2 drives the flip with {@code ViewPropertyAnimator} (hardware accelerated, vsync-paced,
 * interruptible) instead of re-posting a new {@code Handler} runnable per frame. The displayed side
 * is derived from the live rotation, so flipping again mid-animation simply reverses smoothly.
 * Any {@link Drawable} is accepted (v1 crashed with a ClassCastException on non-bitmap drawables).</p>
 *
 * <p>XML: {@code app:reverseBackImage="true"} and {@code app:flipSpeed="slow|normal|fast"}.</p>
 */
public class FlippableImageView extends AppCompatImageView {

    /** Animation speed: full 180-degree flip duration. */
    public enum Speed {
        SLOW(600), NORMAL(300), FAST(150);

        final long durationMs;

        Speed(long durationMs) { this.durationMs = durationMs; }
    }

    private static final float CAMERA_DISTANCE_FACTOR = 8000f;

    private Drawable frontImage;
    private Drawable backImage;
    private boolean flipped;          // logical state (target of the latest flip)
    private boolean showingBack;      // what is currently drawn
    private boolean reverseBackImage;
    private Speed speed = Speed.NORMAL;

    public FlippableImageView(@NonNull Context context) { this(context, null); }

    public FlippableImageView(@NonNull Context context, @Nullable AttributeSet attrs) { this(context, attrs, 0); }

    public FlippableImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // Keeps the card from being clipped by the parent while it rotates around Y.
        setCameraDistance(CAMERA_DISTANCE_FACTOR * context.getResources().getDisplayMetrics().density);
        frontImage = getDrawable(); // picks up android:src from XML, if any
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlippableImageView, defStyleAttr, 0);
            try {
                reverseBackImage = a.getBoolean(R.styleable.FlippableImageView_reverseBackImage, false);
                Speed[] speeds = Speed.values();
                int index = a.getInt(R.styleable.FlippableImageView_flipSpeed, Speed.NORMAL.ordinal());
                speed = speeds[Math.max(0, Math.min(index, speeds.length - 1))];
            } finally {
                a.recycle();
            }
        }
    }

    // ---- v1 constructors, kept for source compatibility ---------------------------------------

    /** @deprecated Use the standard constructors plus {@link #setSpeed(Speed)}. */
    // @Deprecated public FlippableImageView(@NonNull Context c, Speed s) { this(c); setSpeed(s); }
    /** @deprecated see {@link #FlippableImageView(Context, Speed)} */
    // @Deprecated public FlippableImageView(@NonNull Context c, Speed s, @Nullable AttributeSet a) { this(c, a); setSpeed(s); }
    /** @deprecated see {@link #FlippableImageView(Context, Speed)} */
    // @Deprecated public FlippableImageView(@NonNull Context c, Speed s, @Nullable AttributeSet a, int d) { this(c, a, d); setSpeed(s); }
    /** @deprecated Use {@link #setReverseBackImage(boolean)}. */
    // @Deprecated public FlippableImageView(@NonNull Context c, boolean r) { this(c); setReverseBackImage(r); }
    /** @deprecated see {@link #FlippableImageView(Context, boolean)} */
    // @Deprecated public FlippableImageView(@NonNull Context c, boolean r, @Nullable AttributeSet a) { this(c, a); setReverseBackImage(r); }
    /** @deprecated see {@link #FlippableImageView(Context, boolean)} */
    // @Deprecated public FlippableImageView(@NonNull Context c, boolean r, @Nullable AttributeSet a, int d) { this(c, a, d); setReverseBackImage(r); }
    /** @deprecated see {@link #FlippableImageView(Context, boolean)} */
    // @Deprecated public FlippableImageView(@NonNull Context c, boolean r, Speed s) { this(c); setReverseBackImage(r); setSpeed(s); }
    /** @deprecated see {@link #FlippableImageView(Context, boolean)} */
    // @Deprecated public FlippableImageView(@NonNull Context c, boolean r, Speed s, @Nullable AttributeSet a) { this(c, a); setReverseBackImage(r); setSpeed(s); }
    /** @deprecated see {@link #FlippableImageView(Context, boolean)} */
    // @Deprecated public FlippableImageView(@NonNull Context c, boolean r, Speed s, @Nullable AttributeSet a, int d) { this(c, a, d); setReverseBackImage(r); setSpeed(s); }

    // ---- configuration -------------------------------------------------------------------------

    public void setSpeed(@NonNull Speed speed) {
        this.speed = Objects.requireNonNull(speed, "speed");
    }

    public Speed getSpeed() { return speed; }

    /**
     * When {@code true} the back image is mirrored so it reads correctly after the 180-degree
     * rotation. Applies immediately (v1 only affected images assigned afterwards). The mirroring is
     * a view scale, so no bitmap copies are made.
     */
    public void setReverseBackImage(boolean reverseBackImage) {
        this.reverseBackImage = reverseBackImage;
        applySide();
    }

    public void setFrontImage(@DrawableRes int resourceId) {
        setFrontImage(AppCompatResources.getDrawable(getContext(), resourceId));
    }

    public void setFrontImage(@Nullable Bitmap bitmap) {
        setFrontImage(bitmap == null ? null : new BitmapDrawable(getResources(), bitmap));
    }

    /** Sets the front image and shows it immediately when the front side is visible. */
    public void setFrontImage(@Nullable Drawable drawable) {
        frontImage = drawable;
        if (!showingBack) applySide();
    }

    public void setBackImage(@DrawableRes int resourceId) {
        setBackImage(AppCompatResources.getDrawable(getContext(), resourceId));
    }

    public void setBackImage(@Nullable Bitmap bitmap) {
        setBackImage(bitmap == null ? null : new BitmapDrawable(getResources(), bitmap));
    }

    /** Sets the back image and shows it immediately when the back side is visible. */
    public void setBackImage(@Nullable Drawable drawable) {
        backImage = drawable;
        if (showingBack) applySide();
    }

    @Nullable public Drawable getFrontDrawable() { return frontImage; }

    @Nullable public Drawable getBackDrawable() { return backImage; }

    /** @deprecated Use {@link #getFrontDrawable()}. Returns {@code null} for non-bitmap drawables. */
    @Deprecated @Nullable public Bitmap getFrontImage() { return toBitmap(frontImage); }

    /** @deprecated Use {@link #getBackDrawable()}. Returns the un-mirrored bitmap, or {@code null}. */
    @Deprecated @Nullable public Bitmap getBackImage() { return toBitmap(backImage); }

    // ---- flipping ------------------------------------------------------------------------------

    /** Flips instantly, without animation (v1 forgot to swap the image here). */
    public void instantFlip() {
        animate().cancel();
        flipped = !flipped;
        setRotationY(flipped ? 180f : 0f);
        syncSide();
    }

    /** Animated flip. Calling again mid-flip reverses from the current angle. */
    public void flip() {
        flipped = !flipped;
        final float target = flipped ? 180f : 0f;
        long duration = (long) (speed.durationMs * Math.abs(target - getRotationY()) / 180f);
        animate().cancel();
        animate().rotationY(target)
                .setDuration(duration)
                .setUpdateListener(animation -> syncSide())
                .withEndAction(() -> {
                    syncSide();
                    animate().setUpdateListener(null);
                })
                .start();
    }

    /** @return {@code true} once a flip to the back side has been requested/completed. */
    public boolean isFlipped() { return flipped; }

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

    @Nullable
    private static Bitmap toBitmap(@Nullable Drawable d) {
        return d instanceof BitmapDrawable ? ((BitmapDrawable) d).getBitmap() : null;
    }
}
