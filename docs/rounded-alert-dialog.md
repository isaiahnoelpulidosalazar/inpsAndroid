# RoundedAlertDialog

Package: `com.isaiahnoelpulidosalazar.inpsandroid.ui`

`RoundedAlertDialog` is a builder-style rounded dialog (`inps_rounded_alert_dialog_layout`) with a theme-colored title, optional scrollable custom content (`addView`), and two `RoundedButton` actions.

## Features

- `addView(View)` detaches the view from any existing parent before adding it to the dialog's `ScrollView`
- `setCancelable(boolean)` applies both before and after `show()`
- Uses theme attributes (`?android:attr/colorBackground`, `?android:attr/textColorPrimary`) rather than hardcoded colors
- `dismiss()` is safe to call even if `show()` has not been called yet

## Usage

```java
import com.isaiahnoelpulidosalazar.inpsandroid.ui.RoundedAlertDialog;

TextView body = new TextView(context);
body.setText("Delete this item?");

RoundedAlertDialog dialog = new RoundedAlertDialog(context)
        .setTitle("Confirm")
        .addView(body)
        .setCancelable(true);

dialog.setupLeftButton("Cancel")
        .setupLeftButtonOnClick(v -> dialog.dismiss());
dialog.setupRightButton("Delete", Color.WHITE)
        .setupRightButtonOnClick(v -> {
            performDelete();
            dialog.dismiss();
        });
dialog.show();
```
