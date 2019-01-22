package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.mediabrainz.R;
import app.mediabrainz.adapter.pager.UserRatingsPagerAdapter;


public class UserRatingsPagerFragment extends LazyFragment {

    private ViewPager pagerView;
    private TabLayout tabsView;

    public static UserRatingsPagerFragment newInstance() {
        Bundle args = new Bundle();
        UserRatingsPagerFragment fragment = new UserRatingsPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflate(R.layout.fragment_pager_with_icons, container);

        pagerView = layout.findViewById(R.id.pagerView);
        tabsView = layout.findViewById(R.id.tabsView);

        loadView();
        return layout;
    }

    @Override
    protected void lazyLoad() {
        UserRatingsPagerAdapter pagerAdapter = new UserRatingsPagerAdapter(getChildFragmentManager(), getResources());
        pagerView.setAdapter(pagerAdapter);
        pagerView.setOffscreenPageLimit(pagerAdapter.getCount());
        tabsView.setupWithViewPager(pagerView);
        pagerAdapter.setupTabViews(tabsView);
    }
}
