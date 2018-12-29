package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.mediabrainz.R;
import app.mediabrainz.adapter.pager.RecordingInfoPagerAdapter;


public class RecordingInfoPagerFragment extends LazyFragment {

    private ViewPager pagerView;
    private TabLayout tabsView;

    public static RecordingInfoPagerFragment newInstance() {
        Bundle args = new Bundle();
        RecordingInfoPagerFragment fragment = new RecordingInfoPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_pager_with_icons, container, false);

        pagerView = layout.findViewById(R.id.pagerView);
        tabsView = layout.findViewById(R.id.tabsView);

        loadView();
        return layout;
    }

    @Override
    protected void lazyLoad() {
        RecordingInfoPagerAdapter pagerAdapter = new RecordingInfoPagerAdapter(getChildFragmentManager(), getResources());
        pagerView.setAdapter(pagerAdapter);
        pagerView.setOffscreenPageLimit(pagerAdapter.getCount());
        tabsView.setupWithViewPager(pagerView);
        pagerAdapter.setupTabViews(tabsView);
    }
}
