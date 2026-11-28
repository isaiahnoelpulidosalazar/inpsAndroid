package com.aistudio.inpsandroid.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.os.Looper;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class DemoAppTest {

  @Test
  public void mainActivityCreatesTabsAndPagerAdapter() {
    try (ActivityController<MainActivity> controller =
        Robolectric.buildActivity(MainActivity.class).setup()) {
      MainActivity activity = controller.get();
      Shadows.shadowOf(Looper.getMainLooper()).idle();

      TabLayout tabs = activity.findViewById(R.id.docs_tabs);
      ViewPager2 pager = activity.findViewById(R.id.docs_pager);

      assertNotNull(tabs);
      assertNotNull(pager);
      assertEquals(5, tabs.getTabCount());
      assertNotNull(pager.getAdapter());
      assertEquals(5, pager.getAdapter().getItemCount());

      DocsPagerAdapter adapter = (DocsPagerAdapter) pager.getAdapter();
      assertTrue(adapter.createFragment(DocsPagerAdapter.TAB_OVERVIEW) instanceof OverviewFragment);
      assertTrue(adapter.createFragment(DocsPagerAdapter.TAB_CORE) instanceof CoreFragment);
      assertTrue(adapter.createFragment(DocsPagerAdapter.TAB_UI) instanceof UiFragment);
      assertTrue(adapter.createFragment(DocsPagerAdapter.TAB_EDU) instanceof EduFragment);
      Fragment migration = adapter.createFragment(DocsPagerAdapter.TAB_MIGRATION);
      assertTrue(migration instanceof MigrationFragment);

      activity.selectTab(DocsPagerAdapter.TAB_CORE);
      Shadows.shadowOf(Looper.getMainLooper()).idle();
      assertEquals(DocsPagerAdapter.TAB_CORE, pager.getCurrentItem());
    }
  }
}
