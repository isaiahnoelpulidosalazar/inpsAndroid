package com.isaiahnoelpulidosalazar.inpsandroid.ui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class UiWidgetsTest {

    private ContextThemeWrapper darkPrimaryContext;
    private ContextThemeWrapper lightPrimaryContext;

    public static class TestThemedActivity extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            setTheme(androidx.appcompat.R.style.Theme_AppCompat_Light);
            super.onCreate(savedInstanceState);
        }
    }

    @Before
    public void setUp() {
        darkPrimaryContext = new ContextThemeWrapper(
                ApplicationProvider.getApplicationContext(),
                com.google.android.material.R.style.Theme_Material3_Light_NoActionBar
        );
        lightPrimaryContext = new ContextThemeWrapper(
                ApplicationProvider.getApplicationContext(),
                com.google.android.material.R.style.Theme_Material3_Dark_NoActionBar
        );
    }

    @Test
    public void testRoundedViewsTextLuminanceXmlBackgroundAndOutlineClipping() {
        RoundedButton darkBgButton = new RoundedButton(darkPrimaryContext);
        assertNotNull(darkBgButton.getBackground());
        assertEquals(Color.WHITE, darkBgButton.getCurrentTextColor());

        RoundedButton lightBgButton = new RoundedButton(lightPrimaryContext);
        assertNotNull(lightBgButton.getBackground());
        assertEquals(Color.BLACK, lightBgButton.getCurrentTextColor());

        AttributeSet xmlBgAttrs = Robolectric.buildAttributeSet()
                .addAttribute(android.R.attr.background, "@android:color/holo_red_dark")
                .build();
        RoundedButton customBgButton = new RoundedButton(darkPrimaryContext, xmlBgAttrs);
        assertTrue(customBgButton.getBackground() instanceof ColorDrawable);

        RoundedEditText editText = new RoundedEditText(darkPrimaryContext);
        assertNotNull(editText.getBackground());

        RoundedLinearLayout linearLayout = new RoundedLinearLayout(darkPrimaryContext);
        assertNotNull(linearLayout.getBackground());
        assertTrue(linearLayout.getClipToOutline());

        RoundedConstraintLayout constraintLayout = new RoundedConstraintLayout(darkPrimaryContext);
        assertNotNull(constraintLayout.getBackground());
        assertTrue(constraintLayout.getClipToOutline());
    }

    @Test
    public void testRoundedAlertDialogDetachesParentAndPostShowCancelable() {
        FrameLayout previousParent = new FrameLayout(darkPrimaryContext);
        TextView content = new TextView(darkPrimaryContext);
        content.setText("Dialog body");
        previousParent.addView(content);
        assertEquals(previousParent, content.getParent());

        AtomicBoolean leftClicked = new AtomicBoolean(false);
        AtomicBoolean rightClicked = new AtomicBoolean(false);

        RoundedAlertDialog dialog = new RoundedAlertDialog(darkPrimaryContext)
                .setTitle("Confirm")
                .addView(content)
                .setupLeftButton("Cancel")
                .setupLeftButtonOnClick(v -> leftClicked.set(true))
                .setupRightButton("OK", Color.BLUE)
                .setupRightButtonOnClick(v -> rightClicked.set(true));

        assertEquals(0, previousParent.getChildCount());

        dialog.dismiss();
        assertFalse(dialog.isShowing());

        dialog.show();
        assertTrue(dialog.isShowing());

        // setCancelable works after show() without throwing
        dialog.setCancelable(false);

        dialog.dismiss();
        assertFalse(dialog.isShowing());
    }

    @Test
    public void testSimpleListSetItemsWithGetItemsAndTextViewRows() {
        SimpleList list = new SimpleList(darkPrimaryContext);
        list.addItem("First");
        list.addItems(Arrays.asList("Second", "Third"));
        assertEquals(3, list.getItems().size());
        assertThrows(UnsupportedOperationException.class, () -> list.getItems().add("Nope"));

        // Self-assignment using live getItems() view must preserve items
        list.setItems(list.getItems());
        assertEquals(Arrays.asList("First", "Second", "Third"), list.getItems());

        list.setItemPadding(8);
        list.setItems(Arrays.asList("Apple", "Banana"));
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        assertEquals(Arrays.asList("Apple", "Banana"), list.getItems());

        AtomicInteger clickedPos = new AtomicInteger(-1);
        AtomicInteger longClickedPos = new AtomicInteger(-1);
        list.setOnItemClickListener(clickedPos::set);
        list.setOnItemLongClickListener(longClickedPos::set);

        RecyclerView rv = list.getRecyclerView();
        list.measure(
                View.MeasureSpec.makeMeasureSpec(400, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(400, View.MeasureSpec.EXACTLY)
        );
        list.layout(0, 0, 400, 400);

        View firstRow = rv.getChildAt(0);
        assertNotNull(firstRow);
        assertTrue(firstRow instanceof TextView);
        firstRow.performClick();
        firstRow.performLongClick();
        assertEquals(0, clickedPos.get());
        assertEquals(0, longClickedPos.get());

        list.removeItem(0);
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        assertEquals(1, list.getItems().size());

        list.clear();
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        assertTrue(list.getItems().isEmpty());
    }

    @Test
    public void testFlippableImageViewAndStateRestoration() {
        FlippableImageView view = new FlippableImageView(darkPrimaryContext);
        ColorDrawable front = new ColorDrawable(Color.RED);
        Bitmap backBmp = Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888);

        view.setSpeed(FlippableImageView.Speed.FAST);
        assertEquals(FlippableImageView.Speed.FAST, view.getSpeed());

        view.setFrontImage(front);
        view.setBackBitmap(backBmp);
        assertNotNull(view.getFrontDrawable());
        assertNotNull(view.getBackDrawable());
        assertFalse(view.isFlipped());

        // Unambiguous null call for Drawable overload
        view.setFrontImage((Drawable) null);
        view.setFrontImage(front);

        view.setReverseBackImage(true);
        view.instantFlip();
        assertTrue(view.isFlipped());
        assertEquals(-1f, view.getScaleX(), 1e-4f);

        Parcelable saved = view.onSaveInstanceState();
        FlippableImageView restored = new FlippableImageView(darkPrimaryContext);
        restored.setFrontImage(front);
        restored.setBackBitmap(backBmp);
        restored.onRestoreInstanceState(saved);
        assertTrue(restored.isFlipped());
        assertEquals(180f, restored.getRotationY(), 1e-4f);

        view.flip();
        assertFalse(view.isFlipped());
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        backBmp.recycle();
    }

    @Test
    public void testFullscreenEnableAndDisableRestoresPriorState() {
        TestThemedActivity activity = Robolectric.buildActivity(TestThemedActivity.class).setup().get();
        assertNotNull(activity.getSupportActionBar());

        // Pre-hide the action bar before enabling fullscreen
        activity.getSupportActionBar().hide();
        Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(500));
        assertFalse(activity.getSupportActionBar().isShowing());

        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);
        Fullscreen.enable(activity);
        Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(500));
        assertFalse(activity.getSupportActionBar().isShowing());

        Fullscreen.reapplyOnWindowFocusChanged(activity, true);

        // disable() must restore the pre-enable hidden state rather than forcing ActionBar visible
        Fullscreen.disable(activity);
        Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(500));
        assertFalse(activity.getSupportActionBar().isShowing());

        // When ActionBar was visible before enable(), disable() restores it visible
        activity.getSupportActionBar().show();
        Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(500));
        assertTrue(activity.getSupportActionBar().isShowing());

        Fullscreen.enable(activity);
        Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(500));
        assertFalse(activity.getSupportActionBar().isShowing());

        Fullscreen.disable(activity);
        Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(500));
        assertTrue(activity.getSupportActionBar().isShowing());
    }
}
