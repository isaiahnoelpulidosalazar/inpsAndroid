package com.isaiahnoelpulidosalazar.inpsandroid.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.view.ContextThemeWrapper;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.test.core.app.ApplicationProvider;
import com.isaiahnoelpulidosalazar.inpsandroid.R;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class DrawablesTest {

  @Test
  public void testEqualsBitmapWithShapeDrawableAndNulls() {
    Context themed =
        new ContextThemeWrapper(
            ApplicationProvider.getApplicationContext(),
            androidx.appcompat.R.style.Theme_AppCompat_Light_NoActionBar);

    Bitmap probe = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
    assertFalse(
        Drawables.equalsBitmap(null, R.drawable.inps_rounded_alert_dialog_background, probe));
    assertFalse(
        Drawables.equalsBitmap(themed, R.drawable.inps_rounded_alert_dialog_background, null));
    probe.recycle();

    Drawable drawable =
        AppCompatResources.getDrawable(themed, R.drawable.inps_rounded_alert_dialog_background);
    assertNotNull(drawable);

    Bitmap matching = Bitmap.createBitmap(48, 48, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(matching);
    drawable.setBounds(0, 0, 48, 48);
    drawable.draw(canvas);

    assertTrue(
        Drawables.equalsBitmap(themed, R.drawable.inps_rounded_alert_dialog_background, matching));

    Bitmap different = Bitmap.createBitmap(48, 48, Bitmap.Config.ARGB_8888);
    different.eraseColor(Color.MAGENTA);
    assertFalse(
        Drawables.equalsBitmap(themed, R.drawable.inps_rounded_alert_dialog_background, different));

    matching.recycle();
    different.recycle();
  }

  @Test
  public void testEqualsBitmapWithHardwareBitmap() {
    Context themed =
        new ContextThemeWrapper(
            ApplicationProvider.getApplicationContext(),
            androidx.appcompat.R.style.Theme_AppCompat_Light_NoActionBar);
    Drawable drawable =
        AppCompatResources.getDrawable(themed, R.drawable.inps_rounded_alert_dialog_background);
    assertNotNull(drawable);

    Picture picture = new Picture();
    Canvas rec = picture.beginRecording(48, 48);
    drawable.setBounds(0, 0, 48, 48);
    drawable.draw(rec);
    picture.endRecording();

    Bitmap hwBitmap = Bitmap.createBitmap(picture, 48, 48, Bitmap.Config.HARDWARE);
    assertEquals(Bitmap.Config.HARDWARE, hwBitmap.getConfig());
    assertTrue(
        Drawables.equalsBitmap(themed, R.drawable.inps_rounded_alert_dialog_background, hwBitmap));
    hwBitmap.recycle();
  }
}
