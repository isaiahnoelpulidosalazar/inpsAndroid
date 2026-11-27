package com.isaiahnoelpulidosalazar.inpsAndroid;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

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

    private ContextThemeWrapper themedContext;

    public static class TestThemedActivity extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            setTheme(com.google.android.material.R.style.Theme_Material3_Light);
            super.onCreate(savedInstanceState);
        }
    }

    @Before
    public void setUp() {
        themedContext = new ContextThemeWrapper(
                ApplicationProvider.getApplicationContext(),
                com.google.android.material.R.style.Theme_Material3_Light_NoActionBar
        );
    }

    @Test
    public void testRoundedViews() {
        RoundedButton button = new RoundedButton(themedContext);
        assertNotNull(button.getBackground());

        RoundedEditText editText = new RoundedEditText(themedContext);
        assertNotNull(editText.getBackground());

        RoundedLinearLayout linearLayout = new RoundedLinearLayout(themedContext);
        assertNotNull(linearLayout.getBackground());

        RoundedConstraintLayout constraintLayout = new RoundedConstraintLayout(themedContext);
        assertNotNull(constraintLayout.getBackground());
    }

    @Test
    public void testRoundedAlertDialog() {
        TextView content = new TextView(themedContext);
        content.setText("Dialog body");

        AtomicBoolean leftClicked = new AtomicBoolean(false);
        AtomicBoolean rightClicked = new AtomicBoolean(false);

        RoundedAlertDialog dialog = new RoundedAlertDialog(themedContext)
                .setTitle("Confirm")
                .setCancelable(true)
                .addView(content)
                .setupLeftButton("Cancel")
                .setupLeftButtonOnClick(v -> leftClicked.set(true))
                .setupRightButton("OK", Color.BLUE)
                .setupRightButtonOnClick(v -> rightClicked.set(true));

        // Safe before show
        dialog.dismiss();
        assertFalse(dialog.isShowing());

        dialog.show();
        assertTrue(dialog.isShowing());

        dialog.dismiss();
        assertFalse(dialog.isShowing());
    }

    @Test
    public void testSimpleListOperationsAndListeners() {
        SimpleList list = new SimpleList(themedContext);
        list.addItem("First");
        list.addItems(Arrays.asList("Second", "Third"));
        assertEquals(3, list.getItems().size());
        assertThrows(UnsupportedOperationException.class, () -> list.getItems().add("Nope"));

        list.setItemPadding(8);
        list.setItems(Arrays.asList("Apple", "Banana"));
        assertEquals(Arrays.asList("Apple", "Banana"), list.getItems());

        AtomicInteger clickedPos = new AtomicInteger(-1);
        AtomicInteger longClickedPos = new AtomicInteger(-1);
        list.setOnItemClickListener(clickedPos::set);
        list.setOnItemLongClickListener(longClickedPos::set);

        list.measure(
                View.MeasureSpec.makeMeasureSpec(400, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(400, View.MeasureSpec.EXACTLY)
        );
        list.layout(0, 0, 400, 400);

        View firstRow = list.getChildAt(0);
        assertNotNull(firstRow);
        firstRow.performClick();
        firstRow.performLongClick();
        assertEquals(0, clickedPos.get());
        assertEquals(0, longClickedPos.get());

        list.removeItem(0);
        assertEquals(1, list.getItems().size());

        list.clear();
        assertTrue(list.getItems().isEmpty());
    }

    @Test
    public void testFlippableImageView() {
        FlippableImageView view = new FlippableImageView(themedContext);
        ColorDrawable front = new ColorDrawable(Color.RED);
        Bitmap backBmp = Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888);

        view.setSpeed(FlippableImageView.Speed.FAST);
        assertEquals(FlippableImageView.Speed.FAST, view.getSpeed());

        view.setFrontImage(front);
        view.setBackImage(backBmp);
        assertNotNull(view.getFrontDrawable());
        assertNotNull(view.getBackDrawable());
        assertFalse(view.isFlipped());

        view.setReverseBackImage(true);
        view.instantFlip();
        assertTrue(view.isFlipped());
        assertEquals(-1f, view.getScaleX(), 1e-4f);

        view.flip();
        assertFalse(view.isFlipped());
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        backBmp.recycle();
    }

    @Test
    public void testFullscreenEnableAndDisable() {
        TestThemedActivity activity = Robolectric.buildActivity(TestThemedActivity.class).setup().get();
        assertNotNull(activity.getSupportActionBar());

        Fullscreen.enable(activity);
        Shadows.shadowOf(Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(500));
        assertFalse(activity.getSupportActionBar().isShowing());

        Fullscreen.disable(activity);
        Shadows.shadowOf(Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(500));
        assertTrue(activity.getSupportActionBar().isShowing());
    }

    @Test
    public void testSoundSynthesizer() {
        assertEquals(440.0, SoundSynthesizer.frequencyOf(69), 1e-6);
        assertEquals(261.625565, SoundSynthesizer.frequencyOf(60), 1e-3);

        try (SoundSynthesizer synth = new SoundSynthesizer()) {
            assertThrows(IllegalArgumentException.class, () -> synth.playNote(-1));
            assertThrows(IllegalArgumentException.class, () -> synth.playNote(128));
            synth.release();
            // Safe to call after release
            synth.playNote(60);
        }
    }
}
