package com.aistudio.inpsandroid.demo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DocsPagerAdapter extends FragmentStateAdapter {

    public static final int TAB_OVERVIEW = 0;
    public static final int TAB_CORE = 1;
    public static final int TAB_UI = 2;
    public static final int TAB_EDU = 3;
    public static final int TAB_MIGRATION = 4;

    public DocsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case TAB_CORE:
                return new CoreFragment();
            case TAB_UI:
                return new UiFragment();
            case TAB_EDU:
                return new EduFragment();
            case TAB_MIGRATION:
                return new MigrationFragment();
            case TAB_OVERVIEW:
            default:
                return new OverviewFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
