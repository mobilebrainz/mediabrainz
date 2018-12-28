package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.expandedRecycler.Section;
import app.mediabrainz.adapter.recycler.tracks.Header;
import app.mediabrainz.adapter.recycler.tracks.TracksAdapter;
import app.mediabrainz.api.model.Media;
import app.mediabrainz.api.model.Recording;
import app.mediabrainz.api.model.Release;
import app.mediabrainz.communicator.GetReleaseCommunicator;
import app.mediabrainz.communicator.OnPlayYoutubeCommunicator;
import app.mediabrainz.communicator.OnRecordingCommunicator;

import static app.mediabrainz.MediaBrainzApp.api;


public class ReleaseTracksFragment extends BaseComplexRecyclerFragment<Media.Track> {

    private Release release;

    private View errorView;
    private View progressView;
    private View noresultsView;

    public static ReleaseTracksFragment newInstance() {
        Bundle args = new Bundle();
        ReleaseTracksFragment fragment = new ReleaseTracksFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = super.onCreateView(inflater, container, savedInstanceState);

        recyclerContainerView.setVisibility(View.INVISIBLE);
        View frame = inflater.inflate(R.layout.fragment_release_tracks_frame, null);
        errorView = frame.findViewById(R.id.errorView);
        progressView = frame.findViewById(R.id.progressView);
        noresultsView = frame.findViewById(R.id.noresultsView);
        addFrameView(frame);

        loadView();
        return layout;
    }

    @Override
    protected void lazyLoad() {
        noresultsView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        recyclerView.removeAllViewsInLayout();

        release = ((GetReleaseCommunicator) getContext()).getRelease();
        if (release != null) {
            List<Media> medias = release.getMedia();
            if (medias != null && !medias.isEmpty() && !medias.get(0).getTracks().isEmpty()) {
                loadRecordingRatings();
            } else {
                showNoResult(true);
            }
        }
    }

    private void showNoResult(boolean show) {
        if (show) {
            noresultsView.setVisibility(View.VISIBLE);
            recyclerContainerView.setVisibility(View.GONE);
        } else {
            noresultsView.setVisibility(View.GONE);
            recyclerContainerView.setVisibility(View.VISIBLE);
        }
    }

    private void loadRecordingRatings() {
        progressView.setVisibility(View.VISIBLE);
        api.getRecordingRatingsByRelease(
                release.getId(),
                resultRecordings -> {
                    progressView.setVisibility(View.GONE);
                    List<Media> medias = release.getMedia();
                    if (resultRecordings.getCount() > 0) {
                        List<Recording> recordings = resultRecordings.getRecordings();
                        for (Media media : medias) {
                            for (Media.Track track : media.getTracks()) {
                                for (Recording recording : recordings) {
                                    if (track.getRecording().equals(recording)) {
                                        track.setRecording(recording);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    displayResult(medias);
                },
                this::showConnectionWarning,
                100, 0
        );
    }

    private void displayResult(List<Media> medias) {
        List<Section<Media.Track>> sections = new ArrayList<>();
        for (Media media : medias) {
            Header header = new Header();
            header.setExpand(true);

            Section<Media.Track> section = new Section<>(header);

            String format = media.getFormat();
            String title = media.getTitle();
            if (medias.size() > 1 || !TextUtils.isEmpty(format) || !TextUtils.isEmpty(title)) {
                String position = medias.size() > 1 ? " " + media.getPosition() : "";
                title = !TextUtils.isEmpty(title) ? " " + title : "";
                format = !TextUtils.isEmpty(format) ? format : getString(R.string.fm_unknown);
                String str = format + position + title;
                header.setTitle(str);
            }
            long length = 0;
            List<Media.Track> tracks = media.getTracks();
            if (tracks != null && !tracks.isEmpty()) {
                for (Media.Track track : tracks) {
                    length += track.getLength() != null ? track.getLength() : 0;
                }
                header.setLength(length);
                section.getItems().addAll(tracks);
                sections.add(section);
            }

            viewSections = new ArrayList<>();
            for (Section<Media.Track> s : sections) {
                if (!s.getItems().isEmpty()) {
                    viewSections.add(s);
                }
            }
            if (!viewSections.isEmpty()) {
                showNoResult(false);
                configRecycler(viewSections);
            } else {
                showNoResult(true);
            }
        }
    }

    private void configRecycler(List<Section<Media.Track>> items) {
        restoreRecyclerToolbarState(items);

        TracksAdapter tracksAdapter = new TracksAdapter(items);
        recyclerAdapter = tracksAdapter;
        tracksAdapter.setOnItemClickListener(track ->
                ((OnRecordingCommunicator) getContext()).onRecording(track.getRecording().getId()));
        tracksAdapter.setOnPlayYoutubeListener(track ->
                ((OnPlayYoutubeCommunicator) getContext()).onPlay(track.getRecording().getTitle()));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemViewCacheSize(50);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(tracksAdapter);

        configRecyclerToolbar();
    }

    public void configRecyclerToolbar() {
        super.configRecyclerToolbar();
        if (recyclerAdapter.getSections().size() > 1) {
            expandCheckBox.setChecked(true);
        }
    }

    private void showConnectionWarning(Throwable t) {
        progressView.setVisibility(View.GONE);
        //ShowUtil.showError(getActivity(), t);
        errorView.setVisibility(View.VISIBLE);
        errorView.findViewById(R.id.retryButton).setOnClickListener(v -> lazyLoad());
    }

}
