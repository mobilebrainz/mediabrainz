package app.mediabrainz.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import java.util.List;

import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.R;
import app.mediabrainz.api.externalResources.progarchives.ProgarchivesResponse;
import app.mediabrainz.api.externalResources.progarchives.ProgarchivesService;
import app.mediabrainz.api.externalResources.rateyourmusic.RateyourmusicResponse;
import app.mediabrainz.api.externalResources.rateyourmusic.RateyourmusicService;
import app.mediabrainz.api.externalResources.lastfm.model.Album;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.Rating;
import app.mediabrainz.api.model.Release;
import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.api.model.Url;
import app.mediabrainz.communicator.GetReleaseCommunicator;
import app.mediabrainz.communicator.GetRequestQueueCommunicator;
import app.mediabrainz.communicator.GetUrlsCommunicator;
import app.mediabrainz.intent.ActivityFactory;
import app.mediabrainz.util.ShowUtil;
import app.mediabrainz.util.StringFormat;

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.MediaBrainzApp.oauth;


public class ReleaseRatingsFragment extends LazyFragment {

    public static final String TAG = "ReleaseRatingsFragment";
    private final float LASTFM_ALBUM_LISTENERS_COEFF = 75;
    private final float LASTFM_ALBUM_PLAYCOUNT_COEFF = 300;

    private ReleaseGroup releaseGroup;
    private boolean isLoadRatingsEnabled;

    private View contentView;
    private View errorView;
    private View progressView;
    private TextView loginWarningView;
    private TextView allRatingView;
    private RatingBar userRatingBar;

    private TableLayout ratingsTableView;
    private View lastfmPlaycountTableRow;
    private View lastfmListenersTableRow;
    private TextView lastfmListenersView;
    private TextView lastfmPlaycountView;
    private RatingBar lastfmListenersRatingBar;
    private RatingBar lastfmPlaycountRatingBar;

    private View rateyourmusicTableRow;
    private RatingBar rateyourmusicRatingBar;
    private TextView rateyourmusicNumberView;
    private View rateyourmusicProgressBar;

    private View progarchivesTableRow;
    private RatingBar progarchivesRatingBar;
    private TextView progarchivesNumberView;
    private View progarchivesProgressBar;


    public static ReleaseRatingsFragment newInstance() {
        Bundle args = new Bundle();
        ReleaseRatingsFragment fragment = new ReleaseRatingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_release_ratings, container, false);

        contentView = layout.findViewById(R.id.contentView);
        errorView = layout.findViewById(R.id.errorView);
        progressView = layout.findViewById(R.id.progressView);
        loginWarningView = layout.findViewById(R.id.loginWarningView);
        allRatingView = layout.findViewById(R.id.allRatingView);
        userRatingBar = layout.findViewById(R.id.userRatingBar);

        ratingsTableView = layout.findViewById(R.id.ratingsTableView);
        lastfmPlaycountTableRow = layout.findViewById(R.id.lastfmPlaycountTableRow);
        lastfmListenersTableRow = layout.findViewById(R.id.lastfmListenersTableRow);
        lastfmListenersView = layout.findViewById(R.id.lastfmListenersView);
        lastfmPlaycountView = layout.findViewById(R.id.lastfmPlaycountView);
        lastfmListenersRatingBar = layout.findViewById(R.id.lastfmListenersRatingBar);
        lastfmPlaycountRatingBar = layout.findViewById(R.id.lastfmPlaycountRatingBar);

        rateyourmusicTableRow = layout.findViewById(R.id.rateyourmusicTableRow);
        rateyourmusicRatingBar = layout.findViewById(R.id.rateyourmusicRatingBar);
        rateyourmusicNumberView = layout.findViewById(R.id.rateyourmusicNumberView);
        rateyourmusicProgressBar = layout.findViewById(R.id.rateyourmusicProgressBar);

        progarchivesTableRow = layout.findViewById(R.id.progarchivesTableRow);
        progarchivesRatingBar = layout.findViewById(R.id.progarchivesRatingBar);
        progarchivesNumberView = layout.findViewById(R.id.progarchivesNumberView);
        progarchivesProgressBar = layout.findViewById(R.id.progarchivesProgressBar);

        setEditListeners();
        loadView();

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        loginWarningView.setVisibility(oauth.hasAccount() ? View.GONE : View.VISIBLE);
        if (isLoadRatingsEnabled != MediaBrainzApp.getPreferences().isLoadRatingsEnabled()) {
            lazyLoad();
        }
    }

    private void setEditListeners() {
        userRatingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (progressView.getVisibility() == View.VISIBLE) {
                return;
            }
            if (oauth.hasAccount()) {
                if (fromUser) {
                    postRating(rating);
                }
            } else {
                ActivityFactory.startLoginActivity(getContext());
            }
        });
    }

    @Override
    public void lazyLoad() {
        viewProgressLoading(false);
        viewError(false);

        Release release = ((GetReleaseCommunicator) getContext()).getRelease();
        if (release != null && release.getReleaseGroup() != null) {
            releaseGroup = release.getReleaseGroup();
            setUserRating();
            setAllRating();

            isLoadRatingsEnabled = MediaBrainzApp.getPreferences().isLoadRatingsEnabled();
            if (isLoadRatingsEnabled) {
                ratingsTableView.setVisibility(View.VISIBLE);
                setLastfmInfo();
                setRateyourmusicRating();
                setProgarchivesRating();
            } else {
                ratingsTableView.setVisibility(View.GONE);
            }
        }
    }

    private void setAllRating() {
        Rating rating = releaseGroup.getRating();
        if (rating != null) {
            Float r = rating.getValue();
            if (r != null) {
                Integer votesCount = rating.getVotesCount();
                allRatingView.setText(getString(R.string.rating_text, r, votesCount));
            } else {
                allRatingView.setText(getString(R.string.rating_text, 0.0, 0));
            }
        }
    }

    private void setUserRating() {
        Rating rating = releaseGroup.getUserRating();
        if (rating != null) {
            Float r = rating.getValue();
            if (r == null) r = 0f;
            userRatingBar.setRating(r);
        }
    }

    private void setRateyourmusicRating() {
        rateyourmusicTableRow.setVisibility(View.GONE);
        List<Url> urls = ((GetUrlsCommunicator) getContext()).getUrls();

        if (urls != null && !urls.isEmpty()) {
            for (Url url : urls) {
                String res = url.getResource();
                if (res.contains("rateyourmusic")) {

                    rateyourmusicTableRow.setVisibility(View.VISIBLE);
                    rateyourmusicProgressBar.setVisibility(View.VISIBLE);
                    rateyourmusicRatingBar.setVisibility(View.GONE);

                    RequestQueue requestQueue = ((GetRequestQueueCommunicator) getContext()).getRequestQueue();
                    String httpsRes = res.replace("http:", "https:");
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, httpsRes,
                            response -> {
                                RateyourmusicResponse rateyourmusicResponse = new RateyourmusicService().parseResponse(response);
                                String avgRating = rateyourmusicResponse.getAvgRating();
                                String numRating = rateyourmusicResponse.getNumRating();

                                if (!TextUtils.isEmpty(avgRating) && !TextUtils.isEmpty(numRating)) {
                                    rateyourmusicRatingBar.setRating(Float.valueOf(avgRating));
                                    rateyourmusicNumberView.setText(avgRating + "(" + numRating + ")");
                                    rateyourmusicProgressBar.setVisibility(View.GONE);
                                    rateyourmusicRatingBar.setVisibility(View.VISIBLE);
                                    rateyourmusicTableRow.setOnClickListener(
                                            v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(httpsRes))));
                                } else {
                                    rateyourmusicTableRow.setVisibility(View.GONE);
                                }
                            },
                            error -> rateyourmusicTableRow.setVisibility(View.GONE));

                    stringRequest.setTag(TAG);
                    requestQueue.add(stringRequest);
                    break;
                }
            }
        }
    }

    private void setProgarchivesRating() {
        progarchivesTableRow.setVisibility(View.GONE);
        List<Url> urls = ((GetUrlsCommunicator) getContext()).getUrls();

        if (urls != null && !urls.isEmpty()) {
            for (Url url : urls) {
                String res = url.getResource();
                if (res.contains("progarchives")) {

                    progarchivesTableRow.setVisibility(View.VISIBLE);
                    progarchivesProgressBar.setVisibility(View.VISIBLE);
                    progarchivesRatingBar.setVisibility(View.GONE);

                    RequestQueue requestQueue = ((GetRequestQueueCommunicator) getContext()).getRequestQueue();
                    String httpsRes = res.replace("http:", "https:");
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, httpsRes,
                            response -> {
                                ProgarchivesResponse progarchivesResponse = new ProgarchivesService().parseResponse(response);
                                String avgRating = progarchivesResponse.getAvgRating();
                                String numRating = progarchivesResponse.getNumRating();

                                if (!TextUtils.isEmpty(avgRating) && !TextUtils.isEmpty(numRating)) {
                                    progarchivesRatingBar.setRating(Float.valueOf(avgRating));
                                    progarchivesNumberView.setText(avgRating + "(" + numRating + ")");
                                    progarchivesProgressBar.setVisibility(View.GONE);
                                    progarchivesRatingBar.setVisibility(View.VISIBLE);
                                    progarchivesTableRow.setOnClickListener(
                                            v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(httpsRes))));
                                } else {
                                    progarchivesTableRow.setVisibility(View.GONE);
                                }
                            },
                            error -> progarchivesTableRow.setVisibility(View.GONE));

                    stringRequest.setTag(TAG);
                    requestQueue.add(stringRequest);
                    break;
                }
            }
        }
    }

    private void setLastfmInfo() {
        List<Artist.ArtistCredit> artistCredit = releaseGroup.getArtistCredits();
        if (artistCredit != null && !artistCredit.isEmpty()) {
            viewProgressLoading(true);
            api.getAlbumFromLastfm(
                    artistCredit.get(0).getName(), releaseGroup.getTitle(),
                    info -> {
                        viewProgressLoading(false);
                        Album album = info.getAlbum();
                        if (album != null) {
                            lastfmPlaycountTableRow.setVisibility(View.VISIBLE);
                            lastfmListenersTableRow.setVisibility(View.VISIBLE);

                            int listeners = album.getListeners();
                            int playCount = album.getPlaycount();

                            lastfmListenersView.setText(StringFormat.decimalFormat(listeners));
                            lastfmPlaycountView.setText(StringFormat.decimalFormat(playCount));

                            lastfmListenersRatingBar.setRating((float) Math.sqrt(listeners) / LASTFM_ALBUM_LISTENERS_COEFF);
                            lastfmPlaycountRatingBar.setRating((float) Math.sqrt(playCount) / LASTFM_ALBUM_PLAYCOUNT_COEFF);
                        } else {
                            lastfmPlaycountTableRow.setVisibility(View.GONE);
                            lastfmListenersTableRow.setVisibility(View.GONE);
                        }
                    },
                    t -> {
                        lastfmPlaycountTableRow.setVisibility(View.GONE);
                        lastfmListenersTableRow.setVisibility(View.GONE);
                        showConnectionWarning(t);
                    }
            );
        } else {
            lastfmPlaycountTableRow.setVisibility(View.GONE);
            lastfmListenersTableRow.setVisibility(View.GONE);
        }
    }

    private void postRating(float rating) {
        viewProgressLoading(true);
        api.postAlbumRating(
                releaseGroup.getId(), rating,
                metadata -> {
                    if (metadata.getMessage().getText().equals("OK")) {
                        api.getAlbumRatings(
                                releaseGroup.getId(),
                                rg -> {
                                    releaseGroup.setRating(rg.getRating());
                                    releaseGroup.setUserRating(rg.getUserRating());
                                    setAllRating();
                                    setUserRating();
                                    viewProgressLoading(false);
                                },
                                this::showConnectionWarning);
                    } else {
                        viewProgressLoading(false);
                        ShowUtil.showMessage(getActivity(), "Error");
                    }
                },
                this::showConnectionWarning
        );
    }

    private void viewProgressLoading(boolean isView) {
        if (isView) {
            contentView.setAlpha(0.3F);
            userRatingBar.setIsIndicator(true);
            progressView.setVisibility(View.VISIBLE);
        } else {
            contentView.setAlpha(1.0F);
            userRatingBar.setIsIndicator(false);
            progressView.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean isView) {
        if (isView) {
            contentView.setVisibility(View.INVISIBLE);
            errorView.setVisibility(View.VISIBLE);
        } else {
            errorView.setVisibility(View.GONE);
            contentView.setVisibility(View.VISIBLE);
        }
    }

    private void showConnectionWarning(Throwable t) {
        //ShowUtil.showError(getActivity(), t);
        viewProgressLoading(false);
        viewError(true);
        errorView.setVisibility(View.VISIBLE);
        errorView.findViewById(R.id.retryButton).setOnClickListener(v -> lazyLoad());
    }

}
