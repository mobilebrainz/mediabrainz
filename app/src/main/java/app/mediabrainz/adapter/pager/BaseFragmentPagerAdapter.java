package app.mediabrainz.adapter.pager;

import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;


public abstract class BaseFragmentPagerAdapter extends FragmentPagerAdapter implements FragmentPagerAdapterInterface {

    public interface Updatable {
        void update();
    }

    private int pageCount;
    protected int[] tabTitles;
    protected int[] tabIcons;
    protected Resources resources;
    private FragmentManager fragmentManager;
    private Map<Integer, String> fragmentTags = new HashMap<>();

    public BaseFragmentPagerAdapter(int pageCount, FragmentManager fragmentManager, Resources resources) {
        super(fragmentManager);
        this.fragmentManager = fragmentManager;
        this.resources = resources;
        this.pageCount = pageCount;
        this.tabTitles = new int[pageCount];
        this.tabIcons = new int[pageCount];
    }

    @Override
    public int getCount() {
        return pageCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position] != 0 ? resources.getString(tabTitles[position]) : null;
    }

    @Override
    public void setupTabViews(TabLayout tabLayout) {
        for (int i = 0; i < pageCount; i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tabIcons[i] != 0) {
                tab.setIcon(tabIcons[i]);
            }
            if (tabTitles[i] != 0) {
                tab.setText(tabTitles[i]);
            }
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object object = super.instantiateItem(container, position);
        if (object instanceof Fragment) {
            Fragment fragment = (Fragment) object;
            String tag = fragment.getTag();
            fragmentTags.put(position, tag);
        }
        return object;
    }

    @Override
    public Fragment getFragment(int position) {
        String tag = fragmentTags.get(position);
        return (tag != null) ? fragmentManager.findFragmentByTag(tag) : null;
    }

    @Override
    public void updateFragments() {
        for (int i = 0; i < pageCount; i++) {
            Fragment fragment = getFragment(i);
            if (fragment instanceof Updatable) {
                ((Updatable) fragment).update();
            }
        }
    }

}
