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

    private View content;
    private View error;
    private View loading;
    private TextView loginWarning;
    private TextView allRatingText;
    private RatingBar userRatingBar;
    private TableLayout lastfmTable;
    private TextView lastfmListeners;
    private TextView lastfmPlaycount;
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
        View layout = inflater.inflate(R.layout.fragment_artist_ratings, container, false);

        content = layout.findViewById(R.id.content);
        error = layout.findViewById(R.id.error);
        loading = layout.findViewById(R.id.loading);
        loginWarning = layout.findViewById(R.id.login_warning);
        allRatingText = layout.findViewById(R.id.all_rating_text);
        userRatingBar = layout.findViewById(R.id.user_rating_bar);
        lastfmTable = layout.findViewById(R.id.lastfm_table);
        lastfmListeners = layout.findViewById(R.id.lastfm_listeners);
        lastfmPlaycount = layout.findViewById(R.id.lastfm_playcount);
        lastfmListenersRatingBar = layout.findViewById(R.id.lastfm_listeners_rating_bar);
        lastfmPlaycountRatingBar = layout.findViewById(R.id.lastfm_playcount_rating_bar);

        setEditListeners();
        loadView();

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        loginWarning.setVisibility(oauth.hasAccount() ? View.GONE : View.VISIBLE);
    }

    private void setEditListeners() {
        userRatingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (loading.getVisibility() == View.VISIBLE) {
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
                allRatingText.setText(getString(R.string.rating_text, r, votesCount));
            } else {
                allRatingText.setText(getString(R.string.rating_text, 0.0, 0));
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
                        lastfmTable.setVisibility(View.VISIBLE);
                        int listeners = info.getArtist().getStats().getListeners();
                        int playCount = info.getArtist().getStats().getPlaycount();

                        lastfmListeners.setText(StringFormat.decimalFormat(listeners));
                        lastfmPlaycount.setText(StringFormat.decimalFormat(playCount));

                        lastfmListenersRatingBar.setRating((float) Math.sqrt(listeners) / LASTFM_ARTIST_LISTENERS_COEFF);
                        lastfmPlaycountRatingBar.setRating((float) Math.sqrt(playCount) / LASTFM_ARTIST_PLAYCOUNT_COEFF);
                    }
                },
                t -> {
                    lastfmTable.setVisibility(View.GONE);
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
                        ShowUtil.showMessage(getActivity(), "Error");
                    }
                },
                this::showConnectionWarning
        );
    }

    private void viewProgressLoading(boolean isView) {
        if (isView) {
            content.setAlpha(0.3F);
            userRatingBar.setIsIndicator(true);
            loading.setVisibility(View.VISIBLE);
        } else {
            content.setAlpha(1.0F);
            userRatingBar.setIsIndicator(false);
            loading.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean isView) {
        if (isView) {
            content.setVisibility(View.INVISIBLE);
            error.setVisibility(View.VISIBLE);
        } else {
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
