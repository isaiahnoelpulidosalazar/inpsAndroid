package com.aistudio.inpsandroid.demo;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public interface DemoHost {

    interface NotesListener {
        void onNotesUpdated(List<String> notes);
    }

    void selectTab(int index);

    void addNote(String note, Runnable onSaved);

    void deleteNote(String note);

    void resetSampleNotes();

    List<String> getCurrentNotes();

    void registerNotesListener(NotesListener listener);

    void unregisterNotesListener(NotesListener listener);

    void toggleFullscreen();

    void showInfoDialog(String title, String message);

    double playMidiNote(int midi);

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
