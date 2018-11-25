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
import static app.mediabrainz.api.lyrics.model.LyricsApi.LYRICS_INSTRUMENTAL;
import static app.mediabrainz.api.lyrics.model.LyricsApi.LYRICS_NOT_FOUND;


public class RecordingLyricsFragment extends LazyFragment {

    protected boolean isLoading;
    protected boolean isError;

    private View content;
    private View error;
    private View loading;
    private View noresults;
    private TextView lyrics;
    private Button showSiteBtn;

    public static RecordingLyricsFragment newInstance() {
        Bundle args = new Bundle();
        RecordingLyricsFragment fragment = new RecordingLyricsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_recording_lyrics, container, false);

        content = layout.findViewById(R.id.content);
        error = layout.findViewById(R.id.error);
        loading = layout.findViewById(R.id.loading);
        noresults = layout.findViewById(R.id.noresults);
        lyrics = layout.findViewById(R.id.lyrics);
        showSiteBtn = layout.findViewById(R.id.show_site);

        loadView();
        return layout;
    }

    @Override
    public void lazyLoad() {
        content.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
        noresults.setVisibility(View.GONE);

        Recording recording = ((GetRecordingCommunicator) getContext()).getRecording();
        if (recording != null) {
            List<Artist.ArtistCredit> artists = recording.getArtistCredits();
            if (!artists.isEmpty()) {
                // Получить лирику парсингом сайта http://lyrics.wikia.com.
                viewProgressLoading(true);
                // Получить лирику из сервиса http://lyrics.wikia.com/wikia.php
                // Нестабильно даёт результат. Лучше парсить http://lyrics.wikia.com
                // пример: Deep Purple Smoke On The Water. С сервиса - error, с парсинга - текст песни.
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
                                content.setVisibility(View.GONE);
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
                                noresults.setVisibility(View.VISIBLE);
                                content.setVisibility(View.GONE);
                            } else {
                                lyrics.setText(text);
                                showSiteBtn.setOnClickListener(v -> {
                                    if (!isLoading) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(lyricsApi.getUrl())));
                                    }
                                });
                            }
                        },
                        this::showConnectionWarning);

            } else {
                noresults.setVisibility(View.VISIBLE);
                content.setVisibility(View.GONE);
            }
        }
    }

    private void viewProgressLoading(boolean view) {
        if (view) {
            isLoading = true;
            content.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            content.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean view) {
        if (view) {
            isError = true;
            content.setVisibility(View.INVISIBLE);
            error.setVisibility(View.VISIBLE);
        } else {
            isError = false;
            error.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
        }
    }

    private void showConnectionWarning(Throwable t) {
        //ShowUtil.showError(getActivity(), t);
        viewProgressLoading(false);
        viewError(true);
        error.findViewById(R.id.retry_button).setOnClickListener(v -> lazyLoad());
    }

}
