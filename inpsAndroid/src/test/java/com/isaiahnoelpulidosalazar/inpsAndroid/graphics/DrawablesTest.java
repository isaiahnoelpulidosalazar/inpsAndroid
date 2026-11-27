package com.isaiahnoelpulidosalazar.inpsAndroid.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.ContextThemeWrapper;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.test.core.app.ApplicationProvider;

import com.isaiahnoelpulidosalazar.inpsandroid.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class DrawablesTest {

    @Test
    public void testEqualsBitmapWithShapeDrawableAndNulls() {
        Context themed = new ContextThemeWrapper(
                ApplicationProvider.getApplicationContext(),
                com.google.android.material.R.style.Theme_Material3_Light_NoActionBar
        );

        assertFalse(Drawables.equalsBitmap(null, R.drawable.rounded_alert_dialog_background,
                Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)));
        assertFalse(Drawables.equalsBitmap(themed, R.drawable.rounded_alert_dialog_background, null));

        Drawable drawable = AppCompatResources.getDrawable(themed, R.drawable.rounded_alert_dialog_background);
        assertNotNull(drawable);

        Bitmap matching = Bitmap.createBitmap(48, 48, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(matching);
        drawable.setBounds(0, 0, 48, 48);
        drawable.draw(canvas);

        assertTrue(Drawables.equalsBitmap(themed, R.drawable.rounded_alert_dialog_background, matching));

        Bitmap different = Bitmap.createBitmap(48, 48, Bitmap.Config.ARGB_8888);
        different.eraseColor(Color.MAGENTA);
        assertFalse(Drawables.equalsBitmap(themed, R.drawable.rounded_alert_dialog_background, different));

        matching.recycle();
        different.recycle();
    }
}
