package com.aistudio.inpsandroid.demo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.isaiahnoelpulidosalazar.inpsandroid.ui.Drawables;
import com.isaiahnoelpulidosalazar.inpsandroid.ui.FlippableImageView;
import com.isaiahnoelpulidosalazar.inpsandroid.ui.RoundedBottomSheet;
import com.isaiahnoelpulidosalazar.inpsandroid.ui.RoundedButton;
import com.isaiahnoelpulidosalazar.inpsandroid.ui.RoundedCheckBox;
import com.isaiahnoelpulidosalazar.inpsandroid.ui.RoundedEditText;
import com.isaiahnoelpulidosalazar.inpsandroid.ui.SimpleList;

import java.util.List;
import java.util.Locale;

/**
 * Interactive playground tab for {@code ...inpsandroid.ui} widgets: {@link RoundedCheckBox},
 * {@link SimpleList}, {@link FlippableImageView}, {@link Drawables}, {@link RoundedBottomSheet},
 * {@code RoundedAlertDialog}, and {@code Fullscreen}.
 */
public class UiFragment extends Fragment implements DemoHost.NotesListener {

    private FlippableImageView flippableCard;
    private RoundedButton speedButton;
    private TextView drawablesOutput;
    private TextView listSelectionOutput;
    private SimpleList simpleList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ui, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        DemoHost.applyBottomBarPadding(view.findViewById(R.id.ui_scroll));

        flippableCard = view.findViewById(R.id.flippable_card);
        speedButton = view.findViewById(R.id.btn_cycle_speed);
        drawablesOutput = view.findViewById(R.id.ui_drawables_output);
        listSelectionOutput = view.findViewById(R.id.ui_list_selection_output);
        simpleList = view.findViewById(R.id.simple_list);

        RoundedCheckBox bounceToggle = view.findViewById(R.id.cb_bounce_toggle);
        RoundedCheckBox cardSample = view.findViewById(R.id.cb_card_sample);
        RoundedCheckBox pillSample = view.findViewById(R.id.cb_pill_sample);
        RoundedCheckBox minimalSample = view.findViewById(R.id.cb_minimal_sample);
        RoundedCheckBox listModeToggle = view.findViewById(R.id.cb_list_mode);

        bounceToggle.setOnCheckedChangeListener((cb, enabled) -> {
            bounceToggle.setBounceAnimationEnabled(enabled);
            cardSample.setBounceAnimationEnabled(enabled);
            pillSample.setBounceAnimationEnabled(enabled);
            minimalSample.setBounceAnimationEnabled(enabled);
            listModeToggle.setBounceAnimationEnabled(enabled);
            simpleList.setBounceAnimationEnabled(enabled);
        });

        listModeToggle.setOnCheckedChangeListener((cb, enabled) -> {
            simpleList.setCheckboxesEnabled(enabled);
            updateSelectionSummary();
        });

        flippableCard.setFrontImage(R.drawable.ic_card_front);
        flippableCard.setBackImage(R.drawable.ic_card_back);
        flippableCard.setOnClickListener(v -> {
            flippableCard.flip();
            updateCardStatus();
        });

        view.findViewById(R.id.btn_flip).setOnClickListener(v -> {
            flippableCard.flip();
            updateCardStatus();
        });

        speedButton.setOnClickListener(v -> {
            FlippableImageView.Speed next;
            String label;
            switch (flippableCard.getSpeed()) {
                case NORMAL:
                    next = FlippableImageView.Speed.FAST;
                    label = "Speed: Fast";
                    break;
                case FAST:
                    next = FlippableImageView.Speed.SLOW;
                    label = "Speed: Slow";
                    break;
                case SLOW:
                default:
                    next = FlippableImageView.Speed.NORMAL;
                    label = "Speed: Normal";
                    break;
            }
            flippableCard.setSpeed(next);
            speedButton.setText(label);
        });

        view.findViewById(R.id.btn_compare_drawables).setOnClickListener(v -> {
            Drawable front = ContextCompat.getDrawable(requireContext(), R.drawable.ic_card_front);
            int width = front != null && front.getIntrinsicWidth() > 0 ? front.getIntrinsicWidth() : 64;
            int height = front != null && front.getIntrinsicHeight() > 0 ? front.getIntrinsicHeight() : 64;
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            try {
                if (front != null) {
                    Canvas canvas = new Canvas(bmp);
                    front.setBounds(0, 0, width, height);
                    front.draw(canvas);
                }
                boolean sameFront = Drawables.equalsBitmap(requireContext(), R.drawable.ic_card_front, bmp);
                boolean frontVsBack = Drawables.equalsBitmap(requireContext(), R.drawable.ic_card_back, bmp);
                drawablesOutput.setText(String.format(
                        Locale.US,
                        "Drawables.equalsBitmap(ic_card_front, bmp) = %s\nDrawables.equalsBitmap(ic_card_back, bmp)  = %s",
                        sameFront, frontVsBack
                ));
            } finally {
                bmp.recycle();
            }
        });

        view.findViewById(R.id.btn_dialog).setOnClickListener(v -> {
            if (getActivity() instanceof DemoHost) {
                ((DemoHost) getActivity()).showInfoDialog(
                        "RoundedAlertDialog",
                        "Theme-aware colors, RoundedButton actions, and parent-detaching addView()."
                );
            }
        });

        view.findViewById(R.id.btn_bottom_sheet).setOnClickListener(v -> showInteractiveBottomSheet(bounceToggle.isChecked()));

        view.findViewById(R.id.btn_fullscreen).setOnClickListener(v -> {
            if (getActivity() instanceof DemoHost) {
                ((DemoHost) getActivity()).toggleFullscreen();
            }
        });

        simpleList.setOnItemClickListener(pos -> {
            if (!simpleList.isCheckboxesEnabled()) {
                List<String> items = simpleList.getItems();
                if (pos >= 0 && pos < items.size() && getActivity() instanceof DemoHost) {
                    ((DemoHost) getActivity()).showInfoDialog("SimpleList Row", "Selected: " + items.get(pos));
                }
            }
        });
        simpleList.setOnItemCheckedChangeListener((pos, item, isChecked) -> updateSelectionSummary());
        simpleList.setOnItemLongClickListener(pos -> {
            List<String> items = simpleList.getItems();
            if (pos >= 0 && pos < items.size()) {
                String removed = items.get(pos);
                simpleList.removeItem(pos);
                updateSelectionSummary();
                if (getActivity() instanceof DemoHost) {
                    ((DemoHost) getActivity()).deleteNote(removed);
                }
            }
        });

        view.findViewById(R.id.btn_reset_notes).setOnClickListener(v -> {
            if (getActivity() instanceof DemoHost) {
                ((DemoHost) getActivity()).resetSampleNotes();
            }
        });

        if (getActivity() instanceof DemoHost) {
            DemoHost host = (DemoHost) getActivity();
            host.registerNotesListener(this);
            onNotesUpdated(host.getCurrentNotes());
        }
    }

    private void updateCardStatus() {
        drawablesOutput.setText(String.format(
                Locale.US,
                "FlippableImageView flipped=%s, speed=%s",
                flippableCard.isFlipped(), flippableCard.getSpeed().name()
        ));
    }

    private void updateSelectionSummary() {
        if (listSelectionOutput == null || simpleList == null) return;
        if (!simpleList.isCheckboxesEnabled()) {
            listSelectionOutput.setText("Checkbox mode OFF (tap row to inspect)");
            return;
        }
        List<String> checked = simpleList.getCheckedItems();
        listSelectionOutput.setText(String.format(
                Locale.US,
                "Checked (%d): %s",
                checked.size(), checked
        ));
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
        if (simpleList != null) {
            simpleList.setItems(notes);
            updateSelectionSummary();
        }
    }

    private void showInteractiveBottomSheet(boolean bounceEnabled) {
        float density = getResources().getDisplayMetrics().density;
        int padH = Math.round(24 * density);
        int gap = Math.round(8 * density);

        LinearLayout container = new LinearLayout(requireContext());
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(padH, Math.round(4 * density), padH, Math.round(4 * density));

        RoundedCheckBox showHandleCheck = new RoundedCheckBox(requireContext());
        showHandleCheck.setStyle(RoundedCheckBox.Style.CARD);
        showHandleCheck.setBounceAnimationEnabled(bounceEnabled);
        showHandleCheck.setText("Show drag handle");
        showHandleCheck.setChecked(true);

        RoundedCheckBox draggableCheck = new RoundedCheckBox(requireContext());
        draggableCheck.setStyle(RoundedCheckBox.Style.PILL);
        draggableCheck.setBounceAnimationEnabled(bounceEnabled);
        draggableCheck.setText("Enable swipe-to-dismiss");
        draggableCheck.setChecked(true);

        RoundedCheckBox extraRoundedCheck = new RoundedCheckBox(requireContext());
        extraRoundedCheck.setStyle(RoundedCheckBox.Style.MINIMAL);
        extraRoundedCheck.setBounceAnimationEnabled(bounceEnabled);
        extraRoundedCheck.setText("28dp top corner radius");
        extraRoundedCheck.setChecked(false);

        RoundedEditText noteInput = new RoundedEditText(requireContext());
        noteInput.setHint("Optional: add note to EasySQL & SimpleList");
        int inputPad = Math.round(14 * density);
        noteInput.setPadding(inputPad, inputPad, inputPad, inputPad);

        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rowParams.topMargin = gap;

        container.addView(showHandleCheck);
        container.addView(draggableCheck, rowParams);
        container.addView(extraRoundedCheck, rowParams);
        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        inputParams.topMargin = Math.round(12 * density);
        container.addView(noteInput, inputParams);

        RoundedBottomSheet sheet = new RoundedBottomSheet(requireContext())
                .setTitle("RoundedBottomSheet")
                .setSubtitle("Toggle options live or swipe down on the header to dismiss.")
                .addView(container);

        showHandleCheck.setOnCheckedChangeListener((cb, checked) -> sheet.setShowDragHandle(checked));
        draggableCheck.setOnCheckedChangeListener((cb, checked) -> sheet.setDraggable(checked));
        extraRoundedCheck.setOnCheckedChangeListener((cb, checked) ->
                sheet.setCornerRadiusDp(checked ? 28f : 24f));

        sheet.setupSecondaryButton("Close")
                .setupSecondaryButtonOnClick(v -> sheet.dismiss());
        sheet.setupPrimaryButton("Apply")
                .setupPrimaryButtonOnClick(v -> {
                    String note = noteInput.getText() != null ? noteInput.getText().toString().trim() : "";
                    if (!note.isEmpty() && getActivity() instanceof DemoHost) {
                        ((DemoHost) getActivity()).addNote(note, null);
                    }
                    sheet.dismiss();
                });
        sheet.show();
    }
}
