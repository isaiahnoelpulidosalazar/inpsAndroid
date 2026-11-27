package com.aistudio.inpsandroid.demo;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.isaiahnoelpulidosalazar.inpsandroid.core.convert.Converters;
import com.isaiahnoelpulidosalazar.inpsandroid.core.notification.EasyNotification;
import com.isaiahnoelpulidosalazar.inpsandroid.core.time.TimeLeft;
import com.isaiahnoelpulidosalazar.inpsandroid.core.validation.EmailDomainAllowList;
import com.isaiahnoelpulidosalazar.inpsandroid.core.validation.StringChecks;
import com.isaiahnoelpulidosalazar.inpsandroid.ui.RoundedEditText;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Interactive playground tab for {@code ...inpsandroid.core} utilities: {@link Converters},
 * {@link EmailDomainAllowList}, {@link StringChecks}, {@link TimeLeft}, {@code EasySQL}, and
 * {@link EasyNotification}.
 */
public class CoreFragment extends Fragment implements DemoHost.NotesListener {

    private RoundedEditText inputText;
    private TextView outputText;
    private TextView dbStatusText;
    private TextView timeOutputText;
    private TextView notificationOutputText;
    private EasyNotification easyNotification;
    private Runnable pendingNotificationAction;

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted && pendingNotificationAction != null) {
                    Runnable action = pendingNotificationAction;
                    pendingNotificationAction = null;
                    action.run();
                } else if (!granted && notificationOutputText != null) {
                    pendingNotificationAction = null;
                    notificationOutputText.setText("Blocked: POST_NOTIFICATIONS permission denied.");
                }
            });

    private final EmailDomainAllowList emailAllowList = new EmailDomainAllowList.Builder()
            .addDomain("gmail.com")
            .addDomain("outlook.com")
            .addDomain("yahoo.com")
            .useFullDomain(true)
            .build();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_core, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        DemoHost.applyBottomBarPadding(view.findViewById(R.id.core_scroll));

        inputText = view.findViewById(R.id.core_input_text);
        outputText = view.findViewById(R.id.core_output_text);
        dbStatusText = view.findViewById(R.id.core_db_status);
        timeOutputText = view.findViewById(R.id.core_time_output);
        notificationOutputText = view.findViewById(R.id.core_notification_output);

        easyNotification = new EasyNotification(requireContext())
                .createChannel(
                        "demo_alerts",
                        "Demo Alerts",
                        "Notifications dispatched from the inpsAndroid Core tab",
                        NotificationManagerCompat.IMPORTANCE_HIGH,
                        true,
                        true
                );

        view.findViewById(R.id.btn_base64).setOnClickListener(v -> {
            String text = readInputOrDefault("hello inpsAndroid");
            String cap = Converters.capitalize(text);
            String b64 = Converters.toBase64(text);
            outputText.setText("Capitalized: " + cap + "\nBase64: " + b64);
        });

        view.findViewById(R.id.btn_validate_email).setOnClickListener(v -> {
            String text = readInputOrDefault("developer@gmail.com");
            boolean valid = emailAllowList.isValid(text);
            boolean hasDigits = StringChecks.hasNumbers(text);
            outputText.setText(String.format(
                    Locale.US,
                    "Email \"%s\" allow-listed: %s\nContains digit: %s",
                    text, valid, hasDigits
            ));
        });

        view.findViewById(R.id.btn_add_note).setOnClickListener(v -> {
            String note = readInputOrDefault("Note @ " + Converters.toMonthDayYear(new Date()));
            inputText.setText("");
            if (getActivity() instanceof DemoHost) {
                ((DemoHost) getActivity()).addNote(note, () ->
                        outputText.setText("Saved \"" + note + "\" to EasySQL (check UI -> SimpleList)."));
            }
        });

        view.findViewById(R.id.btn_time_left).setOnClickListener(v -> {
            long now = System.currentTimeMillis();
            double secs = TimeLeft.seconds(now, now + 90_000L);
            double mins = TimeLeft.minutes(now, now + 90_000L);
            double hrs = TimeLeft.hours(now, now + 8_100_000L);
            timeOutputText.setText(String.format(
                    Locale.US,
                    "TimeLeft(+90s): %.0f sec / %.2f min\nTimeLeft(+2h15m): %.2f hours",
                    secs, mins, hrs
            ));
        });

        view.findViewById(R.id.btn_send_notification).setOnClickListener(v ->
                runWithNotificationPermission(() -> {
                    String body = readInputOrDefault("Hello from EasyNotification v3.0.2!");
                    int id = easyNotification.prepare("demo_alerts")
                            .title("inpsAndroid Alert")
                            .message(body)
                            .tapActivity(MainActivity.class)
                            .show();
                    updateNotificationStatus(id, "demo_alerts");
                }));

        view.findViewById(R.id.btn_send_big_text_notification).setOnClickListener(v ->
                runWithNotificationPermission(() -> {
                    String body = readInputOrDefault("Expandable BigText notification dispatched cleanly via EasyNotification.");
                    int id = easyNotification.prepare("demo_alerts")
                            .title("Expandable Summary")
                            .message(body)
                            .bigText(body + "\n\nTap to reopen inpsAndroid 3.0.2.")
                            .subText("v3.0.2")
                            .tapActivity(MainActivity.class)
                            .show();
                    updateNotificationStatus(id, "demo_alerts (BigText)");
                }));

        view.findViewById(R.id.btn_cancel_notifications).setOnClickListener(v -> {
            easyNotification.cancelAll();
            notificationOutputText.setText("Cancelled all active app notifications.");
        });

        if (getActivity() instanceof DemoHost) {
            DemoHost host = (DemoHost) getActivity();
            host.registerNotesListener(this);
            onNotesUpdated(host.getCurrentNotes());
        }
    }

    @Override
    public void onDestroyView() {
        if (getActivity() instanceof DemoHost) {
            ((DemoHost) getActivity()).unregisterNotesListener(this);
        }
        super.onDestroyView();
    }

    @Override
    public void onNotesUpdated(List<String> notes) {
        if (dbStatusText != null) {
            dbStatusText.setText(String.format(
                    Locale.US,
                    "EasySQL active rows: %d (synced with UI -> SimpleList).",
                    notes.size()
            ));
        }
    }

    private void runWithNotificationPermission(Runnable action) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && !easyNotification.hasPostNotificationPermission()) {
            pendingNotificationAction = action;
            notificationOutputText.setText("Requesting POST_NOTIFICATIONS permission...");
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            return;
        }
        action.run();
    }

    private void updateNotificationStatus(int id, String channelLabel) {
        if (id == EasyNotification.RESULT_BLOCKED) {
            notificationOutputText.setText("Blocked: notifications disabled or permission denied.");
        } else {
            notificationOutputText.setText(String.format(
                    Locale.US,
                    "Dispatched notification id=%d on %s.",
                    id, channelLabel
            ));
        }
    }

    private String readInputOrDefault(String fallback) {
        CharSequence cs = inputText.getText();
        String s = cs == null ? "" : cs.toString().trim();
        return s.isEmpty() ? fallback : s;
    }
}
