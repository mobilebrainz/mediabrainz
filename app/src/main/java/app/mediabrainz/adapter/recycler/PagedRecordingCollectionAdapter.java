package app.mediabrainz.adapter.recycler;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import app.mediabrainz.R;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.Rating;
import app.mediabrainz.api.model.Recording;
import app.mediabrainz.intent.ActivityFactory;
import app.mediabrainz.util.ShowUtil;

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.MediaBrainzApp.oauth;


public class PagedRecordingCollectionAdapter extends BasePagedListAdapter<Recording> {

    public static class PagedRecordingCollectionViewHolder extends RecyclerView.ViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_recording_collection;

        private Recording recording;
        private String artistName = "";

        private TextView recordingNameView;
        private TextView artistNameView;
        private ImageView deleteView;
        private RatingBar userRatingView;
        private TextView allRatingView;
        private LinearLayout ratingContainerView;
        private ImageView playYoutubeView;

        private PagedRecordingCollectionViewHolder(View v) {
            super(v);
            recordingNameView = v.findViewById(R.id.recordingNameView);
            artistNameView = v.findViewById(R.id.artistNameView);
            deleteView = v.findViewById(R.id.deleteView);
            userRatingView = v.findViewById(R.id.userRatingView);
            allRatingView = v.findViewById(R.id.allRatingView);
            ratingContainerView = v.findViewById(R.id.ratingContainerView);
            playYoutubeView = itemView.findViewById(R.id.playYoutubeView);
        }

        public static PagedRecordingCollectionViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new PagedRecordingCollectionViewHolder(view);
        }

        public void bindTo(Recording recording, boolean isPrivate) {
            this.recording = recording;
            deleteView.setVisibility(isPrivate ? View.VISIBLE : View.GONE);
            recordingNameView.setText(recording.getTitle());
            List<Artist.ArtistCredit> artistCredits = recording.getArtistCredits();
            if (artistCredits != null && !artistCredits.isEmpty()) {
                artistName = artistCredits.get(0).getArtist().getName();
                artistNameView.setText(artistName);
            }
            setUserRating(recording);
            setAllRating(recording);

            ratingContainerView.setOnClickListener(v -> showRatingBar(recording));
        }

        private void showRatingBar(Recording recording) {
            if (oauth.hasAccount()) {
                AlertDialog alertDialog = new AlertDialog.Builder(itemView.getContext()).create();
                alertDialog.show();
                Window win = alertDialog.getWindow();
                if (win != null) {
                    win.setContentView(R.layout.dialog_rating_bar);
                    RatingBar rb = win.findViewById(R.id.rating_bar);
                    View progressView = win.findViewById(R.id.progressView);
                    rb.setRating(userRatingView.getRating());
                    TextView title = win.findViewById(R.id.title_text);
                    title.setText(itemView.getResources().getString(R.string.rate_entity, recording.getTitle()));

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
                userRatingView.setRating(r);
            }
        }

        public void setOnDeleteListener(OnDeleteListener listener) {
            deleteView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(getAdapterPosition());
                }
            });
        }

        public void setonPlayYoutubeListener(OnPlayYoutubeListener listener) {
            playYoutubeView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPlay(artistName + (!artistName.equals("") ? " - " : "") + recording.getTitle());
                }
            });
        }
    }

    private boolean isPrivate;

    public PagedRecordingCollectionAdapter(RetryCallback retryCallback, boolean isPrivate) {
        super(DIFF_CALLBACK, retryCallback);
        this.isPrivate = isPrivate;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return NetworkStateViewHolder.VIEW_HOLDER_LAYOUT;
        } else {
            return PagedRecordingCollectionViewHolder.VIEW_HOLDER_LAYOUT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case PagedRecordingCollectionViewHolder.VIEW_HOLDER_LAYOUT:
                return PagedRecordingCollectionViewHolder.create(parent);
            case NetworkStateViewHolder.VIEW_HOLDER_LAYOUT:
                return NetworkStateViewHolder.create(parent, retryCallback);
            default:
                throw new IllegalArgumentException("unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case PagedRecordingCollectionViewHolder.VIEW_HOLDER_LAYOUT:
                Recording recording = getItem(position);
                ((PagedRecordingCollectionViewHolder) holder).bindTo(recording, isPrivate);
                if (holderClickListener != null) {
                    holder.itemView.setOnClickListener(view -> holderClickListener.onClick(recording));
                }
                if (onDeleteListener != null) {
                    ((PagedRecordingCollectionViewHolder) holder).setOnDeleteListener(onDeleteListener);
                }
                if (onPlayYoutubeListener != null) {
                    ((PagedRecordingCollectionViewHolder) holder).setonPlayYoutubeListener(onPlayYoutubeListener);
                }
                break;
            case NetworkStateViewHolder.VIEW_HOLDER_LAYOUT:
                ((NetworkStateViewHolder) holder).bindTo(networkState);
                break;
        }
    }

    private static DiffUtil.ItemCallback<Recording> DIFF_CALLBACK = new DiffUtil.ItemCallback<Recording>() {
        @Override
        public boolean areItemsTheSame(@NonNull Recording oldItem, @NonNull Recording newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Recording oldItem, @NonNull Recording newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

    public interface OnPlayYoutubeListener {
        void onPlay(String keyword);
    }

    public interface HolderClickListener {
        void onClick(Recording recording);
    }

    public interface OnDeleteListener {
        void onDelete(int position);
    }

    private HolderClickListener holderClickListener;
    private OnDeleteListener onDeleteListener;
    private OnPlayYoutubeListener onPlayYoutubeListener;

    public void setHolderClickListener(HolderClickListener holderClickListener) {
        this.holderClickListener = holderClickListener;
    }

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    public void setOnPlayYoutubeListener(OnPlayYoutubeListener onPlayYoutubeListener) {
        this.onPlayYoutubeListener = onPlayYoutubeListener;
    }
}
