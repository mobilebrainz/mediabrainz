package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.mediabrainz.R;
import app.mediabrainz.adapter.pager.UserTagsPagerAdapter;
import app.mediabrainz.api.model.Tag;
import app.mediabrainz.communicator.GetGenresCommunicator;
import app.mediabrainz.communicator.GetTagsCommunicator;
import app.mediabrainz.communicator.GetUsernameCommunicator;
import app.mediabrainz.viewModels.UserTagsPagerVM;


public class UserTagsPagerFragment extends LazyFragment implements
        GetGenresCommunicator,
        GetTagsCommunicator {

    private String username;
    private List<Tag> genres = new ArrayList<>();
    private List<Tag> tags = new ArrayList<>();
    private boolean isLoading;
    private boolean isError;

    private UserTagsPagerVM userTagsPagerVM;

    private ViewPager pagerView;
    private TabLayout tabsView;
    private View errorView;
    private View progressView;
    private View noresultsView;

    public static UserTagsPagerFragment newInstance() {
        Bundle args = new Bundle();
        UserTagsPagerFragment fragment = new UserTagsPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflate(R.layout.fragment_pager_without_icons, container);

        errorView = layout.findViewById(R.id.errorView);
        progressView = layout.findViewById(R.id.progressView);
        noresultsView = layout.findViewById(R.id.noresultsView);
        pagerView = layout.findViewById(R.id.pagerView);
        tabsView = layout.findViewById(R.id.tabsView);

        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getContext() instanceof GetUsernameCommunicator &&
                (username = ((GetUsernameCommunicator) getContext()).getUsername()) != null) {

            userTagsPagerVM = getViewModel(UserTagsPagerVM.class);
            userTagsPagerVM.userTagsResource.observe(this, resource -> {
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
                        show(resource.getData());
                        break;
                    case INVALID:
                        userTagsPagerVM.load(username);
                        break;
                }
            });
            loadView();
        }
    }


    @Override
    protected void lazyLoad() {
        noresultsView.setVisibility(View.GONE);
        viewError(false);
        viewProgressLoading(false);
        if (userTagsPagerVM != null) {
            userTagsPagerVM.lazyLoad(username);
        }
    }

    private void show(Map<Tag.TagType, List<Tag>> tagMap) {
        if (tagMap == null) return;
        if (tagMap.get(Tag.TagType.GENRE).isEmpty() && tagMap.get(Tag.TagType.TAG).isEmpty()) {
            noresultsView.setVisibility(View.VISIBLE);
        } else {
            setGenres(tagMap.get(Tag.TagType.GENRE));
            setTags(tagMap.get(Tag.TagType.TAG));

            UserTagsPagerAdapter pagerAdapter = new UserTagsPagerAdapter(getChildFragmentManager(), getResources());
            pagerView.setAdapter(pagerAdapter);
            pagerView.setOffscreenPageLimit(pagerAdapter.getCount());
            tabsView.setupWithViewPager(pagerView);
            tabsView.setTabMode(TabLayout.MODE_FIXED);
            pagerAdapter.setupTabViews(tabsView);
        }
    }

    private void viewProgressLoading(boolean isView) {
        if (isView) {
            isLoading = true;
            progressView.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            progressView.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean isView) {
        if (isView) {
            isError = true;
            errorView.setVisibility(View.VISIBLE);
        } else {
            isError = false;
            errorView.setVisibility(View.GONE);
        }
    }

    private void showConnectionWarning(Throwable t) {
        //ShowUtil.showError(getContext(), t);
        viewProgressLoading(false);
        viewError(true);
        errorView.findViewById(R.id.retryButton).setOnClickListener(v -> lazyLoad());
    }

    @Override
    public List<Tag> getGenres() {
        return genres;
    }

    @Override
    public List<Tag> getTags() {
        return tags;
    }

    public void setGenres(List<Tag> genres) {
        this.genres = genres;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
