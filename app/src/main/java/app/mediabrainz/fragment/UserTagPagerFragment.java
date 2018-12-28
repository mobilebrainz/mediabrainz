package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;

import app.mediabrainz.R;
import app.mediabrainz.adapter.pager.UserTagPagerAdapter;
import app.mediabrainz.api.site.TagEntity;
import app.mediabrainz.api.site.TagServiceInterface;
import app.mediabrainz.communicator.GetUserTagEntitiesCommunicator;
import app.mediabrainz.communicator.ShowTitleCommunicator;

import static app.mediabrainz.MediaBrainzApp.api;


public class UserTagPagerFragment extends Fragment implements
        GetUserTagEntitiesCommunicator {

    public static final String USERNAME = "USERNAME";
    public static final String USER_TAG = "USER_TAG";

    private String username;
    private String userTag;
    private boolean isLoading;
    private boolean isError;
    private Map<TagServiceInterface.UserTagType, List<TagEntity>> entitiesMap;

    private ViewPager pagerView;
    private TabLayout tabsView;
    private View errorView;
    private View progressView;

    public static UserTagPagerFragment newInstance(String username, String tag) {
        Bundle args = new Bundle();
        args.putString(USERNAME, username);
        args.putString(USER_TAG, tag);
        UserTagPagerFragment fragment = new UserTagPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_pager_with_icons, container, false);

        pagerView = layout.findViewById(R.id.pagerView);
        tabsView = layout.findViewById(R.id.tabsView);
        errorView = layout.findViewById(R.id.errorView);
        progressView = layout.findViewById(R.id.progressView);

        username = getArguments().getString(USERNAME);
        userTag = getArguments().getString(USER_TAG);

        ((ShowTitleCommunicator) getContext()).getToolbarTopTitleView().setText(userTag);

        load();
        return layout;
    }

    private void configurePager() {
        UserTagPagerAdapter pagerAdapter = new UserTagPagerAdapter(getChildFragmentManager(), getResources());
        pagerView.setAdapter(pagerAdapter);
        pagerView.setOffscreenPageLimit(pagerAdapter.getCount());
        tabsView.setupWithViewPager(pagerView);
        pagerAdapter.setupTabViews(tabsView);
    }

    private void load() {
        viewError(false);

        viewProgressLoading(true);
        api.getUserTagEntities(username, userTag,
                map -> {
                    viewProgressLoading(false);
                    entitiesMap = map;
                    configurePager();
                },
                this::showConnectionWarning);
    }

    private void viewProgressLoading(boolean isView) {
        if (isView) {
            isLoading = true;
            pagerView.setAlpha(0.3F);
            progressView.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            pagerView.setAlpha(1.0F);
            progressView.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean isView) {
        if (isView) {
            isError = true;
            pagerView.setVisibility(View.INVISIBLE);
            errorView.setVisibility(View.VISIBLE);
        } else {
            isError = false;
            errorView.setVisibility(View.GONE);
            pagerView.setVisibility(View.VISIBLE);
        }
    }

    private void showConnectionWarning(Throwable t) {
        //ShowUtil.showError(getContext(), t);
        viewProgressLoading(false);
        viewError(true);
        errorView.findViewById(R.id.retryButton).setOnClickListener(v -> load());
    }

    @Override
    public List<TagEntity> getEntities(TagServiceInterface.UserTagType userTagType) {
        if (entitiesMap != null) {
            return entitiesMap.get(userTagType);
        }
        return null;
    }

}
