package com.aistudio.inpsandroid.demo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/** ViewPager2 adapter supplying documentation tabs for Overview, Core, UI, Edu, and Migration. */
public class DocsPagerAdapter extends FragmentStateAdapter {

    /** Tab index for {@link OverviewFragment}. */
    public static final int TAB_OVERVIEW = 0;
    /** Tab index for {@link CoreFragment}. */
    public static final int TAB_CORE = 1;
    /** Tab index for {@link UiFragment}. */
    public static final int TAB_UI = 2;
    /** Tab index for {@link EduFragment}. */
    public static final int TAB_EDU = 3;
    /** Tab index for {@link MigrationFragment}. */
    public static final int TAB_MIGRATION = 4;

    /**
     * Creates the pager adapter bound to the host activity's lifecycle.
     *
     * @param fragmentActivity host activity
     */
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
