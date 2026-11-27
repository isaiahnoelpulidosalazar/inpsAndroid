package com.aistudio.inpsandroid.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.isaiahnoelpulidosalazar.inpsandroid.core.concurrent.MainThread;
import com.isaiahnoelpulidosalazar.inpsandroid.core.convert.Converters;
import com.isaiahnoelpulidosalazar.inpsandroid.core.database.EasySQL;
import com.isaiahnoelpulidosalazar.inpsandroid.core.time.TimeLeft;
import com.isaiahnoelpulidosalazar.inpsandroid.core.validation.EmailDomainAllowList;
import com.isaiahnoelpulidosalazar.inpsandroid.core.validation.StringChecks;
import com.isaiahnoelpulidosalazar.inpsandroid.edu.AreaCalculator;
import com.isaiahnoelpulidosalazar.inpsandroid.edu.SoundSynthesizer;
import com.isaiahnoelpulidosalazar.inpsandroid.edu.sort.Sort;
import com.isaiahnoelpulidosalazar.inpsandroid.ui.FlippableImageView;
import com.isaiahnoelpulidosalazar.inpsandroid.ui.Fullscreen;
import com.isaiahnoelpulidosalazar.inpsandroid.ui.RoundedAlertDialog;
import com.isaiahnoelpulidosalazar.inpsandroid.ui.RoundedEditText;
import com.isaiahnoelpulidosalazar.inpsandroid.ui.SimpleList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TABLE_NOTES = "notes";
    private static final String COL_TEXT = "body";

    private final ExecutorService dbExecutor = Executors.newSingleThreadExecutor();
    private SoundSynthesizer soundSynthesizer;
    private EasySQL easySQL;
    private boolean isFullscreen;

    private RoundedEditText inputText;
    private TextView outputText;
    private SimpleList simpleList;
    private FlippableImageView flippableCard;

    private final EmailDomainAllowList emailAllowList = new EmailDomainAllowList.Builder()
            .addDomain("gmail.com")
            .addDomain("outlook.com")
            .addDomain("yahoo.com")
            .useFullDomain(true)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View rootContainer = findViewById(R.id.root_container);
        final int basePad = Math.round(16 * getResources().getDisplayMetrics().density);
        ViewCompat.setOnApplyWindowInsetsListener(rootContainer, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    basePad + insets.left,
                    basePad + insets.top,
                    basePad + insets.right,
                    basePad + insets.bottom
            );
            return windowInsets;
        });

        soundSynthesizer = new SoundSynthesizer();
        soundSynthesizer.prepare(60, 64, 67, 72);
        easySQL = new EasySQL(this, "inps_demo.db");

        inputText = findViewById(R.id.input_text);
        outputText = findViewById(R.id.output_text);
        simpleList = findViewById(R.id.simple_list);
        flippableCard = findViewById(R.id.flippable_card);

        flippableCard.setFrontImage(R.drawable.ic_card_front);
        flippableCard.setBackImage(R.drawable.ic_card_back);
        flippableCard.setOnClickListener(v -> flippableCard.flip());

        simpleList.setItemPadding(14, 12, 14, 12);
        simpleList.setOnItemClickListener(pos -> {
            String item = simpleList.getItems().get(pos);
            showInfoDialog("List Item", "Selected: " + item);
        });
        simpleList.setOnItemLongClickListener(pos -> {
            String removed = simpleList.getItems().get(pos);
            simpleList.removeItem(pos);
            outputText.setText("Removed \"" + removed + "\" from SimpleList.");
            dbExecutor.execute(() -> easySQL.delete(TABLE_NOTES, COL_TEXT, removed));
        });

        findViewById(R.id.btn_base64).setOnClickListener(v -> {
            String text = readInputOrDefault("hello inpsAndroid");
            String cap = Converters.capitalize(text);
            String b64 = Converters.toBase64(text);
            outputText.setText("Capitalized: " + cap + "\nBase64: " + b64);
        });

        findViewById(R.id.btn_validate_email).setOnClickListener(v -> {
            String text = readInputOrDefault("developer@gmail.com");
            boolean valid = emailAllowList.isValid(text);
            boolean hasDigits = StringChecks.hasNumbers(text);
            outputText.setText(String.format(
                    Locale.US,
                    "Email \"%s\" allow-listed: %s\nContains digit: %s",
                    text, valid, hasDigits
            ));
        });

        findViewById(R.id.btn_add_note).setOnClickListener(v -> {
            String note = readInputOrDefault("Note @ " + Converters.toMonthDayYear(new Date()));
            simpleList.addItem(note);
            inputText.setText("");
            dbExecutor.execute(() -> easySQL.insert(TABLE_NOTES, Collections.singletonMap(COL_TEXT, note)));
            outputText.setText("Saved \"" + note + "\" to EasySQL.");
        });

        findViewById(R.id.btn_flip).setOnClickListener(v -> flippableCard.flip());

        findViewById(R.id.btn_dialog).setOnClickListener(v ->
                showInfoDialog("RoundedAlertDialog", "Built with inpsAndroid 3.0.0 RoundedAlertDialog."));

        findViewById(R.id.btn_fullscreen).setOnClickListener(v -> {
            isFullscreen = !isFullscreen;
            if (isFullscreen) {
                Fullscreen.enable(this);
            } else {
                Fullscreen.disable(this);
            }
        });

        findViewById(R.id.btn_note_c4).setOnClickListener(v -> playMidi(60, "C4"));
        findViewById(R.id.btn_note_e4).setOnClickListener(v -> playMidi(64, "E4"));
        findViewById(R.id.btn_note_g4).setOnClickListener(v -> playMidi(67, "G4"));
        findViewById(R.id.btn_note_c5).setOnClickListener(v -> playMidi(72, "C5"));

        findViewById(R.id.btn_math_sort).setOnClickListener(v -> {
            int[] sample = {42, -7, 19, 3, 25};
            int[] sorted = Sort.countingSort(sample);
            double circle = AreaCalculator.circleArea(5.0);
            long now = System.currentTimeMillis();
            double mins = TimeLeft.minutes(now, now + 90_000L);
            outputText.setText(String.format(
                    Locale.US,
                    "CountingSort: %s\nCircle(r=5) area: %.2f\nTimeLeft(+90s): %.2f min",
                    Arrays.toString(sorted), circle, mins
            ));
        });

        loadInitialNotes();
    }

    private void loadInitialNotes() {
        dbExecutor.execute(() -> {
            Map<String, String> cols = new LinkedHashMap<>();
            cols.put("id", "INTEGER PRIMARY KEY AUTOINCREMENT");
            cols.put(COL_TEXT, "TEXT NOT NULL");
            easySQL.createTable(TABLE_NOTES, cols);

            List<Map<String, Object>> rows = easySQL.selectAll(TABLE_NOTES);
            if (rows.isEmpty()) {
                easySQL.inTransaction(() -> {
                    easySQL.insert(TABLE_NOTES, Collections.singletonMap(COL_TEXT, "Tap an item to inspect"));
                    easySQL.insert(TABLE_NOTES, Collections.singletonMap(COL_TEXT, "Long-press to delete"));
                    easySQL.insert(TABLE_NOTES, Collections.singletonMap(COL_TEXT, "inpsAndroid 3.0.0 ready"));
                });
                rows = easySQL.selectAll(TABLE_NOTES);
            }

            List<String> loaded = new ArrayList<>(rows.size());
            for (Map<String, Object> row : rows) {
                Object val = row.get(COL_TEXT);
                if (val != null) loaded.add(val.toString());
            }
            MainThread.post(() -> simpleList.setItems(loaded));
        });
    }

    private void playMidi(int midi, String label) {
        soundSynthesizer.playNote(midi);
        outputText.setText(String.format(
                Locale.US,
                "Playing %s (MIDI %d, %.2f Hz)",
                label, midi, SoundSynthesizer.frequencyOf(midi)
        ));
    }

    private void showInfoDialog(String title, String message) {
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

    private String readInputOrDefault(String fallback) {
        CharSequence cs = inputText.getText();
        String s = cs == null ? "" : cs.toString().trim();
        return s.isEmpty() ? fallback : s;
    }

    @Override
    protected void onDestroy() {
        soundSynthesizer.release();
        easySQL.close();
        dbExecutor.shutdown();
        super.onDestroy();
    }
}
