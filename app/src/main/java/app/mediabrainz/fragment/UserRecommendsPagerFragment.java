package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.mediabrainz.R;
import app.mediabrainz.adapter.pager.UserRecommendsPagerAdapter;


public class UserRecommendsPagerFragment extends LazyFragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    public static UserRecommendsPagerFragment newInstance() {
        Bundle args = new Bundle();
        UserRecommendsPagerFragment fragment = new UserRecommendsPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_pager_with_icons, container, false);

        viewPager = layout.findViewById(R.id.pager);
        tabLayout = layout.findViewById(R.id.tabs);

        loadView();
        return layout;
    }

    @Override
    protected void lazyLoad() {
        UserRecommendsPagerAdapter pagerAdapter = new UserRecommendsPagerAdapter(getChildFragmentManager(), getResources());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(pagerAdapter.getCount());
        tabLayout.setupWithViewPager(viewPager);
        pagerAdapter.setupTabViews(tabLayout);
    }

}
