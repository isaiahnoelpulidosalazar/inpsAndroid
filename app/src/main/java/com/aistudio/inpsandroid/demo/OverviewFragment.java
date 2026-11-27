package com.aistudio.inpsandroid.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class OverviewFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        DemoHost.applyBottomBarPadding(view.findViewById(R.id.overview_scroll));

        view.findViewById(R.id.btn_jump_core).setOnClickListener(v -> selectTab(DocsPagerAdapter.TAB_CORE));
        view.findViewById(R.id.btn_jump_ui).setOnClickListener(v -> selectTab(DocsPagerAdapter.TAB_UI));
        view.findViewById(R.id.btn_jump_edu).setOnClickListener(v -> selectTab(DocsPagerAdapter.TAB_EDU));
    }

    private void selectTab(int index) {
        if (getActivity() instanceof DemoHost) {
            ((DemoHost) getActivity()).selectTab(index);
        }
    }
}
