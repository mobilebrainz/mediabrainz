package app.mediabrainz.adapter.pager;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;


public interface FragmentPagerAdapterInterface {

    void setupTabViews(TabLayout tabLayout);

    Fragment getFragment(int position);

    void updateFragments();
}
