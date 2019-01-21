package app.mediabrainz.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.mediabrainz.R;
import app.mediabrainz.adapter.pager.UserTagPagerAdapter;
import app.mediabrainz.communicator.ShowTitleCommunicator;
import app.mediabrainz.viewModels.UserTagVM;


public class UserTagPagerFragment extends Fragment {

    public static final String USERNAME = "UserTagPagerFragment.USERNAME";
    public static final String USER_TAG = "UserTagPagerFragment.USER_TAG";

    private String username;
    private String userTag;
    private boolean isLoading;
    private boolean isError;

    private UserTagVM userTagVM;

    private ViewPager pagerView;
    private TabLayout tabsView;
    private View errorView;
    private View progressView;

    public static UserTagPagerFragment newInstance(@NonNull String username, @NonNull String tag) {
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

        return layout;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(USERNAME, username);
        outState.putString(USER_TAG, userTag);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            if (getArguments() != null) {
                username = getArguments().getString(USERNAME);
                userTag = getArguments().getString(USER_TAG);
            } else if (savedInstanceState != null) {
                username = savedInstanceState.getString(USERNAME);
                userTag = savedInstanceState.getString(USER_TAG);
            }

            if (!TextUtils.isEmpty(userTag) && !TextUtils.isEmpty(username)) {
                if (getContext() instanceof ShowTitleCommunicator) {
                    ((ShowTitleCommunicator) getContext()).getToolbarTopTitleView().setText(userTag);
                }
                userTagVM = ViewModelProviders.of(getActivity()).get(UserTagVM.class);

                userTagVM.entitiesMapResource.observe(this, resource -> {
                    if (resource == null) return;
                    switch (resource.getStatus()) {
                        case LOADING:
                            viewProgressLoading(true);
                            break;
                        case ERROR:
                            showConnectionWarning(resource.getThrowable());
                            break;
                        case SUCCESS:
                            viewProgressLoading(false);
                            configurePager();
                            break;
                        case INVALID:
                            load();
                            break;
                    }
                });
                load();
            }
        }
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
        viewProgressLoading(false);
        userTagVM.load(username, userTag);
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

}
