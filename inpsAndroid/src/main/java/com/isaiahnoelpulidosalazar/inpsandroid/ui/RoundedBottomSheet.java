package com.isaiahnoelpulidosalazar.inpsandroid.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.isaiahnoelpulidosalazar.inpsandroid.R;

import java.util.Objects;

/**
 * Builder-style modal bottom sheet with top-rounded corners, optional drag handle, interactive
 * swipe-to-dismiss gesture, edge-to-edge inset padding, scrollable body content, and dual
 * {@link RoundedButton} actions.
 *
 * <pre>{@code
 * RoundedBottomSheet sheet = new RoundedBottomSheet(context)
 *         .setTitle("Quick Actions")
 *         .setSubtitle("Select an option or swipe down to dismiss")
 *         .setMessage("Custom views via addView() detach from any prior parent automatically.");
 * sheet.setupSecondaryButton("Cancel").setupSecondaryButtonOnClick(v -> sheet.dismiss());
 * sheet.setupPrimaryButton("Confirm").setupPrimaryButtonOnClick(v -> sheet.dismiss());
 * sheet.show();
 * }</pre>
 */
public class RoundedBottomSheet {

    private static final float DEFAULT_CORNER_RADIUS_DP = 24f;
    private static final float DIM_AMOUNT = 0.45f;
    private static final float DISMISS_DRAG_FRACTION = 0.28f;
    private static final float DISMISS_VELOCITY_THRESHOLD_PX = 1200f;
    private static final long ENTER_ANIMATION_MS = 240L;
    private static final long EXIT_ANIMATION_MS = 200L;

    private final Context context;
    private final AppCompatDialog dialog;
    private final View scrimView;
    private final View sheetContainer;
    private final View handleArea;
    private final TextView titleView;
    private final TextView subtitleView;
    private final ScrollView scrollView;
    private final LinearLayout actionsContainer;
    private final RoundedButton secondaryButton;
    private final RoundedButton primaryButton;

    private boolean cancelable = true;
    private boolean canceledOnTouchOutside = true;
    private boolean draggable = true;
    private boolean dismissing;
    private DialogInterface.OnDismissListener externalDismissListener;
    private DialogInterface.OnCancelListener externalCancelListener;

    public RoundedBottomSheet(@NonNull Context context) {
        this.context = Objects.requireNonNull(context, "context");
        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.inps_rounded_bottom_sheet_layout, null);

        scrimView = rootView.findViewById(R.id.inps_rounded_bottom_sheet_scrim);
        sheetContainer = rootView.findViewById(R.id.inps_rounded_bottom_sheet_container);
        handleArea = rootView.findViewById(R.id.inps_rounded_bottom_sheet_handle_area);
        titleView = rootView.findViewById(R.id.inps_rounded_bottom_sheet_title);
        subtitleView = rootView.findViewById(R.id.inps_rounded_bottom_sheet_subtitle);
        scrollView = rootView.findViewById(R.id.inps_rounded_bottom_sheet_scroll);
        actionsContainer = rootView.findViewById(R.id.inps_rounded_bottom_sheet_actions);
        secondaryButton = rootView.findViewById(R.id.inps_rounded_bottom_sheet_secondary_button);
        primaryButton = rootView.findViewById(R.id.inps_rounded_bottom_sheet_primary_button);

        sheetContainer.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        sheetContainer.setClipToOutline(true);
        styleSecondaryButtonOutline();
        applyBottomWindowInsets();

        dialog = new AppCompatDialog(context);
        dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(rootView);
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        dialog.setOnDismissListener(d -> {
            dismissing = false;
            sheetContainer.setTranslationY(0f);
            if (externalDismissListener != null) {
                externalDismissListener.onDismiss(d);
            }
        });
        dialog.setOnCancelListener(d -> {
            if (externalCancelListener != null) {
                externalCancelListener.onCancel(d);
            }
        });

        scrimView.setOnClickListener(v -> {
            if (cancelable && canceledOnTouchOutside) {
                animateAndClose(true);
            }
        });

        DragDismissTouchListener dragListener = new DragDismissTouchListener();
        handleArea.setOnTouchListener(dragListener);
        titleView.setOnTouchListener(dragListener);
        subtitleView.setOnTouchListener(dragListener);
    }

    /** Sets the header title. Passing {@code null} or empty hides the title view. */
    public RoundedBottomSheet setTitle(@Nullable CharSequence title) {
        if (title == null || title.length() == 0) {
            titleView.setText(null);
            titleView.setVisibility(View.GONE);
        } else {
            titleView.setText(title);
            titleView.setVisibility(View.VISIBLE);
        }
        return this;
    }

    /** Sets the header title from a string resource. */
    public RoundedBottomSheet setTitle(@StringRes int resId) {
        return setTitle(context.getString(resId));
    }

    /** Sets the optional header subtitle. Passing {@code null} or empty hides the subtitle view. */
    public RoundedBottomSheet setSubtitle(@Nullable CharSequence subtitle) {
        if (subtitle == null || subtitle.length() == 0) {
            subtitleView.setText(null);
            subtitleView.setVisibility(View.GONE);
        } else {
            subtitleView.setText(subtitle);
            subtitleView.setVisibility(View.VISIBLE);
        }
        return this;
    }

    /** Sets the optional header subtitle from a string resource. */
    public RoundedBottomSheet setSubtitle(@StringRes int resId) {
        return setSubtitle(context.getString(resId));
    }

    /**
     * Convenience helper that displays a styled body message inside the scrollable content region.
     * Passing {@code null} clears and hides the content region.
     */
    public RoundedBottomSheet setMessage(@Nullable CharSequence message) {
        if (message == null || message.length() == 0) {
            scrollView.removeAllViews();
            scrollView.setVisibility(View.GONE);
            return this;
        }
        TextView messageView = new TextView(context);
        int horizontalPad = Math.round(RoundedViewHelper.dp(context, 24f));
        int verticalPad = Math.round(RoundedViewHelper.dp(context, 4f));
        messageView.setPadding(horizontalPad, verticalPad, horizontalPad, verticalPad);
        messageView.setTextColor(resolveThemeColor(context, android.R.attr.textColorSecondary, Color.DKGRAY));
        messageView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
        messageView.setLineSpacing(RoundedViewHelper.dp(context, 3f), 1f);
        messageView.setText(message);
        return addView(messageView);
    }

    /** Convenience helper that displays a body message from a string resource. */
    public RoundedBottomSheet setMessage(@StringRes int resId) {
        return setMessage(context.getString(resId));
    }

    /**
     * Sets the scrollable sheet content, detaching {@code view} from any existing parent first and
     * replacing any previously added view.
     */
    public RoundedBottomSheet addView(@NonNull View view) {
        Objects.requireNonNull(view, "view");
        if (view.getParent() instanceof ViewGroup) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        scrollView.removeAllViews();
        scrollView.addView(view);
        scrollView.setVisibility(View.VISIBLE);
        return this;
    }

    /** Configures cancelability both before and after {@link #show()}. */
    public RoundedBottomSheet setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        dialog.setCancelable(cancelable);
        return this;
    }

    public boolean isCancelable() {
        return cancelable;
    }

    /** Configures whether tapping the dimmed backdrop outside the sheet cancels it. */
    public RoundedBottomSheet setCanceledOnTouchOutside(boolean cancel) {
        this.canceledOnTouchOutside = cancel;
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    /** Enables or disables vertical drag-to-dismiss gestures on the handle and header. */
    public RoundedBottomSheet setDraggable(boolean draggable) {
        this.draggable = draggable;
        return this;
    }

    public boolean isDraggable() {
        return draggable;
    }

    /** Shows or hides the top drag handle pill. */
    public RoundedBottomSheet setShowDragHandle(boolean show) {
        handleArea.setVisibility(show ? View.VISIBLE : View.GONE);
        return this;
    }

    public boolean isDragHandleVisible() {
        return handleArea.getVisibility() == View.VISIBLE;
    }

    /** Updates the top corner radius of the sheet surface in dp (default {@code 24dp}). */
    public RoundedBottomSheet setCornerRadiusDp(float dp) {
        float radiusPx = Math.max(0f, RoundedViewHelper.dp(context, dp));
        Drawable background = sheetContainer.getBackground();
        if (background != null) {
            Drawable mutated = background.mutate();
            if (mutated instanceof GradientDrawable) {
                ((GradientDrawable) mutated).setCornerRadii(
                        new float[]{radiusPx, radiusPx, radiusPx, radiusPx, 0f, 0f, 0f, 0f}
                );
                sheetContainer.setBackground(mutated);
                sheetContainer.invalidateOutline();
            }
        }
        return this;
    }

    public RoundedBottomSheet setupPrimaryButton(@Nullable CharSequence text) {
        return setupButton(primaryButton, text, null);
    }

    public RoundedBottomSheet setupPrimaryButton(@Nullable CharSequence text, int textColor) {
        return setupButton(primaryButton, text, textColor);
    }

    public RoundedBottomSheet setupPrimaryButtonOnClick(@Nullable View.OnClickListener listener) {
        primaryButton.setOnClickListener(listener);
        return this;
    }

    public RoundedBottomSheet setupSecondaryButton(@Nullable CharSequence text) {
        return setupButton(secondaryButton, text, null);
    }

    public RoundedBottomSheet setupSecondaryButton(@Nullable CharSequence text, int textColor) {
        return setupButton(secondaryButton, text, textColor);
    }

    public RoundedBottomSheet setupSecondaryButtonOnClick(@Nullable View.OnClickListener listener) {
        secondaryButton.setOnClickListener(listener);
        return this;
    }

    public RoundedBottomSheet setOnDismissListener(@Nullable DialogInterface.OnDismissListener listener) {
        this.externalDismissListener = listener;
        return this;
    }

    public RoundedBottomSheet setOnCancelListener(@Nullable DialogInterface.OnCancelListener listener) {
        this.externalCancelListener = listener;
        return this;
    }

    public RoundedButton getPrimaryButton() {
        return primaryButton;
    }

    public RoundedButton getSecondaryButton() {
        return secondaryButton;
    }

    public View getSheetContainer() {
        return sheetContainer;
    }

    /** Displays the modal bottom sheet with an edge-to-edge dimmed backdrop and slide-up entrance. */
    public void show() {
        dismissing = false;
        sheetContainer.animate().cancel();
        sheetContainer.setTranslationY(0f);

        if (!dialog.isShowing()) {
            configureWindow();
            dialog.show();
            configureWindow();
            sheetContainer.post(() -> {
                int height = sheetContainer.getHeight();
                if (height > 0 && dialog.isShowing() && !dismissing && ValueAnimator.areAnimatorsEnabled()) {
                    sheetContainer.setTranslationY(height);
                    sheetContainer.animate()
                            .translationY(0f)
                            .setDuration(ENTER_ANIMATION_MS)
                            .setInterpolator(new DecelerateInterpolator(1.8f))
                            .start();
                }
            });
        }
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }

    /** Dismisses the sheet; animates out if laid out, or dismisses synchronously before layout. */
    public void dismiss() {
        animateAndClose(false);
    }

    /** Dismisses immediately without running the slide-down exit animation. */
    public void dismissImmediately() {
        dismissing = false;
        sheetContainer.animate().cancel();
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void animateAndClose(boolean cancel) {
        if (!dialog.isShowing() || dismissing) {
            return;
        }
        int height = sheetContainer.getHeight();
        if (height <= 0 || !sheetContainer.isAttachedToWindow() || !ValueAnimator.areAnimatorsEnabled()) {
            if (cancel) {
                dialog.cancel();
            } else {
                dialog.dismiss();
            }
            return;
        }

        dismissing = true;
        sheetContainer.animate().cancel();
        sheetContainer.animate()
                .translationY(height)
                .setDuration(EXIT_ANIMATION_MS)
                .setInterpolator(new AccelerateInterpolator(1.4f))
                .withEndAction(() -> {
                    dismissing = false;
                    if (dialog.isShowing()) {
                        if (cancel) {
                            dialog.cancel();
                        } else {
                            dialog.dismiss();
                        }
                    }
                })
                .start();
    }

    private RoundedBottomSheet setupButton(RoundedButton button, @Nullable CharSequence text, @Nullable Integer color) {
        if (text == null || text.length() == 0) {
            button.setText(null);
            button.setVisibility(View.GONE);
        } else {
            button.setText(text);
            if (color != null) {
                button.setTextColor(color);
            }
            button.setVisibility(View.VISIBLE);
        }
        boolean anyVisible = primaryButton.getVisibility() == View.VISIBLE
                || secondaryButton.getVisibility() == View.VISIBLE;
        actionsContainer.setVisibility(anyVisible ? View.VISIBLE : View.GONE);
        return this;
    }

    private void configureWindow() {
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setDimAmount(DIM_AMOUNT);
        WindowCompat.setDecorFitsSystemWindows(window, false);
    }

    private void applyBottomWindowInsets() {
        final int baseBottomPadding = sheetContainer.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(sheetContainer, (v, insets) -> {
            Insets sysBars = insets.getInsets(
                    WindowInsetsCompat.Type.navigationBars() | WindowInsetsCompat.Type.ime()
            );
            v.setPadding(
                    v.getPaddingLeft(),
                    v.getPaddingTop(),
                    v.getPaddingRight(),
                    baseBottomPadding + sysBars.bottom
            );
            return insets;
        });
    }

    private void styleSecondaryButtonOutline() {
        int textPrimary = resolveThemeColor(context, android.R.attr.textColorPrimary, Color.rgb(15, 23, 42));
        secondaryButton.setTextColor(textPrimary);
        Drawable bg = secondaryButton.getBackground();
        if (bg instanceof RippleDrawable) {
            RippleDrawable ripple = (RippleDrawable) bg.mutate();
            if (ripple.getNumberOfLayers() > 0) {
                Drawable layer = ripple.getDrawable(0);
                if (layer instanceof GradientDrawable) {
                    int strokeWidth = Math.max(1, Math.round(RoundedViewHelper.dp(context, 1f)));
                    ((GradientDrawable) layer).setStroke(
                            strokeWidth,
                            ColorUtils.setAlphaComponent(textPrimary, 48)
                    );
                }
            }
        } else if (bg instanceof LayerDrawable && ((LayerDrawable) bg).getNumberOfLayers() > 0) {
            Drawable layer = ((LayerDrawable) bg).getDrawable(0);
            if (layer instanceof GradientDrawable) {
                int strokeWidth = Math.max(1, Math.round(RoundedViewHelper.dp(context, 1f)));
                ((GradientDrawable) layer).setStroke(
                        strokeWidth,
                        ColorUtils.setAlphaComponent(textPrimary, 48)
                );
            }
        }
        // Keep default corner radius drawable crisp if background mutated above
        setCornerRadiusDp(DEFAULT_CORNER_RADIUS_DP);
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

    private final class DragDismissTouchListener implements View.OnTouchListener {
        private final int touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        private VelocityTracker velocityTracker;
        private float downRawY;
        private float startTranslationY;
        private boolean isDragging;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (!draggable || dismissing) {
                return false;
            }
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    sheetContainer.animate().cancel();
                    downRawY = event.getRawY();
                    startTranslationY = sheetContainer.getTranslationY();
                    isDragging = false;
                    recycleTracker();
                    velocityTracker = VelocityTracker.obtain();
                    velocityTracker.addMovement(event);
                    return true;

                case MotionEvent.ACTION_MOVE:
                    if (velocityTracker != null) {
                        velocityTracker.addMovement(event);
                    }
                    float deltaY = event.getRawY() - downRawY;
                    if (!isDragging && Math.abs(deltaY) > touchSlop) {
                        isDragging = true;
                    }
                    if (isDragging) {
                        sheetContainer.setTranslationY(Math.max(0f, startTranslationY + deltaY));
                    }
                    return true;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    float yVelocity = 0f;
                    if (velocityTracker != null) {
                        velocityTracker.addMovement(event);
                        velocityTracker.computeCurrentVelocity(1000);
                        yVelocity = velocityTracker.getYVelocity();
                        recycleTracker();
                    }
                    if (isDragging) {
                        int height = sheetContainer.getHeight();
                        boolean pastDistance = height > 0
                                && sheetContainer.getTranslationY() > height * DISMISS_DRAG_FRACTION;
                        boolean pastVelocity = yVelocity > DISMISS_VELOCITY_THRESHOLD_PX;
                        if (cancelable && (pastDistance || pastVelocity)) {
                            animateAndClose(true);
                        } else {
                            sheetContainer.animate()
                                    .translationY(0f)
                                    .setDuration(200L)
                                    .setInterpolator(new DecelerateInterpolator(1.6f))
                                    .start();
                        }
                        isDragging = false;
                        return true;
                    }
                    v.performClick();
                    return false;

                default:
                    return false;
            }
        }

        private void recycleTracker() {
            if (velocityTracker != null) {
                velocityTracker.recycle();
                velocityTracker = null;
            }
        }
    }
}
