package app.mediabrainz.adapter.recycler;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.R;
import app.mediabrainz.api.externalResources.lastfm.model.Image;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.Rating;
import app.mediabrainz.intent.ActivityFactory;
import app.mediabrainz.util.ShowUtil;

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.MediaBrainzApp.oauth;


public class PagedArtistCollectionAdapter extends BasePagedListAdapter<Artist> {

    public static class PagedArtistCollectionViewHolder extends RecyclerView.ViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_artist_collection;

        private ImageView imageView;
        private ProgressBar imageProgressView;
        private TextView artistNameView;
        private RatingBar userRatingView;
        private TextView allRatingView;
        private ImageView deleteView;
        private LinearLayout ratingContainerView;

        private PagedArtistCollectionViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.releaseImageView);
            imageProgressView = v.findViewById(R.id.imageProgressView);
            artistNameView = v.findViewById(R.id.artistNameView);
            userRatingView = v.findViewById(R.id.userRatingView);
            allRatingView = v.findViewById(R.id.allRatingView);
            deleteView = v.findViewById(R.id.deleteView);
            ratingContainerView = v.findViewById(R.id.ratingContainerView);
        }

        public static PagedArtistCollectionViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new PagedArtistCollectionViewHolder(view);
        }

        private void bindTo(Artist artist, boolean isPrivate) {
            deleteView.setVisibility(isPrivate ? View.VISIBLE : View.GONE);

            artistNameView.setText(artist.getName());
            setUserRating(artist);
            setAllRating(artist);
            if (MediaBrainzApp.getPreferences().isLoadImagesEnabled()) {
                loadArtistImageFromLastfm(artist.getName());
            } else {
                imageView.setVisibility(View.VISIBLE);
            }
            ratingContainerView.setOnClickListener(v -> showRatingBar(artist));
        }

        private void showRatingBar(Artist artist) {
            if (oauth.hasAccount()) {
                AlertDialog alertDialog = new AlertDialog.Builder(itemView.getContext()).create();
                alertDialog.show();
                Window win = alertDialog.getWindow();
                if (win != null) {
                    win.setContentView(R.layout.dialog_rating_bar);
                    RatingBar rb = win.findViewById(R.id.rating_bar);
                    View progressView = win.findViewById(R.id.progressView);
                    TextView title = win.findViewById(R.id.title_text);
                    title.setText(itemView.getResources().getString(R.string.rate_entity, artist.getName()));
                    rb.setRating(userRatingView.getRating());

                    rb.setOnRatingBarChangeListener((RatingBar ratingBar, float rating, boolean fromUser) -> {
                        if (oauth.hasAccount() && progressView.getVisibility() == View.INVISIBLE && fromUser) {
                            progressView.setVisibility(View.VISIBLE);
                            rb.setAlpha(0.3F);
                            api.postArtistRating(
                                    artist.getId(), rating,
                                    metadata -> {
                                        progressView.setVisibility(View.INVISIBLE);
                                        rb.setAlpha(1.0F);
                                        if (metadata.getMessage().getText().equals("OK")) {
                                            userRatingView.setRating(rating);
                                            api.getArtistRatings(
                                                    artist.getId(),
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

        private void setAllRating(Artist artist) {
            Rating rating = artist.getRating();
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

        private void setUserRating(Artist artist) {
            Rating rating = artist.getUserRating();
            if (rating != null) {
                Float r = rating.getValue();
                if (r == null) r = 0f;
                userRatingView.setRating(r);
            }
        }

        private void loadArtistImageFromLastfm(String name) {
            showImageProgressLoading(true);
            api.getArtistFromLastfm(
                    name,
                    result -> {
                        boolean loaded = true;
                        if (result.getError() == null || result.getError() == 0) {
                            List<Image> images = result.getArtist().getImages();
                            if (images != null && !images.isEmpty()) {
                                for (Image img : images) {
                                    if (img.getSize().equals(Image.SizeType.MEDIUM.toString()) && !TextUtils.isEmpty(img.getText())) {
                                        Picasso.get().load(img.getText()).fit().into(imageView, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                showImageProgressLoading(false);
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                showImageProgressLoading(false);
                                            }
                                        });
                                        loaded = false;
                                        break;
                                    }
                                }
                            }
                        }
                        if (loaded) {
                            showImageProgressLoading(false);
                        }
                    },
                    t -> showImageProgressLoading(false));
        }

        private void showImageProgressLoading(boolean show) {
            if (show) {
                imageView.setVisibility(View.INVISIBLE);
                imageProgressView.setVisibility(View.VISIBLE);
            } else {
                imageProgressView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
            }
        }

        public void setOnDeleteListener(OnDeleteListener listener) {
            deleteView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(getAdapterPosition());
                }
            });
        }

    }

    private boolean isPrivate;

    public PagedArtistCollectionAdapter(RetryCallback retryCallback, boolean isPrivate) {
        super(DIFF_CALLBACK, retryCallback);
        this.isPrivate = isPrivate;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return NetworkStateViewHolder.VIEW_HOLDER_LAYOUT;
        } else {
            return PagedArtistCollectionViewHolder.VIEW_HOLDER_LAYOUT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case PagedArtistCollectionViewHolder.VIEW_HOLDER_LAYOUT:
                return PagedArtistCollectionViewHolder.create(parent);
            case NetworkStateViewHolder.VIEW_HOLDER_LAYOUT:
                return NetworkStateViewHolder.create(parent, retryCallback);
            default:
                throw new IllegalArgumentException("unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case PagedArtistCollectionViewHolder.VIEW_HOLDER_LAYOUT:
                Artist artist = getItem(position);
                ((PagedArtistCollectionViewHolder) holder).bindTo(artist, isPrivate);
                if (holderClickListener != null) {
                    holder.itemView.setOnClickListener(view -> holderClickListener.onClick(artist));
                }
                if (onDeleteListener != null) {
                    ((PagedArtistCollectionViewHolder) holder).setOnDeleteListener(onDeleteListener);
                }
                break;
            case NetworkStateViewHolder.VIEW_HOLDER_LAYOUT:
                ((NetworkStateViewHolder) holder).bindTo(networkState);
                break;
        }
    }

    private static DiffUtil.ItemCallback<Artist> DIFF_CALLBACK = new DiffUtil.ItemCallback<Artist>() {
        @Override
        public boolean areItemsTheSame(@NonNull Artist oldItem, @NonNull Artist newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Artist oldItem, @NonNull Artist newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

    public interface HolderClickListener {
        void onClick(Artist artist);
    }

    private HolderClickListener holderClickListener;

    public void setHolderClickListener(HolderClickListener holderClickListener) {
        this.holderClickListener = holderClickListener;
    }

    public interface OnDeleteListener {
        void onDelete(int position);
    }

    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }
}
