package app.mediabrainz.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import app.mediabrainz.R;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.Recording;
import app.mediabrainz.communicator.GetRecordingCommunicator;

import java.util.List;

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.api.externalResources.lyrics.model.LyricsApi.LYRICS_INSTRUMENTAL;
import static app.mediabrainz.api.externalResources.lyrics.model.LyricsApi.LYRICS_NOT_FOUND;


public class RecordingLyricsFragment extends LazyFragment {

    protected boolean isLoading;
    protected boolean isError;

    private View contentView;
    private View errorView;
    private View progressView;
    private View noresultsView;
    private TextView lyricsView;
    private Button showSiteButton;

    public static RecordingLyricsFragment newInstance() {
        Bundle args = new Bundle();
        RecordingLyricsFragment fragment = new RecordingLyricsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_recording_lyrics, container, false);

        contentView = layout.findViewById(R.id.contentView);
        errorView = layout.findViewById(R.id.errorView);
        progressView = layout.findViewById(R.id.progressView);
        noresultsView = layout.findViewById(R.id.noresultsView);
        lyricsView = layout.findViewById(R.id.lyricsView);
        showSiteButton = layout.findViewById(R.id.showSiteButton);

        loadView();
        return layout;
    }

    @Override
    public void lazyLoad() {
        contentView.setVisibility(View.GONE);
        progressView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        noresultsView.setVisibility(View.GONE);

        Recording recording = ((GetRecordingCommunicator) getContext()).getRecording();
        if (recording != null) {
            List<Artist.ArtistCredit> artists = recording.getArtistCredits();
            if (!artists.isEmpty()) {
                // Получить лирику парсингом сайта http://lyrics.wikia.com.
                viewProgressLoading(true);
                // Получить лирику из сервиса http://lyrics.wikia.com/wikia.php
                // Нестабильно даёт результат. Лучше парсить http://lyrics.wikia.com
                // пример: Deep Purple Smoke On The Water. С сервиса - errorView, с парсинга - текст песни.
                /*
                api.getLyricsWikia(
                        artists.get(0).getName(), recording.getTitle(),
                        lyricsResult -> {
                            lyrics.setText(lyricsResult.getResult().getLyrics());
                            viewProgressLoading(false);
                        },
                        t -> {
                            viewProgressLoading(false);
                            if (LyricsService.isNotFound(((HttpException) t).getContent())) {
                                noresults.setVisibility(View.VISIBLE);
                                contentView.setVisibility(View.GONE);
                            } else {
                                showConnectionWarning(t);
                            }
                        });
                    */
                api.getLyricsWikiaApi(
                        artists.get(0).getName(), recording.getTitle(),
                        lyricsApi -> {
                            viewProgressLoading(false);
                            String text = lyricsApi.getLyrics();
                            if (text.equals(LYRICS_NOT_FOUND) || text.equals(LYRICS_INSTRUMENTAL)) {
                                noresultsView.setVisibility(View.VISIBLE);
                                contentView.setVisibility(View.GONE);
                            } else {
                                lyricsView.setText(text);
                                showSiteButton.setOnClickListener(v -> {
                                    if (!isLoading) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(lyricsApi.getUrl())));
                                    }
                                });
                            }
                        },
                        this::showConnectionWarning);

            } else {
                noresultsView.setVisibility(View.VISIBLE);
                contentView.setVisibility(View.GONE);
            }
        }
    }

    private void viewProgressLoading(boolean view) {
        if (view) {
            isLoading = true;
            contentView.setVisibility(View.GONE);
            progressView.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            contentView.setVisibility(View.VISIBLE);
            progressView.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean view) {
        if (view) {
            isError = true;
            contentView.setVisibility(View.INVISIBLE);
            errorView.setVisibility(View.VISIBLE);
        } else {
            isError = false;
            errorView.setVisibility(View.GONE);
            contentView.setVisibility(View.VISIBLE);
        }
    }

    private void showConnectionWarning(Throwable t) {
        //ShowUtil.showError(getActivity(), t);
        viewProgressLoading(false);
        viewError(true);
        errorView.findViewById(R.id.retryButton).setOnClickListener(v -> lazyLoad());
    }

}
