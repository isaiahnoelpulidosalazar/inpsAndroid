package com.aistudio.inpsandroid.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.isaiahnoelpulidosalazar.inpsandroid.edu.AreaCalculator;
import com.isaiahnoelpulidosalazar.inpsandroid.edu.sort.Sort;

import java.util.Arrays;
import java.util.Locale;

/**
 * Interactive playground tab for {@code ...inpsandroid.edu} modules: {@link Sort},
 * {@link AreaCalculator}, and {@link com.isaiahnoelpulidosalazar.inpsandroid.edu.SoundSynthesizer}.
 */
public class EduFragment extends Fragment {

    private static final int[] SAMPLE_ARRAY = {42, -7, 19, 3, 25, 0, -12};

    private TextView sortOutput;
    private TextView areaOutput;
    private TextView soundOutput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        DemoHost.applyBottomBarPadding(view.findViewById(R.id.edu_scroll));

        sortOutput = view.findViewById(R.id.edu_sort_output);
        areaOutput = view.findViewById(R.id.edu_area_output);
        soundOutput = view.findViewById(R.id.edu_sound_output);

        view.findViewById(R.id.btn_sort_counting).setOnClickListener(v ->
                showSorted("Sort.countingSort", Sort.countingSort(SAMPLE_ARRAY)));

        view.findViewById(R.id.btn_sort_quick).setOnClickListener(v ->
                showSorted("Sort.quicksort", Sort.quicksort(SAMPLE_ARRAY)));

        view.findViewById(R.id.btn_sort_merge).setOnClickListener(v ->
                showSorted("Sort.mergeSort", Sort.mergeSort(SAMPLE_ARRAY)));

        view.findViewById(R.id.btn_sort_heap).setOnClickListener(v ->
                showSorted("Sort.heapsort", Sort.heapsort(SAMPLE_ARRAY.clone())));

        view.findViewById(R.id.btn_area_demo).setOnClickListener(v -> {
            double circle = AreaCalculator.circleArea(5.0);
            double triangle = AreaCalculator.triangleArea(8.0, 6.0);
            double rect = AreaCalculator.rectangleArea(12.0, 4.5);
            double sphere = AreaCalculator.sphereSurfaceArea(3.0);
            areaOutput.setText(String.format(
                    Locale.US,
                    "circleArea(r=5)       = %.2f\ntriangleArea(8, 6)    = %.2f\nrectangleArea(12,4.5) = %.2f\nsphereSurfaceArea(3)  = %.2f",
                    circle, triangle, rect, sphere
            ));
        });

        view.findViewById(R.id.btn_note_c4).setOnClickListener(v -> playNote(60, "C4"));
        view.findViewById(R.id.btn_note_e4).setOnClickListener(v -> playNote(64, "E4"));
        view.findViewById(R.id.btn_note_g4).setOnClickListener(v -> playNote(67, "G4"));
        view.findViewById(R.id.btn_note_c5).setOnClickListener(v -> playNote(72, "C5"));
    }

    private void showSorted(String algorithm, int[] result) {
        sortOutput.setText(String.format(
                Locale.US,
                "%s\nIn:  %s\nOut: %s",
                algorithm,
                Arrays.toString(SAMPLE_ARRAY),
                Arrays.toString(result)
        ));
    }

    private void playNote(int midi, String label) {
        if (getActivity() instanceof DemoHost) {
            double hz = ((DemoHost) getActivity()).playMidiNote(midi);
            soundOutput.setText(String.format(
                    Locale.US,
                    "Playing %s (MIDI %d -> %.2f Hz)",
                    label, midi, hz
            ));
        }
    }
}
