package app.mediabrainz.adapter.recycler.tracks;

import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
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

    public interface OnItemClickListener {
        void onClick(Media.Track track);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private TextView num;
    private TextView name;
    private TextView length;
    private RatingBar userRating;
    private TextView allRatingView;
    private LinearLayout ratingContainer;

    public ItemViewHolder(View itemView, boolean visible) {
        super(itemView, visible);
        num = itemView.findViewById(R.id.track_num);
        name = itemView.findViewById(R.id.track_name);
        length = itemView.findViewById(R.id.track_length);
        userRating = itemView.findViewById(R.id.user_rating);
        allRatingView = itemView.findViewById(R.id.all_rating);
        ratingContainer = itemView.findViewById(R.id.rating_container);
    }

    public void bindView(Media.Track track) {
        itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onClick(track);
            }
        });
        num.setText(track.getNumber());
        name.setText(track.getTitle());
        length.setText(MbUtils.formatTime(track.getLength()));

        setUserRating(track.getRecording());
        setAllRating(track.getRecording());

        ratingContainer.setOnClickListener(v -> showRatingBar(track.getRecording()));
    }

    private void showRatingBar(Recording recording) {
        if (oauth.hasAccount()) {
            AlertDialog alertDialog = new AlertDialog.Builder(itemView.getContext()).create();
            alertDialog.show();
            Window win = alertDialog.getWindow();
            if (win != null) {
                win.setContentView(R.layout.dialog_rating_bar);
                RatingBar rb = win.findViewById(R.id.rating_bar);
                rb.setRating(userRating.getRating());
                View ratingProgress = win.findViewById(R.id.loading);
                TextView title = win.findViewById(R.id.title_text);
                title.setText(itemView.getResources().getString(R.string.rate_entity, recording.getTitle()));

                rb.setOnRatingBarChangeListener((RatingBar ratingBar, float rating, boolean fromUser) -> {
                    if (oauth.hasAccount() && ratingProgress.getVisibility() == View.INVISIBLE && fromUser) {
                        ratingProgress.setVisibility(View.VISIBLE);
                        rb.setAlpha(0.3F);
                        api.postRecordingRating(
                                recording.getId(), rating,
                                metadata -> {
                                    ratingProgress.setVisibility(View.INVISIBLE);
                                    rb.setAlpha(1.0F);
                                    if (metadata.getMessage().getText().equals("OK")) {
                                        userRating.setRating(rating);
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
                                    ratingProgress.setVisibility(View.INVISIBLE);
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
            userRating.setRating(r);
        }
    }

}
