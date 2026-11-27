package com.aistudio.inpsandroid.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.isaiahnoelpulidosalazar.inpsandroid.core.concurrent.MainThread;
import com.isaiahnoelpulidosalazar.inpsandroid.core.database.EasySQL;
import com.isaiahnoelpulidosalazar.inpsandroid.edu.SoundSynthesizer;
import com.isaiahnoelpulidosalazar.inpsandroid.ui.Fullscreen;
import com.isaiahnoelpulidosalazar.inpsandroid.ui.RoundedAlertDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements DemoHost {

    private static final String TABLE_NOTES = "notes";
    private static final String COL_TEXT = "body";

    private final ExecutorService dbExecutor = Executors.newSingleThreadExecutor();
    private final Set<NotesListener> notesListeners = new LinkedHashSet<>();
    private final List<String> cachedNotes = new ArrayList<>();

    private SoundSynthesizer soundSynthesizer;
    private EasySQL easySQL;
    private ViewPager2 docsPager;
    private boolean isFullscreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WindowInsetsControllerCompat insetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        insetsController.setAppearanceLightStatusBars(true);
        insetsController.setAppearanceLightNavigationBars(true);

        View header = findViewById(R.id.docs_header);
        ViewCompat.setOnApplyWindowInsetsListener(header, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars()
                    | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(insets.left, insets.top, insets.right, 0);
            return windowInsets;
        });

        soundSynthesizer = new SoundSynthesizer();
        soundSynthesizer.prepare(60, 64, 67, 72);
        easySQL = new EasySQL(this, "inps_demo.db");

        TabLayout tabLayout = findViewById(R.id.docs_tabs);
        docsPager = findViewById(R.id.docs_pager);
        docsPager.setAdapter(new DocsPagerAdapter(this));
        docsPager.setOffscreenPageLimit(4);

        final int[] tabTitles = {
                R.string.tab_overview,
                R.string.tab_core,
                R.string.tab_ui,
                R.string.tab_edu,
                R.string.tab_migration
        };
        new TabLayoutMediator(tabLayout, docsPager,
                (tab, position) -> tab.setText(tabTitles[position])).attach();

        loadNotesFromDb(false);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Fullscreen.reapplyOnWindowFocusChanged(this, hasFocus);
    }

    @Override
    public void selectTab(int index) {
        if (docsPager != null) {
            docsPager.setCurrentItem(index, true);
        }
    }

    @Override
    public void addNote(String note, Runnable onSaved) {
        dbExecutor.execute(() -> {
            easySQL.insert(TABLE_NOTES, Collections.singletonMap(COL_TEXT, note));
            List<String> updated = queryAllNotes();
            MainThread.post(() -> {
                updateNotesCache(updated);
                if (onSaved != null) {
                    onSaved.run();
                }
            });
        });
    }

    @Override
    public void deleteNote(String note) {
        dbExecutor.execute(() -> {
            easySQL.delete(TABLE_NOTES, COL_TEXT, note);
            List<String> updated = queryAllNotes();
            MainThread.post(() -> updateNotesCache(updated));
        });
    }

    @Override
    public void resetSampleNotes() {
        loadNotesFromDb(true);
    }

    @Override
    public List<String> getCurrentNotes() {
        return new ArrayList<>(cachedNotes);
    }

    @Override
    public void registerNotesListener(NotesListener listener) {
        notesListeners.add(listener);
    }

    @Override
    public void unregisterNotesListener(NotesListener listener) {
        notesListeners.remove(listener);
    }

    @Override
    public void toggleFullscreen() {
        isFullscreen = !isFullscreen;
        if (isFullscreen) {
            Fullscreen.enable(this);
        } else {
            Fullscreen.disable(this);
        }
    }

    @Override
    public void showInfoDialog(String title, String message) {
        TextView body = new TextView(this);
        int pad = Math.round(24 * getResources().getDisplayMetrics().density);
        body.setPadding(pad, pad / 2, pad, pad / 2);
        body.setText(message);

        RoundedAlertDialog dialog = new RoundedAlertDialog(this)
                .setTitle(title)
                .addView(body);
        dialog.setupLeftButton("Close").setupLeftButtonOnClick(v -> dialog.dismiss());
        dialog.setupRightButton("OK").setupRightButtonOnClick(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public double playMidiNote(int midi) {
        soundSynthesizer.playNote(midi);
        return SoundSynthesizer.frequencyOf(midi);
    }

    private void loadNotesFromDb(boolean forceSeed) {
        dbExecutor.execute(() -> {
            Map<String, String> cols = new LinkedHashMap<>();
            cols.put("id", "INTEGER PRIMARY KEY AUTOINCREMENT");
            cols.put(COL_TEXT, "TEXT NOT NULL");
            easySQL.createTable(TABLE_NOTES, cols);

            if (forceSeed) {
                easySQL.dropTable(TABLE_NOTES);
                easySQL.createTable(TABLE_NOTES, cols);
            }

            List<String> loaded = queryAllNotes();
            if (loaded.isEmpty()) {
                easySQL.inTransaction(() -> {
                    easySQL.insert(TABLE_NOTES, Collections.singletonMap(COL_TEXT, "Tap a row to inspect"));
                    easySQL.insert(TABLE_NOTES, Collections.singletonMap(COL_TEXT, "Long-press to delete"));
                    easySQL.insert(TABLE_NOTES, Collections.singletonMap(COL_TEXT, "inpsAndroid 3.0.0 ready"));
                });
                loaded = queryAllNotes();
            }

            final List<String> finalNotes = loaded;
            MainThread.post(() -> updateNotesCache(finalNotes));
        });
    }

    private List<String> queryAllNotes() {
        List<Map<String, Object>> rows = easySQL.selectAll(TABLE_NOTES);
        List<String> loaded = new ArrayList<>(rows.size());
        for (Map<String, Object> row : rows) {
            Object val = row.get(COL_TEXT);
            if (val != null) {
                loaded.add(val.toString());
            }
        }
        return loaded;
    }

    private void updateNotesCache(List<String> notes) {
        cachedNotes.clear();
        cachedNotes.addAll(notes);
        for (NotesListener listener : new ArrayList<>(notesListeners)) {
            listener.onNotesUpdated(getCurrentNotes());
        }
    }

    @Override
    protected void onDestroy() {
        soundSynthesizer.release();
        easySQL.close();
        dbExecutor.shutdown();
        super.onDestroy();
    }
}
