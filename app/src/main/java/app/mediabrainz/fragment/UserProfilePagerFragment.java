package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.mediabrainz.R;
import app.mediabrainz.adapter.pager.UserProfilePagerAdapter;


public class UserProfilePagerFragment extends LazyFragment {

    private ViewPager pagerView;
    private TabLayout tabsView;

    public interface UserProfileTabOrdinalCommunicator {
        int getUserProfileTabOrdinal();
    }

    public static UserProfilePagerFragment newInstance() {
        Bundle args = new Bundle();
        UserProfilePagerFragment fragment = new UserProfilePagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_pager_without_icons, container, false);

        pagerView = layout.findViewById(R.id.pagerView);
        tabsView = layout.findViewById(R.id.tabsView);

        loadView();
        return layout;
    }

    @Override
    protected void lazyLoad() {
        UserProfilePagerAdapter pagerAdapter = new UserProfilePagerAdapter(getChildFragmentManager(), getResources());
        pagerView.setAdapter(pagerAdapter);
        pagerView.setOffscreenPageLimit(pagerAdapter.getCount());
        tabsView.setupWithViewPager(pagerView);
        tabsView.setTabMode(TabLayout.MODE_FIXED);
        pagerAdapter.setupTabViews(tabsView);

        int userProfileTabOrdinal = ((UserProfileTabOrdinalCommunicator) getContext()).getUserProfileTabOrdinal();
        if (userProfileTabOrdinal >= 0 && userProfileTabOrdinal < UserProfilePagerAdapter.PAGE_COUNT) {
            pagerView.setCurrentItem(userProfileTabOrdinal);
        }
    }
}
