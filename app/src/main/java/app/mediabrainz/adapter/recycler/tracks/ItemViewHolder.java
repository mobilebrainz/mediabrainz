package app.mediabrainz.adapter.recycler.tracks;

import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.expandedRecycler.BaseItemViewHolder;
import app.mediabrainz.api.model.Media;
import app.mediabrainz.api.model.Rating;
import app.mediabrainz.api.model.Recording;
import app.mediabrainz.intent.ActivityFactory;
import app.mediabrainz.util.MbUtils;
import app.mediabrainz.util.ShowUtil;

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.MediaBrainzApp.oauth;


public class ItemViewHolder extends BaseItemViewHolder {

    public interface OnPlayYoutubeListener {
        void onPlay(Media.Track track);
    }

    public interface OnItemClickListener {
        void onClick(Media.Track track);
    }

    private OnItemClickListener onItemClickListener;
    private OnPlayYoutubeListener onPlayYoutubeListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnPlayYoutubeListener(OnPlayYoutubeListener onPlayYoutubeListener) {
        this.onPlayYoutubeListener = onPlayYoutubeListener;
    }

    private TextView trackNumView;
    private TextView trackNameView;
    private TextView trackLengthView;
    private RatingBar userRatingView;
    private TextView allRatingView;
    private LinearLayout ratingContainerView;
    private ImageView playYoutubeView;

    public ItemViewHolder(View itemView, boolean visible) {
        super(itemView, visible);
        trackNumView = itemView.findViewById(R.id.trackNumView);
        trackNameView = itemView.findViewById(R.id.trackNameView);
        trackLengthView = itemView.findViewById(R.id.trackLengthView);
        userRatingView = itemView.findViewById(R.id.userRatingView);
        allRatingView = itemView.findViewById(R.id.allRatingView);
        ratingContainerView = itemView.findViewById(R.id.ratingContainerView);
        playYoutubeView = itemView.findViewById(R.id.playYoutubeView);
    }

    public void bindView(Media.Track track) {
        itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onClick(track);
            }
        });
        playYoutubeView.setOnClickListener(v -> {
            if (onPlayYoutubeListener != null) {
                onPlayYoutubeListener.onPlay(track);
            }
        });

        trackNumView.setText(track.getNumber());
        trackNameView.setText(track.getTitle());
        trackLengthView.setText(MbUtils.formatTime(track.getLength()));

        setUserRating(track.getRecording());
        setAllRating(track.getRecording());

        ratingContainerView.setOnClickListener(v -> showRatingBar(track.getRecording()));
    }

    private void showRatingBar(Recording recording) {
        if (oauth.hasAccount()) {
            AlertDialog alertDialog = new AlertDialog.Builder(itemView.getContext()).create();
            alertDialog.show();
            Window win = alertDialog.getWindow();
            if (win != null) {
                win.setContentView(R.layout.dialog_rating_bar);
                RatingBar rb = win.findViewById(R.id.ratingBar);
                rb.setRating(userRatingView.getRating());
                View progressView = win.findViewById(R.id.progressView);
                TextView titleTextView = win.findViewById(R.id.titleTextView);
                titleTextView.setText(itemView.getResources().getString(R.string.rate_entity, recording.getTitle()));

                rb.setOnRatingBarChangeListener((RatingBar ratingBar, float rating, boolean fromUser) -> {
                    if (oauth.hasAccount() && progressView.getVisibility() == View.INVISIBLE && fromUser) {
                        progressView.setVisibility(View.VISIBLE);
                        rb.setAlpha(0.3F);
                        api.postRecordingRating(
                                recording.getId(), rating,
                                metadata -> {
                                    progressView.setVisibility(View.INVISIBLE);
                                    rb.setAlpha(1.0F);
                                    if (metadata.getMessage().getText().equals("OK")) {
                                        userRatingView.setRating(rating);
                                        api.getRecordingRatings(
                                                recording.getId(),
                                                this::setAllRating,
                                                t -> ShowUtil.showToast(itemView.getContext(), t.getMessage()));
                                    } else {
                                        ShowUtil.showToast(itemView.getContext(), "Error");
                                    }
                                    alertDialog.dismiss();
                                },
                                t -> {
                                    progressView.setVisibility(View.INVISIBLE);
                                    rb.setAlpha(1.0F);
                                    ShowUtil.showToast(itemView.getContext(), t.getMessage());
                                    alertDialog.dismiss();
                                });
                    } else {
                        ActivityFactory.startLoginActivity(itemView.getContext());
                    }
                });
            }
        } else {
            ActivityFactory.startLoginActivity(itemView.getContext());
        }
    }

    private void setAllRating(Recording recording) {
        Rating rating = recording.getRating();
        if (rating != null) {
            Float r = rating.getValue();
            if (r != null) {
                Integer votesCount = rating.getVotesCount();
                allRatingView.setText(itemView.getContext().getString(R.string.rating_text, r, votesCount));
            } else {
                allRatingView.setText(itemView.getContext().getString(R.string.rating_text, 0.0, 0));
            }
        }
    }

    private void setUserRating(Recording recording) {
        Rating rating = recording.getUserRating();
        if (rating != null) {
            Float r = rating.getValue();
            if (r == null) r = 0f;
            userRatingView.setRating(r);
        }
    }

}
