package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TextView;

import app.mediabrainz.R;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.Rating;
import app.mediabrainz.communicator.GetArtistCommunicator;
import app.mediabrainz.intent.ActivityFactory;
import app.mediabrainz.util.ShowUtil;
import app.mediabrainz.util.StringFormat;

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.MediaBrainzApp.oauth;


public class ArtistRatingsFragment extends LazyFragment {

    private final float LASTFM_ARTIST_LISTENERS_COEFF = 185;
    private final float LASTFM_ARTIST_PLAYCOUNT_COEFF = 950;

    private Artist artist;

    private View contentView;
    private View errorView;
    private View progressView;
    private TextView loginWarningView;
    private TextView allRatingView;
    private RatingBar userRatingBar;
    private TableLayout ratingsTableView;
    private TextView lastfmListenersView;
    private TextView lastfmPlaycountView;
    private RatingBar lastfmListenersRatingBar;
    private RatingBar lastfmPlaycountRatingBar;

    public static ArtistRatingsFragment newInstance() {
        Bundle args = new Bundle();
        ArtistRatingsFragment fragment = new ArtistRatingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflate(R.layout.fragment_artist_ratings, container);

        contentView = layout.findViewById(R.id.contentView);
        errorView = layout.findViewById(R.id.errorView);
        progressView = layout.findViewById(R.id.progressView);
        loginWarningView = layout.findViewById(R.id.loginWarningView);
        allRatingView = layout.findViewById(R.id.allRatingView);
        userRatingBar = layout.findViewById(R.id.userRatingBar);
        ratingsTableView = layout.findViewById(R.id.ratingsTableView);
        lastfmListenersView = layout.findViewById(R.id.lastfmListenersView);
        lastfmPlaycountView = layout.findViewById(R.id.lastfmPlaycountView);
        lastfmListenersRatingBar = layout.findViewById(R.id.lastfmListenersRatingBar);
        lastfmPlaycountRatingBar = layout.findViewById(R.id.lastfmPlaycountRatingBar);

        setEditListeners();
        loadView();

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        loginWarningView.setVisibility(oauth.hasAccount() ? View.GONE : View.VISIBLE);
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

        artist = ((GetArtistCommunicator) getContext()).getArtist();
        if (artist != null) {
            setUserRating();
            setAllRating();
            setLastfmInfo();
        }
    }

    private void setAllRating() {
        Rating rating = artist.getRating();
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
        Rating rating = artist.getUserRating();
        if (rating != null) {
            Float r = rating.getValue();
            if (r == null) r = 0f;
            userRatingBar.setRating(r);
        }
    }

    private void setLastfmInfo() {
        viewProgressLoading(true);
        api.getArtistFromLastfm(
                artist.getName(),
                info -> {
                    viewProgressLoading(false);

                    if (info.getArtist() != null) {
                        ratingsTableView.setVisibility(View.VISIBLE);
                        int listeners = info.getArtist().getStats().getListeners();
                        int playCount = info.getArtist().getStats().getPlaycount();

                        lastfmListenersView.setText(StringFormat.decimalFormat(listeners));
                        lastfmPlaycountView.setText(StringFormat.decimalFormat(playCount));

                        lastfmListenersRatingBar.setRating((float) Math.sqrt(listeners) / LASTFM_ARTIST_LISTENERS_COEFF);
                        lastfmPlaycountRatingBar.setRating((float) Math.sqrt(playCount) / LASTFM_ARTIST_PLAYCOUNT_COEFF);
                    }
                },
                t -> {
                    ratingsTableView.setVisibility(View.GONE);
                    showConnectionWarning(t);
                }
        );
    }

    private void postRating(float rating) {
        viewProgressLoading(true);
        api.postArtistRating(
                artist.getId(), rating,
                metadata -> {
                    if (metadata.getMessage().getText().equals("OK")) {
                        api.getArtistRatings(
                                artist.getId(),
                                a -> {
                                    artist.setRating(a.getRating());
                                    artist.setUserRating(a.getUserRating());
                                    setAllRating();
                                    setUserRating();
                                    viewProgressLoading(false);
                                },
                                this::showConnectionWarning
                        );
                    } else {
                        viewProgressLoading(false);
                        toast("Error");
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
        errorView.findViewById(R.id.retryButton).setOnClickListener(v -> lazyLoad());
    }

}
