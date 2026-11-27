package com.aistudio.inpsandroid.demo;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.isaiahnoelpulidosalazar.inpsandroid.ui.Drawables;
import com.isaiahnoelpulidosalazar.inpsandroid.ui.FlippableImageView;
import com.isaiahnoelpulidosalazar.inpsandroid.ui.RoundedButton;
import com.isaiahnoelpulidosalazar.inpsandroid.ui.SimpleList;

import java.util.List;
import java.util.Locale;

public class UiFragment extends Fragment implements DemoHost.NotesListener {

    private FlippableImageView flippableCard;
    private RoundedButton speedButton;
    private TextView drawablesOutput;
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
        simpleList = view.findViewById(R.id.simple_list);

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
            android.graphics.Bitmap bmp = android.graphics.Bitmap.createBitmap(
                    width, height, android.graphics.Bitmap.Config.ARGB_8888);
            try {
                if (front != null) {
                    android.graphics.Canvas canvas = new android.graphics.Canvas(bmp);
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

        view.findViewById(R.id.btn_fullscreen).setOnClickListener(v -> {
            if (getActivity() instanceof DemoHost) {
                ((DemoHost) getActivity()).toggleFullscreen();
            }
        });

        simpleList.setItemPadding(14, 12, 14, 12);
        simpleList.setOnItemClickListener(pos -> {
            List<String> items = simpleList.getItems();
            if (pos >= 0 && pos < items.size() && getActivity() instanceof DemoHost) {
                ((DemoHost) getActivity()).showInfoDialog("SimpleList Row", "Selected: " + items.get(pos));
            }
        });
        simpleList.setOnItemLongClickListener(pos -> {
            List<String> items = simpleList.getItems();
            if (pos >= 0 && pos < items.size()) {
                String removed = items.get(pos);
                simpleList.removeItem(pos);
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
        }
    }
}
