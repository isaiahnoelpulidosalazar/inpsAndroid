package com.isaiahnoelpulidosalazar.inpsandroid.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.OvershootInterpolator;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.isaiahnoelpulidosalazar.inpsandroid.R;

import java.util.Objects;

/**
 * Modern rounded checkbox component implementing {@link Checkable}, supporting selectable visual
 * styles ({@link Style#CARD}, {@link Style#PILL}, {@link Style#MINIMAL}) and a configurable
 * bounce animation toggle ({@code app:inpsBounceAnimation} / {@link #setBounceAnimationEnabled}).
 *
 * <pre>{@code
 * <com.isaiahnoelpulidosalazar.inpsandroid.ui.RoundedCheckBox
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     android:text="Enable bounce animation"
 *     android:checked="true"
 *     app:inpsCheckStyle="card"
 *     app:inpsBounceAnimation="true"
 *     app:inpsCornerRadius="14dp" />
 * }</pre>
 */
public class RoundedCheckBox extends LinearLayout implements Checkable {

    /** Visual presentation styles for {@link RoundedCheckBox}. */
    public enum Style {
        /** Rounded card container with border and soft primary tint when checked. */
        CARD,
        /** Full capsule pill container with border and soft primary tint when checked. */
        PILL,
        /** Borderless minimal row with ripple feedback and rounded checkbox indicator. */
        MINIMAL
    }

    /** Listener notified when the checked state changes. */
    public interface OnCheckedChangeListener {
        void onCheckedChanged(RoundedCheckBox checkBox, boolean isChecked);
    }

    private static final float DEFAULT_CORNER_RADIUS_DP = 12f;
    private static final float PILL_RADIUS_DP = 999f;
    private static final long BOUNCE_DURATION_MS = 260L;

    private final CheckIndicatorView indicatorView;
    private final AppCompatTextView labelView;

    private Style style = Style.CARD;
    private float cornerRadiusPx;
    private boolean hasExplicitCornerRadius;
    private boolean bounceAnimationEnabled = true;
    private boolean checked;
    private boolean broadcasting;
    private OnCheckedChangeListener checkedChangeListener;

    public RoundedCheckBox(@NonNull Context context) {
        this(context, null);
    }

    public RoundedCheckBox(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundedCheckBox(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setClickable(true);
        setFocusable(true);
        setMinimumHeight(Math.round(RoundedViewHelper.dp(context, 48f)));

        int hPad = Math.round(RoundedViewHelper.dp(context, 14f));
        int vPad = Math.round(RoundedViewHelper.dp(context, 12f));
        setPadding(hPad, vPad, hPad, vPad);

        cornerRadiusPx = RoundedViewHelper.dp(context, DEFAULT_CORNER_RADIUS_DP);

        int primaryColor = resolveThemeColor(context, androidx.appcompat.R.attr.colorPrimary, Color.rgb(29, 78, 216));
        int textPrimary = resolveThemeColor(context, android.R.attr.textColorPrimary, Color.rgb(15, 23, 42));
        int outlineColor = ColorUtils.setAlphaComponent(textPrimary, 96);

        indicatorView = new CheckIndicatorView(context, primaryColor, outlineColor);
        int boxSize = Math.round(RoundedViewHelper.dp(context, 22f));
        LayoutParams boxParams = new LayoutParams(boxSize, boxSize);
        addView(indicatorView, boxParams);

        labelView = new AppCompatTextView(context);
        labelView.setTextColor(textPrimary);
        labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
        labelView.setSingleLine(false);
        labelView.setMaxLines(2);
        labelView.setEllipsize(TextUtils.TruncateAt.END);
        LayoutParams textParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
        textParams.setMarginStart(Math.round(RoundedViewHelper.dp(context, 12f)));
        addView(labelView, textParams);

        if (attrs != null) {
            TypedArray std = context.obtainStyledAttributes(
                    attrs, new int[]{android.R.attr.text, android.R.attr.checked});
            try {
                CharSequence initialText = std.getText(0);
                if (initialText != null) {
                    labelView.setText(initialText);
                }
                checked = std.getBoolean(1, false);
            } finally {
                std.recycle();
            }

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundedCheckBox, defStyleAttr, 0);
            try {
                bounceAnimationEnabled = a.getBoolean(R.styleable.RoundedCheckBox_inpsBounceAnimation, true);
                Style[] styles = Style.values();
                int idx = a.getInt(R.styleable.RoundedCheckBox_inpsCheckStyle, Style.CARD.ordinal());
                style = styles[Math.max(0, Math.min(idx, styles.length - 1))];
                if (a.hasValue(R.styleable.RoundedCheckBox_inpsCornerRadius)) {
                    hasExplicitCornerRadius = true;
                    cornerRadiusPx = a.getDimension(R.styleable.RoundedCheckBox_inpsCornerRadius, cornerRadiusPx);
                }
            } finally {
                a.recycle();
            }
        }

        applyVisualState(false);
    }

    @Override
    public boolean performClick() {
        toggle();
        return super.performClick();
    }

    @Override
    public void toggle() {
        setChecked(!checked, bounceAnimationEnabled);
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    /** Sets the checked state immediately without triggering the bounce animation. */
    @Override
    public void setChecked(boolean checked) {
        setChecked(checked, false);
    }

    /**
     * Sets the checked state and optionally runs the bounce animation when
     * {@link #isBounceAnimationEnabled()} is {@code true}.
     */
    public void setChecked(boolean checked, boolean animate) {
        boolean changed = this.checked != checked;
        this.checked = checked;
        applyVisualState(animate && bounceAnimationEnabled);
        if (changed && !broadcasting && checkedChangeListener != null) {
            broadcasting = true;
            try {
                checkedChangeListener.onCheckedChanged(this, this.checked);
            } finally {
                broadcasting = false;
            }
        }
    }

    /** Enables or disables the overshoot bounce animation when toggled. */
    public void setBounceAnimationEnabled(boolean enabled) {
        this.bounceAnimationEnabled = enabled;
        if (!enabled) {
            cancelBounce();
        }
    }

    /** @return {@code true} if bounce animations play when toggled. */
    public boolean isBounceAnimationEnabled() {
        return bounceAnimationEnabled;
    }

    /** Sets the container presentation style ({@link Style#CARD}, {@link Style#PILL}, or {@link Style#MINIMAL}). */
    public void setStyle(@NonNull Style style) {
        this.style = Objects.requireNonNull(style, "style");
        applyVisualState(false);
    }

    public Style getStyle() {
        return style;
    }

    /** Sets corner radius in dp for {@link Style#CARD} and {@link Style#MINIMAL}. */
    public void setCornerRadiusDp(float dp) {
        hasExplicitCornerRadius = true;
        cornerRadiusPx = RoundedViewHelper.dp(getContext(), dp);
        applyVisualState(false);
    }

    public void setText(@Nullable CharSequence text) {
        labelView.setText(text);
    }

    public void setText(@StringRes int resId) {
        labelView.setText(resId);
    }

    public CharSequence getText() {
        return labelView.getText();
    }

    public TextView getLabelView() {
        return labelView;
    }

    public void setOnCheckedChangeListener(@Nullable OnCheckedChangeListener listener) {
        this.checkedChangeListener = listener;
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return "android.widget.CheckBox";
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setChecked(checked);
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setCheckable(true);
        info.setChecked(checked);
    }

    @Override
    protected void onDetachedFromWindow() {
        cancelBounce();
        super.onDetachedFromWindow();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState state = new SavedState(superState);
        state.checked = checked;
        state.bounceEnabled = bounceAnimationEnabled;
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
        bounceAnimationEnabled = saved.bounceEnabled;
        setChecked(saved.checked, false);
    }

    private void applyVisualState(boolean animateBounce) {
        indicatorView.setChecked(checked);
        updateContainerBackground();
        if (animateBounce) {
            playBounce();
        }
    }

    private void updateContainerBackground() {
        Context context = getContext();
        int primaryColor = resolveThemeColor(context, androidx.appcompat.R.attr.colorPrimary, Color.rgb(29, 78, 216));
        int bgColor = resolveThemeColor(context, android.R.attr.colorBackground, Color.WHITE);
        int textColor = resolveThemeColor(context, android.R.attr.textColorPrimary, Color.rgb(15, 23, 42));
        int rippleColor = resolveThemeColor(
                context,
                androidx.appcompat.R.attr.colorControlHighlight,
                ColorUtils.setAlphaComponent(primaryColor, 36)
        );

        float radiusPx;
        if (style == Style.PILL) {
            radiusPx = RoundedViewHelper.dp(context, PILL_RADIUS_DP);
        } else if (style == Style.MINIMAL && !hasExplicitCornerRadius) {
            radiusPx = RoundedViewHelper.dp(context, 8f);
        } else {
            radiusPx = cornerRadiusPx;
        }

        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(radiusPx);

        if (style == Style.MINIMAL) {
            shape.setColor(checked ? ColorUtils.setAlphaComponent(primaryColor, 18) : Color.TRANSPARENT);
            setBackground(new RippleDrawable(ColorStateList.valueOf(rippleColor), shape, createMask(radiusPx)));
            return;
        }

        int fillColor = checked
                ? ColorUtils.compositeColors(ColorUtils.setAlphaComponent(primaryColor, 26), bgColor)
                : bgColor;
        int strokeColor = checked
                ? primaryColor
                : ColorUtils.setAlphaComponent(textColor, 40);
        int strokeWidthPx = Math.max(1, Math.round(RoundedViewHelper.dp(context, checked ? 1.5f : 1f)));

        shape.setColor(fillColor);
        shape.setStroke(strokeWidthPx, strokeColor);
        setBackground(new RippleDrawable(ColorStateList.valueOf(rippleColor), shape, createMask(radiusPx)));
    }

    private static GradientDrawable createMask(float radiusPx) {
        GradientDrawable mask = new GradientDrawable();
        mask.setColor(Color.WHITE);
        mask.setCornerRadius(radiusPx);
        return mask;
    }

    private void playBounce() {
        indicatorView.animate().cancel();
        indicatorView.setScaleX(0.76f);
        indicatorView.setScaleY(0.76f);
        indicatorView.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(BOUNCE_DURATION_MS)
                .setInterpolator(new OvershootInterpolator(2.8f))
                .start();

        if (style != Style.MINIMAL) {
            animate().cancel();
            setScaleX(0.97f);
            setScaleY(0.97f);
            animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(210L)
                    .setInterpolator(new OvershootInterpolator(1.6f))
                    .start();
        }
    }

    private void cancelBounce() {
        indicatorView.animate().cancel();
        indicatorView.setScaleX(1f);
        indicatorView.setScaleY(1f);
        animate().cancel();
        setScaleX(1f);
        setScaleY(1f);
    }

    private static int resolveThemeColor(Context context, int attr, int fallback) {
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(attr, tv, true)) {
            if (tv.type >= TypedValue.TYPE_FIRST_COLOR_INT && tv.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                return tv.data;
            }
            if (tv.resourceId != 0) {
                return ContextCompat.getColor(context, tv.resourceId);
            }
        }
        return fallback;
    }

    /** Custom-drawn rounded square checkbox indicator with contrast-safe checkmark stroke. */
    private static final class CheckIndicatorView extends View {
        private final Paint boxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint checkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF boxRect = new RectF();
        private final Path checkPath = new Path();
        private final float cornerRadiusPx;
        private final float strokeWidthPx;
        private final int primaryColor;
        private final int uncheckedOutlineColor;
        private boolean isBoxChecked;

        CheckIndicatorView(Context context, int primaryColor, int uncheckedOutlineColor) {
            super(context);
            this.primaryColor = primaryColor;
            this.uncheckedOutlineColor = uncheckedOutlineColor;
            this.cornerRadiusPx = RoundedViewHelper.dp(context, 6f);
            this.strokeWidthPx = RoundedViewHelper.dp(context, 2f);

            boxPaint.setStyle(Paint.Style.FILL);
            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setStrokeWidth(strokeWidthPx);

            int checkmarkColor = ColorUtils.calculateLuminance(primaryColor) < 0.5 ? Color.WHITE : Color.BLACK;
            checkPaint.setStyle(Paint.Style.STROKE);
            checkPaint.setColor(checkmarkColor);
            checkPaint.setStrokeWidth(RoundedViewHelper.dp(context, 2.2f));
            checkPaint.setStrokeCap(Paint.Cap.ROUND);
            checkPaint.setStrokeJoin(Paint.Join.ROUND);
        }

        void setChecked(boolean checked) {
            if (this.isBoxChecked != checked) {
                this.isBoxChecked = checked;
                invalidate();
            }
        }

        @Override
        protected void onDraw(@NonNull Canvas canvas) {
            super.onDraw(canvas);
            float halfStroke = strokeWidthPx / 2f;
            boxRect.set(halfStroke, halfStroke, getWidth() - halfStroke, getHeight() - halfStroke);

            if (isBoxChecked) {
                boxPaint.setColor(primaryColor);
                canvas.drawRoundRect(boxRect, cornerRadiusPx, cornerRadiusPx, boxPaint);

                float w = getWidth();
                float h = getHeight();
                checkPath.reset();
                checkPath.moveTo(w * 0.26f, h * 0.52f);
                checkPath.lineTo(w * 0.43f, h * 0.69f);
                checkPath.lineTo(w * 0.74f, h * 0.33f);
                canvas.drawPath(checkPath, checkPaint);
            } else {
                strokePaint.setColor(uncheckedOutlineColor);
                canvas.drawRoundRect(boxRect, cornerRadiusPx, cornerRadiusPx, strokePaint);
            }
        }
    }

    private static final class SavedState extends BaseSavedState {
        boolean checked;
        boolean bounceEnabled;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            checked = in.readInt() == 1;
            bounceEnabled = in.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(checked ? 1 : 0);
            out.writeInt(bounceEnabled ? 1 : 0);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
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
