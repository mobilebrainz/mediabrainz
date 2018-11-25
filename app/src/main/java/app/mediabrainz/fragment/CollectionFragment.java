package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.AreaCollectionAdapter;
import app.mediabrainz.adapter.recycler.ArtistCollectionAdapter;
import app.mediabrainz.adapter.recycler.EventCollectionAdapter;
import app.mediabrainz.adapter.recycler.LabelCollectionAdapter;
import app.mediabrainz.adapter.recycler.PlaceCollectionAdapter;
import app.mediabrainz.adapter.recycler.RecordingCollectionAdapter;
import app.mediabrainz.adapter.recycler.ReleaseCollectionAdapter;
import app.mediabrainz.adapter.recycler.ReleaseGroupCollectionAdapter;
import app.mediabrainz.adapter.recycler.SeriesCollectionAdapter;
import app.mediabrainz.adapter.recycler.WorkCollectionAdapter;
import app.mediabrainz.api.model.Area;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.BaseLookupEntity;
import app.mediabrainz.api.model.Collection;
import app.mediabrainz.api.model.Event;
import app.mediabrainz.api.model.Label;
import app.mediabrainz.api.model.Place;
import app.mediabrainz.api.model.Recording;
import app.mediabrainz.api.model.Release;
import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.api.model.Series;
import app.mediabrainz.api.model.Work;
import app.mediabrainz.communicator.GetCollectionCommunicator;
import app.mediabrainz.communicator.GetUsernameCommunicator;
import app.mediabrainz.communicator.OnReleaseGroupCommunicator;
import app.mediabrainz.communicator.ShowFloatingActionButtonCommunicator;
import app.mediabrainz.communicator.ShowTitleCommunicator;
import app.mediabrainz.functions.Action;
import app.mediabrainz.intent.ActivityFactory;
import app.mediabrainz.util.ShowUtil;

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.MediaBrainzApp.oauth;
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


public class CollectionFragment extends Fragment {

    private Collection collection;
    private boolean isPrivate;

    private RecyclerView collectionRecycler;
    private View error;
    private View loading;
    private View noresults;

    public static CollectionFragment newInstance() {
        Bundle args = new Bundle();
        CollectionFragment fragment = new CollectionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_collection, container, false);

        String username = ((GetUsernameCommunicator) getContext()).getUsername();
        isPrivate = oauth.hasAccount() && username.equals(oauth.getName());

        error = layout.findViewById(R.id.error);
        loading = layout.findViewById(R.id.loading);
        noresults = layout.findViewById(R.id.noresults);
        collectionRecycler = layout.findViewById(R.id.collection_recycler);

        load();
        configCollectionRecycler();
        return layout;
    }

    private void configCollectionRecycler() {
        collectionRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        collectionRecycler.setItemViewCacheSize(50);
        collectionRecycler.setHasFixedSize(true);
    }

    public void load() {
        error.setVisibility(View.GONE);

        collection = ((GetCollectionCommunicator) getContext()).getCollection();
        ((ShowTitleCommunicator) getContext()).getTopTitle().setText(collection.getName());
        if (collection != null) {
            switch (collection.getEntityType()) {
                case AREA_ENTITY_TYPE:
                    loadAreaCollection();
                    break;

                case ARTIST_ENTITY_TYPE:
                    loadArtistCollection();
                    break;

                case EVENT_ENTITY_TYPE:
                    loadEventCollection();
                    break;

                case INSTRUMENT_ENTITY_TYPE:
                    // TODO: getWikidata instruments?
                    break;

                case LABEL_ENTITY_TYPE:
                    loadLabelCollection();
                    break;

                case PLACE_ENTITY_TYPE:
                    loadPlaceCollection();
                    break;

                case RECORDING_ENTITY_TYPE:
                    loadRecordingCollection();
                    break;

                case RELEASE_ENTITY_TYPE:
                    loadReleaseCollection();
                    break;

                case RELEASE_GROUP_ENTITY_TYPE:
                    loadReleaseGroupCollection();
                    break;

                case SERIES_ENTITY_TYPE:
                    loadSeriesCollection();
                    break;

                case WORK_ENTITY_TYPE:
                    loadWorkCollection();
                    break;
            }
        }
    }

    private void loadAreaCollection() {
        loading.setVisibility(View.VISIBLE);
        api.getAreasFromCollection(
                collection,
                areaBrowse -> {
                    loading.setVisibility(View.GONE);
                    if (isPrivate) {
                        ((ShowFloatingActionButtonCommunicator) getContext()).showFloatingActionButton(true, ShowFloatingActionButtonCommunicator.FloatingButtonType.EDIT_COLLECTION);
                    }
                    if (areaBrowse.getCount() > 0) {
                        List<Area> areas = areaBrowse.getAreas();
                        AreaCollectionAdapter adapter = new AreaCollectionAdapter(areas, isPrivate);
                        collectionRecycler.setAdapter(adapter);
                        if (isPrivate) {
                            //adapter.setHolderClickListener(position ->ActivityFactory.startAreaActivity(getContext(), areas.get(position).getId()));
                            adapter.setOnDeleteListener(position ->
                                    onDelete(areas.get(position), () -> {
                                        areas.remove(position);
                                        adapter.notifyItemRemoved(position);
                                        if (areas.size() == 0) {
                                            noresults.setVisibility(View.VISIBLE);
                                        }
                                    }));
                        }
                    } else {
                        noresults.setVisibility(View.VISIBLE);
                    }
                },
                this::showConnectionWarning,
                100, 0);
    }

    private void loadArtistCollection() {
        loading.setVisibility(View.VISIBLE);
        api.getArtistsFromCollection(
                collection,
                artistBrowse -> {
                    loading.setVisibility(View.GONE);
                    if (isPrivate) {
                        ((ShowFloatingActionButtonCommunicator) getContext()).showFloatingActionButton(true, ShowFloatingActionButtonCommunicator.FloatingButtonType.EDIT_COLLECTION);
                    }
                    if (artistBrowse.getCount() > 0) {
                        List<Artist> artists = artistBrowse.getArtists();
                        ArtistCollectionAdapter adapter = new ArtistCollectionAdapter(artists, isPrivate);
                        collectionRecycler.setAdapter(adapter);
                        adapter.setHolderClickListener(position ->
                                ActivityFactory.startArtistActivity(getContext(), artists.get(position).getId()));
                        if (isPrivate) {
                            adapter.setOnDeleteListener(position ->
                                    onDelete(artists.get(position), () -> {
                                        artists.remove(position);
                                        adapter.notifyItemRemoved(position);
                                        if (artists.size() == 0) {
                                            noresults.setVisibility(View.VISIBLE);
                                        }
                                    }));
                        }
                    } else {
                        noresults.setVisibility(View.VISIBLE);
                    }
                },
                this::showConnectionWarning,
                100, 0);
    }

    private void loadEventCollection() {
        loading.setVisibility(View.VISIBLE);
        api.getEventsFromCollection(
                collection,
                eventBrowse -> {
                    loading.setVisibility(View.GONE);
                    if (isPrivate) {
                        ((ShowFloatingActionButtonCommunicator) getContext()).showFloatingActionButton(true, ShowFloatingActionButtonCommunicator.FloatingButtonType.EDIT_COLLECTION);
                    }
                    if (eventBrowse.getCount() > 0) {
                        List<Event> events = eventBrowse.getEvents();
                        EventCollectionAdapter adapter = new EventCollectionAdapter(events, isPrivate);
                        collectionRecycler.setAdapter(adapter);
                        if (isPrivate) {
                            adapter.setOnDeleteListener(position ->
                                    onDelete(events.get(position), () -> {
                                        events.remove(position);
                                        adapter.notifyItemRemoved(position);
                                        if (events.size() == 0) {
                                            noresults.setVisibility(View.VISIBLE);
                                        }
                                    }));
                        }
                        //adapter.setHolderClickListener(position ->ActivityFactory.startEventActivity(getContext(), events.get(position).getId()));
                    } else {
                        noresults.setVisibility(View.VISIBLE);
                    }
                },
                this::showConnectionWarning,
                100, 0);
    }

    private void loadLabelCollection() {
        loading.setVisibility(View.VISIBLE);
        api.getLabelsFromCollection(
                collection,
                labelBrowse -> {
                    loading.setVisibility(View.GONE);
                    if (isPrivate) {
                        ((ShowFloatingActionButtonCommunicator) getContext()).showFloatingActionButton(true, ShowFloatingActionButtonCommunicator.FloatingButtonType.EDIT_COLLECTION);
                    }
                    if (labelBrowse.getCount() > 0) {
                        List<Label> labels = labelBrowse.getLabels();
                        LabelCollectionAdapter adapter = new LabelCollectionAdapter(labels, isPrivate);
                        collectionRecycler.setAdapter(adapter);
                        if (isPrivate) {
                            adapter.setOnDeleteListener(position ->
                                    onDelete(labels.get(position), () -> {
                                        labels.remove(position);
                                        adapter.notifyItemRemoved(position);
                                        if (labels.size() == 0) {
                                            noresults.setVisibility(View.VISIBLE);
                                        }
                                    }));
                        }
                        //adapter.setHolderClickListener(position ->ActivityFactory.startLabelActivity(getContext(), labels.get(position).getId()));
                    } else {
                        noresults.setVisibility(View.VISIBLE);
                    }
                },
                this::showConnectionWarning,
                100, 0);
    }

    private void loadPlaceCollection() {
        loading.setVisibility(View.VISIBLE);
        api.getPlacesFromCollection(
                collection,
                placeBrowse -> {
                    loading.setVisibility(View.GONE);
                    if (isPrivate) {
                        ((ShowFloatingActionButtonCommunicator) getContext()).showFloatingActionButton(true, ShowFloatingActionButtonCommunicator.FloatingButtonType.EDIT_COLLECTION);
                    }
                    if (placeBrowse.getCount() > 0) {
                        List<Place> places = placeBrowse.getPlaces();
                        PlaceCollectionAdapter adapter = new PlaceCollectionAdapter(places, isPrivate);
                        collectionRecycler.setAdapter(adapter);
                        if (isPrivate) {
                            adapter.setOnDeleteListener(position ->
                                    onDelete(places.get(position), () -> {
                                        places.remove(position);
                                        adapter.notifyItemRemoved(position);
                                        if (places.size() == 0) {
                                            noresults.setVisibility(View.VISIBLE);
                                        }
                                    }));
                        }
                        //adapter.setHolderClickListener(position ->ActivityFactory.startPlaceActivity(getContext(), places.get(position).getId()));
                    } else {
                        noresults.setVisibility(View.VISIBLE);
                    }
                },
                this::showConnectionWarning,
                100, 0);
    }

    private void loadRecordingCollection() {
        loading.setVisibility(View.VISIBLE);
        api.getRecordingsFromCollection(
                collection,
                recordingBrowse -> {
                    loading.setVisibility(View.GONE);
                    if (isPrivate) {
                        ((ShowFloatingActionButtonCommunicator) getContext()).showFloatingActionButton(true, ShowFloatingActionButtonCommunicator.FloatingButtonType.EDIT_COLLECTION);
                    }
                    if (recordingBrowse.getCount() > 0) {
                        List<Recording> recordings = recordingBrowse.getRecordings();
                        RecordingCollectionAdapter adapter = new RecordingCollectionAdapter(recordings, isPrivate);
                        collectionRecycler.setAdapter(adapter);
                        if (isPrivate) {
                            adapter.setOnDeleteListener(position ->
                                    onDelete(recordings.get(position), () -> {
                                        recordings.remove(position);
                                        adapter.notifyItemRemoved(position);
                                        if (recordings.size() == 0) {
                                            noresults.setVisibility(View.VISIBLE);
                                        }
                                    }));
                        }
                        adapter.setHolderClickListener(position ->
                                ActivityFactory.startRecordingActivity(getContext(), recordings.get(position).getId()));
                    } else {
                        noresults.setVisibility(View.VISIBLE);
                    }
                },
                this::showConnectionWarning,
                100, 0);
    }

    private void loadReleaseCollection() {
        loading.setVisibility(View.VISIBLE);
        api.getReleasesFromCollection(
                collection,
                releaseBrowse -> {
                    loading.setVisibility(View.GONE);
                    if (isPrivate) {
                        ((ShowFloatingActionButtonCommunicator) getContext()).showFloatingActionButton(true, ShowFloatingActionButtonCommunicator.FloatingButtonType.EDIT_COLLECTION);
                    }
                    if (releaseBrowse.getCount() > 0) {
                        List<Release> releases = releaseBrowse.getReleases();
                        ReleaseCollectionAdapter adapter = new ReleaseCollectionAdapter(releases, isPrivate);
                        collectionRecycler.setAdapter(adapter);
                        if (isPrivate) {
                            adapter.setOnDeleteListener(position ->
                                    onDelete(releases.get(position), () -> {
                                        releases.remove(position);
                                        adapter.notifyItemRemoved(position);
                                        if (releases.size() == 0) {
                                            noresults.setVisibility(View.VISIBLE);
                                        }
                                    }));
                        }
                        adapter.setHolderClickListener(position ->
                                ActivityFactory.startReleaseActivity(getContext(), releases.get(position).getId()));
                    } else {
                        noresults.setVisibility(View.VISIBLE);
                    }
                },
                this::showConnectionWarning,
                100, 0);
    }

    private void loadReleaseGroupCollection() {
        loading.setVisibility(View.VISIBLE);
        api.getReleaseGroupsFromCollection(
                collection,
                releaseGroupsBrowse -> {
                    loading.setVisibility(View.GONE);
                    if (isPrivate) {
                        ((ShowFloatingActionButtonCommunicator) getContext()).showFloatingActionButton(true, ShowFloatingActionButtonCommunicator.FloatingButtonType.EDIT_COLLECTION);
                    }
                    if (releaseGroupsBrowse.getCount() > 0) {
                        List<ReleaseGroup> releaseGroups = releaseGroupsBrowse.getReleaseGroups();
                        ReleaseGroupCollectionAdapter adapter = new ReleaseGroupCollectionAdapter(releaseGroups, isPrivate);
                        collectionRecycler.setAdapter(adapter);
                        if (isPrivate) {
                            adapter.setOnDeleteListener(position ->
                                    onDelete(releaseGroups.get(position), () -> {
                                        releaseGroups.remove(position);
                                        adapter.notifyItemRemoved(position);
                                        if (releaseGroups.size() == 0) {
                                            noresults.setVisibility(View.VISIBLE);
                                        }
                                    }));
                        }
                        adapter.setHolderClickListener(position ->
                                ((OnReleaseGroupCommunicator) getContext()).onReleaseGroup(releaseGroups.get(position).getId()));
                    } else {
                        noresults.setVisibility(View.VISIBLE);
                    }
                },
                this::showConnectionWarning,
                100, 0);
    }

    private void loadSeriesCollection() {
        loading.setVisibility(View.VISIBLE);
        api.getSeriesFromCollection(
                collection,
                seriesBrowse -> {
                    loading.setVisibility(View.GONE);
                    if (isPrivate) {
                        ((ShowFloatingActionButtonCommunicator) getContext()).showFloatingActionButton(true, ShowFloatingActionButtonCommunicator.FloatingButtonType.EDIT_COLLECTION);
                    }
                    if (seriesBrowse.getCount() > 0) {
                        List<Series> serieses = seriesBrowse.getSeries();
                        SeriesCollectionAdapter adapter = new SeriesCollectionAdapter(serieses, isPrivate);
                        collectionRecycler.setAdapter(adapter);
                        if (isPrivate) {
                            adapter.setOnDeleteListener(position ->
                                    onDelete(serieses.get(position), () -> {
                                        serieses.remove(position);
                                        adapter.notifyItemRemoved(position);
                                        if (serieses.size() == 0) {
                                            noresults.setVisibility(View.VISIBLE);
                                        }
                                    }));
                        }
                        //adapter.setHolderClickListener(position -> ActivityFactory.startSeriesActivity(getContext(), serieses.get(position).getId()));
                    } else {
                        noresults.setVisibility(View.VISIBLE);
                    }
                },
                this::showConnectionWarning,
                100, 0);
    }

    private void loadWorkCollection() {
        loading.setVisibility(View.VISIBLE);
        api.getWorksFromCollection(
                collection,
                workBrowse -> {
                    loading.setVisibility(View.GONE);
                    if (isPrivate) {
                        ((ShowFloatingActionButtonCommunicator) getContext()).showFloatingActionButton(true, ShowFloatingActionButtonCommunicator.FloatingButtonType.EDIT_COLLECTION);
                    }
                    if (workBrowse.getCount() > 0) {
                        List<Work> works = workBrowse.getWorks();
                        WorkCollectionAdapter adapter = new WorkCollectionAdapter(works, isPrivate);
                        collectionRecycler.setAdapter(adapter);
                        if (isPrivate) {
                            adapter.setOnDeleteListener(position ->
                                    onDelete(works.get(position), () -> {
                                        works.remove(position);
                                        adapter.notifyItemRemoved(position);
                                        if (works.size() == 0) {
                                            noresults.setVisibility(View.VISIBLE);
                                        }
                                    }));
                        }
                        //adapter.setHolderClickListener(position ->ActivityFactory.startWorkActivity(getContext(), works.get(position).getId()));
                    } else {
                        noresults.setVisibility(View.VISIBLE);
                    }
                },
                this::showConnectionWarning,
                100, 0);
    }

    private void showConnectionWarning(Throwable t) {
        //ShowUtil.showError(getActivity(), t);
        loading.setVisibility(View.GONE);
        error.setVisibility(View.VISIBLE);
        error.findViewById(R.id.retry_button).setOnClickListener(v -> load());
    }

    public void onDelete(BaseLookupEntity entity, Action action) {
        View titleView = getLayoutInflater().inflate(R.layout.layout_custom_alert_dialog_title, null);
        TextView titleText = titleView.findViewById(R.id.title_text);
        titleText.setText(R.string.collection_delete_entity);

        new AlertDialog.Builder(getContext())
                .setCustomTitle(titleView)
                .setMessage(getString(R.string.delete_alert_message))
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    loading.setVisibility(View.VISIBLE);
                    if (!api.deleteEntityFromCollection(
                            collection, entity,
                            metadata -> {
                                loading.setVisibility(View.GONE);
                                if (metadata.getMessage().getText().equals("OK")) {
                                    action.run();
                                } else {
                                    ShowUtil.showMessage(getActivity(), "Error");
                                }
                            },
                            this::showConnectionWarning)) {
                        loading.setVisibility(View.GONE);
                    }
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.cancel())
                .show();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isPrivate) {
            ((ShowFloatingActionButtonCommunicator) getContext()).showFloatingActionButton(false, ShowFloatingActionButtonCommunicator.FloatingButtonType.EDIT_COLLECTION);
        }
    }

}
