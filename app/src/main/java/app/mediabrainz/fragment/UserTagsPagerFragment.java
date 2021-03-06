package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.mediabrainz.R;
import app.mediabrainz.adapter.pager.UserTagsPagerAdapter;
import app.mediabrainz.api.model.Tag;
import app.mediabrainz.communicator.GetGenresCommunicator;
import app.mediabrainz.communicator.GetTagsCommunicator;
import app.mediabrainz.communicator.GetUsernameCommunicator;

import java.util.ArrayList;
import java.util.List;

import static app.mediabrainz.MediaBrainzApp.api;


public class UserTagsPagerFragment extends LazyFragment implements
        GetGenresCommunicator,
        GetTagsCommunicator {

    private List<Tag> genres = new ArrayList<>();
    private List<Tag> tags = new ArrayList<>();
    private boolean isLoading;
    private boolean isError;

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
        View layout = inflater.inflate(R.layout.fragment_pager_without_icons, container, false);

        errorView = layout.findViewById(R.id.errorView);
        progressView = layout.findViewById(R.id.progressView);
        noresultsView = layout.findViewById(R.id.noresultsView);
        pagerView = layout.findViewById(R.id.pagerView);
        tabsView = layout.findViewById(R.id.tabsView);

        loadView();
        return layout;
    }

    @Override
    protected void lazyLoad() {
        viewError(false);
        noresultsView.setVisibility(View.GONE);

        String username = ((GetUsernameCommunicator) getContext()).getUsername();
        if (username != null) {
            viewProgressLoading(true);

            api.getTags(username,
                    tagMap -> {
                        viewProgressLoading(false);
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
                    },
                    this::showConnectionWarning);
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
