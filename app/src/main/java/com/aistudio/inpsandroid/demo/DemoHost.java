package com.aistudio.inpsandroid.demo;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

/**
 * Host contract implemented by {@link MainActivity} so demo documentation fragments can switch
 * tabs, read/mutate shared {@code EasySQL} notes, toggle fullscreen, and synthesize audio notes.
 */
public interface DemoHost {

    /** Callback notified whenever the shared EasySQL note list changes. */
    interface NotesListener {
        /**
         * Receives an updated snapshot of stored notes.
         *
         * @param notes immutable/copied list of note strings
         */
        void onNotesUpdated(List<String> notes);
    }

    /** Switches the top-level documentation {@link androidx.viewpager2.widget.ViewPager2} tab. */
    void selectTab(int index);

    /** Persists a new note in EasySQL and invokes {@code onSaved} on the main thread. */
    void addNote(String note, Runnable onSaved);

    /** Deletes matching note entries from EasySQL and notifies registered listeners. */
    void deleteNote(String note);

    /** Drops and re-seeds the EasySQL notes table with sample entries. */
    void resetSampleNotes();

    /** Returns a snapshot of the currently cached EasySQL notes. */
    List<String> getCurrentNotes();

    /** Registers a listener to receive note-list updates. */
    void registerNotesListener(NotesListener listener);

    /** Unregisters a previously registered notes listener. */
    void unregisterNotesListener(NotesListener listener);

    /** Toggles immersive fullscreen using {@link com.isaiahnoelpulidosalazar.inpsandroid.ui.Fullscreen}. */
    void toggleFullscreen();

    /** Displays a {@link com.isaiahnoelpulidosalazar.inpsandroid.ui.RoundedAlertDialog} info popup. */
    void showInfoDialog(String title, String message);

    /** Triggers playback of {@code midi} via {@link com.isaiahnoelpulidosalazar.inpsandroid.edu.SoundSynthesizer}. */
    double playMidiNote(int midi);

    /**
     * Preserves bottom navigation-bar spacing inside scrollable tab content during edge-to-edge layout.
     *
     * @param scrollView scrollable container whose bottom padding should track navigation bar insets
     */
    static void applyBottomBarPadding(View scrollView) {
        final int baseBottom = scrollView.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(scrollView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars());
            v.setPadding(
                    v.getPaddingLeft(),
                    v.getPaddingTop(),
                    v.getPaddingRight(),
                    baseBottom + insets.bottom
            );
            return windowInsets;
        });
        ViewCompat.requestApplyInsets(scrollView);
    }
}
