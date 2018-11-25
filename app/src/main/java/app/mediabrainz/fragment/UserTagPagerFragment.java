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

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private View error;
    private View loading;

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
        View layout = inflater.inflate(R.layout.fragment_user_tag_pager, container, false);

        viewPager = layout.findViewById(R.id.pager);
        tabLayout = layout.findViewById(R.id.tabs);
        error = layout.findViewById(R.id.error);
        loading = layout.findViewById(R.id.loading);

        username = getArguments().getString(USERNAME);
        userTag = getArguments().getString(USER_TAG);

        ((ShowTitleCommunicator) getContext()).getTopTitle().setText(userTag);

        load();
        return layout;
    }

    private void configurePager() {
        UserTagPagerAdapter pagerAdapter = new UserTagPagerAdapter(getChildFragmentManager(), getResources());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(pagerAdapter.getCount());
        tabLayout.setupWithViewPager(viewPager);
        pagerAdapter.setupTabViews(tabLayout);
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
            viewPager.setAlpha(0.3F);
            loading.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            viewPager.setAlpha(1.0F);
            loading.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean isView) {
        if (isView) {
            isError = true;
            viewPager.setVisibility(View.INVISIBLE);
            error.setVisibility(View.VISIBLE);
        } else {
            isError = false;
            error.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
        }
    }

    private void showConnectionWarning(Throwable t) {
        //ShowUtil.showError(getContext(), t);
        viewProgressLoading(false);
        viewError(true);
        error.findViewById(R.id.retry_button).setOnClickListener(v -> load());
    }

    @Override
    public List<TagEntity> getEntities(TagServiceInterface.UserTagType userTagType) {
        if (entitiesMap != null) {
            return entitiesMap.get(userTagType);
        }
        return null;
    }

}
