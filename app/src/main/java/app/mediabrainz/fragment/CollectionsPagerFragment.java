package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.mediabrainz.R;
import app.mediabrainz.adapter.pager.BaseFragmentPagerAdapter;
import app.mediabrainz.adapter.pager.CollectionsPagerAdapter;
import app.mediabrainz.api.model.Collection;
import app.mediabrainz.communicator.GetCollectionsCommunicator;
import app.mediabrainz.communicator.GetUsernameCommunicator;

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.adapter.pager.CollectionsPagerAdapter.CollectionTab.AREAS;
import static app.mediabrainz.adapter.pager.CollectionsPagerAdapter.CollectionTab.ARTISTS;
import static app.mediabrainz.adapter.pager.CollectionsPagerAdapter.CollectionTab.EVENTS;
import static app.mediabrainz.adapter.pager.CollectionsPagerAdapter.CollectionTab.INSTRUMENTS;
import static app.mediabrainz.adapter.pager.CollectionsPagerAdapter.CollectionTab.LABELS;
import static app.mediabrainz.adapter.pager.CollectionsPagerAdapter.CollectionTab.PLACES;
import static app.mediabrainz.adapter.pager.CollectionsPagerAdapter.CollectionTab.RECORDINGS;
import static app.mediabrainz.adapter.pager.CollectionsPagerAdapter.CollectionTab.RELEASES;
import static app.mediabrainz.adapter.pager.CollectionsPagerAdapter.CollectionTab.RELEASE_GROUPS;
import static app.mediabrainz.adapter.pager.CollectionsPagerAdapter.CollectionTab.SERIES;
import static app.mediabrainz.adapter.pager.CollectionsPagerAdapter.CollectionTab.WORKS;
import static app.mediabrainz.api.model.Collection.AREA_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.ARTIST_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.EVENT_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.INSTRUMENT_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.LABEL_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.PLACE_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.RECORDING_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.RELEASE_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.RELEASE_GROUP_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.SERIES_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.WORK_ENTITY_TYPE;


public class CollectionsPagerFragment extends LazyFragment implements
        GetCollectionsCommunicator,
        BaseFragmentPagerAdapter.Updatable {

    public interface CollectionTabOrdinalCommunicator {
        int getCollectionTabOrdinal();
    }

    private boolean isLoading;
    private boolean isError;
    private List<Collection> collections;

    private ViewPager pagerView;
    private TabLayout tabsView;
    private View errorView;
    private View progressView;
    private View noresultsView;

    public static CollectionsPagerFragment newInstance() {
        Bundle args = new Bundle();
        CollectionsPagerFragment fragment = new CollectionsPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_collections_pager, container, false);

        pagerView = layout.findViewById(R.id.pagerView);
        tabsView = layout.findViewById(R.id.tabsView);
        errorView = layout.findViewById(R.id.errorView);
        progressView = layout.findViewById(R.id.progressView);
        noresultsView = layout.findViewById(R.id.noresultsView);

        loadView();
        return layout;
    }

    @Override
    public void update() {
        lazyLoad();
    }

    private void configurePager(List<CollectionsPagerAdapter.CollectionTab> collectionTabs) {


        CollectionsPagerAdapter pagerAdapter = new CollectionsPagerAdapter(getChildFragmentManager(), getResources(), collectionTabs);
        pagerView.setAdapter(pagerAdapter);
        pagerView.setOffscreenPageLimit(pagerAdapter.getCount());
        tabsView.setupWithViewPager(pagerView);
        pagerAdapter.setupTabViews(tabsView);

        int collectionTabOrdinal = ((CollectionTabOrdinalCommunicator) getContext()).getCollectionTabOrdinal();

        for (int i = 0; i < collectionTabs.size(); i++) {
            if (collectionTabs.get(i).ordinal() == collectionTabOrdinal) {
                pagerView.setCurrentItem(i);
                break;
            }
        }
    }

    @Override
    protected void lazyLoad() {
        noresultsView.setVisibility(View.GONE);
        viewError(false);
        String username = ((GetUsernameCommunicator) getContext()).getUsername();

        //TODO: make .browse(n, m)?
        viewProgressLoading(true);
        api.getCollections(
                username,
                collectionBrowse -> {
                    viewProgressLoading(false);
                    if (collectionBrowse.getCount() > 0) {
                        collections = collectionBrowse.getCollections();

                        List<CollectionsPagerAdapter.CollectionTab> collectionTabs = new ArrayList<>();
                        for (Collection collection : collections) {
                            switch (collection.getEntityType()) {
                                case AREA_ENTITY_TYPE:
                                    collection.setCount(collection.getAreaCount());
                                    if (!collectionTabs.contains(AREAS)) {
                                        collectionTabs.add(AREAS);
                                    }
                                    break;
                                case ARTIST_ENTITY_TYPE:
                                    collection.setCount(collection.getArtistCount());
                                    if (!collectionTabs.contains(ARTISTS)) {
                                        collectionTabs.add(ARTISTS);
                                    }
                                    break;
                                case EVENT_ENTITY_TYPE:
                                    collection.setCount(collection.getEventCount());
                                    if (!collectionTabs.contains(EVENTS)) {
                                        collectionTabs.add(EVENTS);
                                    }
                                    break;
                                case INSTRUMENT_ENTITY_TYPE:
                                    collection.setCount(collection.getInstrumentCount());
                                    if (!collectionTabs.contains(INSTRUMENTS)) {
                                        collectionTabs.add(INSTRUMENTS);
                                    }
                                    break;
                                case LABEL_ENTITY_TYPE:
                                    collection.setCount(collection.getLabelCount());
                                    if (!collectionTabs.contains(LABELS)) {
                                        collectionTabs.add(LABELS);
                                    }
                                    break;
                                case PLACE_ENTITY_TYPE:
                                    collection.setCount(collection.getPlaceCount());
                                    if (!collectionTabs.contains(PLACES)) {
                                        collectionTabs.add(PLACES);
                                    }
                                    break;
                                case RECORDING_ENTITY_TYPE:
                                    collection.setCount(collection.getRecordingCount());
                                    if (!collectionTabs.contains(RECORDINGS)) {
                                        collectionTabs.add(RECORDINGS);
                                    }
                                    break;
                                case RELEASE_ENTITY_TYPE:
                                    collection.setCount(collection.getReleaseCount());
                                    if (!collectionTabs.contains(RELEASES)) {
                                        collectionTabs.add(RELEASES);
                                    }
                                    break;
                                case RELEASE_GROUP_ENTITY_TYPE:
                                    collection.setCount(collection.getReleaseGroupCount());
                                    if (!collectionTabs.contains(RELEASE_GROUPS)) {
                                        collectionTabs.add(RELEASE_GROUPS);
                                    }
                                    break;
                                case SERIES_ENTITY_TYPE:
                                    collection.setCount(collection.getSeriesCount());
                                    if (!collectionTabs.contains(SERIES)) {
                                        collectionTabs.add(SERIES);
                                    }
                                    break;
                                case WORK_ENTITY_TYPE:
                                    collection.setCount(collection.getWorkCount());
                                    if (!collectionTabs.contains(WORKS)) {
                                        collectionTabs.add(WORKS);
                                    }
                                    break;
                            }
                        }
                        Collections.sort(collectionTabs, (t1, t2) -> t1.ordinal() - t2.ordinal());
                        if (collectionTabs.size() < 5) {
                            tabsView.setTabMode(TabLayout.MODE_FIXED);
                        } else {
                            tabsView.setTabMode(TabLayout.MODE_SCROLLABLE);
                        }
                        configurePager(collectionTabs);
                    } else {
                        noresultsView.setVisibility(View.VISIBLE);
                    }
                },
                this::showConnectionWarning,
                100, 0);
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
        errorView.findViewById(R.id.retryButton).setOnClickListener(v -> update());
    }


    @Override
    public List<Collection> getCollections() {
        return collections;
    }

    public ViewPager getPagerView() {
        return pagerView;
    }

}
