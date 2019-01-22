package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.CollectionsAdapter;
import app.mediabrainz.api.model.Collection;
import app.mediabrainz.communicator.GetCollectionsCommunicator;
import app.mediabrainz.communicator.GetUsernameCommunicator;
import app.mediabrainz.communicator.OnCollectionCommunicator;
import app.mediabrainz.util.ShowUtil;
import app.mediabrainz.viewModels.CollectionsTabVM;

import static app.mediabrainz.MediaBrainzApp.oauth;
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


public class CollectionsTabFragment extends BaseFragment {

    private static final String COLLECTION_TAB = "COLLECTION_TAB";

    private int deletedPos;
    private int collectionTab;
    private boolean isPrivate;
    private CollectionsTabVM collectionsTabVM;
    private CollectionsAdapter adapter;
    private List<Collection> tabCollections;

    private View progressView;
    private RecyclerView recyclerView;

    public static CollectionsTabFragment newInstance(int collectionTab) {
        Bundle args = new Bundle();
        args.putInt(COLLECTION_TAB, collectionTab);
        CollectionsTabFragment fragment = new CollectionsTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflate(R.layout.fragment_recycler_view, container);

        collectionTab = getArguments().getInt(COLLECTION_TAB);
        recyclerView = layout.findViewById(R.id.recyclerView);
        progressView = layout.findViewById(R.id.progressView);

        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String username;
        if (getContext() instanceof GetUsernameCommunicator &&
                (username = ((GetUsernameCommunicator) getContext()).getUsername()) != null) {

            isPrivate = oauth.hasAccount() && username.equals(oauth.getName());

            collectionsTabVM = getViewModel(CollectionsTabVM.class);
            collectionsTabVM.deleteEvent.observeEvent(this, resource -> {
                if (resource == null) return;
                switch (resource.getStatus()) {
                    case LOADING:
                        viewProgressLoading(true);
                        break;
                    case ERROR:
                        viewProgressLoading(false);
                        if (resource.getThrowable() != null) {
                            toast(resource.getThrowable().getMessage());
                        }
                        break;
                    case SUCCESS:
                        viewProgressLoading(false);
                        Collection collection = resource.getData();
                        if (collection != null) {
                            tabCollections.remove(collection);
                            if (adapter != null) {
                                adapter.notifyItemRemoved(deletedPos);
                            }
                        }
                        //userCollectionsSharedVM.invalidateUserCollections();
                        break;
                }
            });
            show();
        }
    }

    public void show() {
        viewProgressLoading(false);

        List<Collection> collections = ((GetCollectionsCommunicator) getParentFragment()).getCollections();
        tabCollections = new ArrayList<>();
        if (collections != null) {
            for (Collection collection : collections) {
                if ((AREA_ENTITY_TYPE.equals(collection.getEntityType()) && collectionTab == AREAS.ordinal())
                        || (ARTIST_ENTITY_TYPE.equals(collection.getEntityType()) && collectionTab == ARTISTS.ordinal())
                        || (EVENT_ENTITY_TYPE.equals(collection.getEntityType()) && collectionTab == EVENTS.ordinal())
                        || (INSTRUMENT_ENTITY_TYPE.equals(collection.getEntityType()) && collectionTab == INSTRUMENTS.ordinal())
                        || (LABEL_ENTITY_TYPE.equals(collection.getEntityType()) && collectionTab == LABELS.ordinal())
                        || (PLACE_ENTITY_TYPE.equals(collection.getEntityType()) && collectionTab == PLACES.ordinal())
                        || (RECORDING_ENTITY_TYPE.equals(collection.getEntityType()) && collectionTab == RECORDINGS.ordinal())
                        || (RELEASE_ENTITY_TYPE.equals(collection.getEntityType()) && collectionTab == RELEASES.ordinal())
                        || (RELEASE_GROUP_ENTITY_TYPE.equals(collection.getEntityType()) && collectionTab == RELEASE_GROUPS.ordinal())
                        || (SERIES_ENTITY_TYPE.equals(collection.getEntityType()) && collectionTab == SERIES.ordinal())
                        || (WORK_ENTITY_TYPE.equals(collection.getEntityType()) && collectionTab == WORKS.ordinal())) {
                    tabCollections.add(collection);
                }
            }
            if (!tabCollections.isEmpty()) {
                adapter = new CollectionsAdapter(tabCollections, isPrivate);

                adapter.setHolderClickListener(pos ->
                        ((OnCollectionCommunicator) getContext()).onCollection(tabCollections.get(pos)));

                if (isPrivate) {
                    adapter.setOnDeleteCollectionListener(
                            pos -> {
                                deletedPos = pos;
                                Collection collection = tabCollections.get(pos);
                                View titleView = getLayoutInflater().inflate(R.layout.layout_custom_alert_dialog_title, null);
                                TextView titleTextView = titleView.findViewById(R.id.titleTextView);
                                titleTextView.setText(getString(R.string.collection_alert_title, collection.getName()));

                                new AlertDialog.Builder(getContext())
                                        .setCustomTitle(titleView)
                                        .setMessage(getString(R.string.delete_alert_message))
                                        .setPositiveButton(android.R.string.yes, (dialog, which) -> collectionsTabVM.deleteCollection(collection))
                                        .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.cancel())
                                        .show();
                            });


                }
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setItemViewCacheSize(100);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapter);
            }
        }
    }

    private void viewProgressLoading(boolean isView) {
        if (isView) {
            recyclerView.setAlpha(0.3F);
            progressView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setAlpha(1.0F);
            progressView.setVisibility(View.GONE);
        }
    }
}
