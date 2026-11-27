package com.isaiahnoelpulidosalazar.inpsandroid.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.isaiahnoelpulidosalazar.inpsandroid.R;

/**
 * Builder-style rounded dialog with a title, optional scrollable content, and two
 * {@link RoundedButton} actions.
 *
 * <pre>{@code
 * RoundedAlertDialog dialog = new RoundedAlertDialog(context).setTitle("Confirm");
 * dialog.setupLeftButton("Cancel").setupLeftButtonOnClick(v -> dialog.dismiss());
 * dialog.setupRightButton("OK", Color.RED).setupRightButtonOnClick(v -> doIt());
 * dialog.show();
 * }</pre>
 */
public class RoundedAlertDialog {

    private final AlertDialog.Builder builder;
    private final TextView titleView;
    private final ScrollView scrollView;
    private final RoundedButton leftButton;
    private final RoundedButton rightButton;
    private AlertDialog alertDialog;
    private boolean cancelable = true;

    public RoundedAlertDialog(Context context) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.inps_rounded_alert_dialog_layout, null);
        titleView = dialogView.findViewById(R.id.inps_rounded_alert_dialog_title);
        scrollView = dialogView.findViewById(R.id.inps_rounded_alert_dialog_view);
        leftButton = dialogView.findViewById(R.id.inps_rounded_alert_dialog_left_button);
        rightButton = dialogView.findViewById(R.id.inps_rounded_alert_dialog_right_button);
        scrollView.setVisibility(View.GONE);
        builder = new AlertDialog.Builder(context).setView(dialogView);
    }

    /**
     * Sets the scrollable content, detaching {@code view} from any existing parent first and
     * replacing any previously added view.
     */
    public RoundedAlertDialog addView(View view) {
        if (view.getParent() instanceof ViewGroup) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        scrollView.removeAllViews();
        scrollView.addView(view);
        scrollView.setVisibility(View.VISIBLE);
        return this;
    }

    public RoundedAlertDialog setTitle(CharSequence title) {
        titleView.setText(title);
        return this;
    }

    /** Configures cancelability both before and after {@link #show()}. */
    public RoundedAlertDialog setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        builder.setCancelable(cancelable);
        if (alertDialog != null) {
            alertDialog.setCancelable(cancelable);
        }
        return this;
    }

    public RoundedAlertDialog setupLeftButton(String text) { return setup(leftButton, text, null); }

    public RoundedAlertDialog setupLeftButton(String text, int color) { return setup(leftButton, text, color); }

    public RoundedAlertDialog setupRightButton(String text) { return setup(rightButton, text, null); }

    public RoundedAlertDialog setupRightButton(String text, int color) { return setup(rightButton, text, color); }

    public RoundedAlertDialog setupLeftButtonOnClick(View.OnClickListener listener) {
        leftButton.setOnClickListener(listener);
        return this;
    }

    public RoundedAlertDialog setupRightButtonOnClick(View.OnClickListener listener) {
        rightButton.setOnClickListener(listener);
        return this;
    }

    private RoundedAlertDialog setup(RoundedButton button, String text, Integer color) {
        button.setText(text);
        if (color != null) button.setTextColor(color);
        button.setVisibility(View.VISIBLE);
        return this;
    }

    /** Builds (once) and shows the dialog with a transparent window so the rounded shape is visible. */
    public void show() {
        if (alertDialog == null) {
            alertDialog = builder.create();
            alertDialog.setCancelable(cancelable);
            Window window = alertDialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }
        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

    public boolean isShowing() {
        return alertDialog != null && alertDialog.isShowing();
    }

    /** Dismisses the dialog; safe to call before {@link #show()}. */
    public void dismiss() {
        if (alertDialog != null) alertDialog.dismiss();
    }
}
