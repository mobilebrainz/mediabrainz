package app.mediabrainz.adapter.recycler;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import app.mediabrainz.R;
import app.mediabrainz.api.model.Rating;
import app.mediabrainz.api.model.Recording;
import app.mediabrainz.intent.ActivityFactory;
import app.mediabrainz.util.ShowUtil;

import java.util.Collections;
import java.util.List;

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.MediaBrainzApp.oauth;


public class RecordingCollectionAdapter extends BaseRecyclerViewAdapter<RecordingCollectionAdapter.RecordingCollectionViewHolder> {

    private List<Recording> recordings;

    public static class RecordingCollectionViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_recording_collection;

        private TextView recordingNameTextView;
        private ImageView deleteButton;
        private RatingBar userRating;
        private TextView allRatingView;
        private LinearLayout ratingContainer;

        public static RecordingCollectionViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new RecordingCollectionViewHolder(view);
        }

        private RecordingCollectionViewHolder(View v) {
            super(v);
            recordingNameTextView = v.findViewById(R.id.recording_name);
            deleteButton = v.findViewById(R.id.delete);
            userRating = v.findViewById(R.id.user_rating);
            allRatingView = v.findViewById(R.id.all_rating);
            ratingContainer = v.findViewById(R.id.rating_container);
        }

        public void bindTo(Recording recording, boolean isPrivate) {
            deleteButton.setVisibility(isPrivate ? View.VISIBLE : View.GONE);
            recordingNameTextView.setText(recording.getTitle());
            setUserRating(recording);
            setAllRating(recording);

            ratingContainer.setOnClickListener(v -> showRatingBar(recording));
        }

        private void showRatingBar(Recording recording) {
            if (oauth.hasAccount()) {
                AlertDialog alertDialog = new AlertDialog.Builder(itemView.getContext()).create();
                alertDialog.show();
                Window win = alertDialog.getWindow();
                if (win != null) {
                    win.setContentView(R.layout.dialog_rating_bar);
                    RatingBar rb = win.findViewById(R.id.rating_bar);
                    View ratingProgress = win.findViewById(R.id.loading);
                    rb.setRating(userRating.getRating());
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
                    allRatingView.setText(itemView.getResources().getString(R.string.rating_text, r, votesCount));
                } else {
                    allRatingView.setText(itemView.getResources().getString(R.string.rating_text, 0.0, 0));
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

        public void setOnDeleteListener(OnDeleteListener listener) {
            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(getAdapterPosition());
                }
            });
        }
    }

    private boolean isPrivate;

    public RecordingCollectionAdapter(List<Recording> recordings, boolean isPrivate) {
        this.recordings = recordings;
        this.isPrivate = isPrivate;
        Collections.sort(this.recordings, (a1, a2) -> (a1.getTitle()).compareTo(a2.getTitle()));
    }

    @Override
    public void onBind(RecordingCollectionViewHolder holder, final int position) {
        holder.setOnDeleteListener(onDeleteListener);
        holder.bindTo(recordings.get(position), isPrivate);
    }

    @Override
    public int getItemCount() {
        return recordings.size();
    }

    @NonNull
    @Override
    public RecordingCollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return RecordingCollectionViewHolder.create(parent);
    }

    public interface OnDeleteListener {
        void onDelete(int position);
    }

    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }
}
