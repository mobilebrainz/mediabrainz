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
import app.mediabrainz.api.coverart.CoverArtImage;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.Rating;
import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.apihandler.StringMapper;
import app.mediabrainz.intent.ActivityFactory;
import app.mediabrainz.util.ShowUtil;

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.MediaBrainzApp.oauth;


public class PagedReleaseGroupCollectionAdapter extends BasePagedListAdapter<ReleaseGroup> {

    public static class PagedReleaseGroupCollectionViewHolder extends RecyclerView.ViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_release_group_collection;

        private ImageView coverartView;
        private ProgressBar imageProgressView;
        private TextView releaseGroupNameView;
        private RatingBar userRatingView;
        private TextView allRatingView;
        private ImageView deleteView;
        private LinearLayout ratingContainerView;
        private TextView artistNameView;
        private TextView typeView;

        private PagedReleaseGroupCollectionViewHolder(View v) {
            super(v);
            coverartView = v.findViewById(R.id.coverartView);
            imageProgressView = v.findViewById(R.id.imageProgressView);
            releaseGroupNameView = v.findViewById(R.id.releaseGroupNameView);
            deleteView = v.findViewById(R.id.deleteView);
            userRatingView = v.findViewById(R.id.userRatingView);
            allRatingView = v.findViewById(R.id.allRatingView);
            ratingContainerView = v.findViewById(R.id.ratingContainerView);
            artistNameView = v.findViewById(R.id.artistNameView);
            typeView = v.findViewById(R.id.typeView);
        }

        public static PagedReleaseGroupCollectionViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new PagedReleaseGroupCollectionViewHolder(view);
        }

        private void bindTo(ReleaseGroup releaseGroup, boolean isPrivate) {
            deleteView.setVisibility(isPrivate ? View.VISIBLE : View.GONE);
            releaseGroupNameView.setText(releaseGroup.getTitle());
            List<Artist.ArtistCredit> artistCredits = releaseGroup.getArtistCredits();
            if (artistCredits != null && !artistCredits.isEmpty()) {
                artistNameView.setText(artistCredits.get(0).getArtist().getName());
            }
            typeView.setText(StringMapper.mapReleaseGroupOneType(releaseGroup));

            setUserRating(releaseGroup);
            setAllRating(releaseGroup);

            if (MediaBrainzApp.getPreferences().isLoadImagesEnabled()) {
                loadImage(releaseGroup.getId());
            } else {
                coverartView.setVisibility(View.VISIBLE);
            }
            ratingContainerView.setOnClickListener(v -> showRatingBar(releaseGroup));
        }

        private void showRatingBar(ReleaseGroup releaseGroup) {
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
                    title.setText(itemView.getResources().getString(R.string.rate_entity, releaseGroup.getTitle()));

                    rb.setOnRatingBarChangeListener((RatingBar ratingBar, float rating, boolean fromUser) -> {
                        if (oauth.hasAccount() && progressView.getVisibility() == View.INVISIBLE && fromUser) {
                            progressView.setVisibility(View.VISIBLE);
                            rb.setAlpha(0.3F);
                            api.postAlbumRating(
                                    releaseGroup.getId(), rating,
                                    metadata -> {
                                        progressView.setVisibility(View.INVISIBLE);
                                        rb.setAlpha(1.0F);
                                        if (metadata.getMessage().getText().equals("OK")) {
                                            userRatingView.setRating(rating);
                                            api.getAlbumRatings(
                                                    releaseGroup.getId(),
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
                                    }
                            );
                        } else {
                            ActivityFactory.startLoginActivity(itemView.getContext());
                        }
                    });
                }
            } else {
                ActivityFactory.startLoginActivity(itemView.getContext());
            }
        }

        private void setAllRating(ReleaseGroup releaseGroup) {
            Rating rating = releaseGroup.getRating();
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

        private void setUserRating(ReleaseGroup releaseGroup) {
            Rating rating = releaseGroup.getUserRating();
            if (rating != null) {
                Float r = rating.getValue();
                if (r == null) r = 0f;
                userRatingView.setRating(r);
            }
        }

        private void loadImage(String mbid) {
            showImageProgressLoading(true);
            api.getReleaseGroupCoverArt(
                    mbid,
                    coverArt -> {
                        CoverArtImage.Thumbnails thumbnails = coverArt.getFrontThumbnails();
                        if (thumbnails != null && !TextUtils.isEmpty(thumbnails.getSmall())) {
                            Picasso.get().load(thumbnails.getSmall()).fit()
                                    .into(coverartView, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            showImageProgressLoading(false);
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            showImageProgressLoading(false);
                                        }
                                    });
                        } else {
                            showImageProgressLoading(false);
                        }
                    },
                    t -> showImageProgressLoading(false));
        }

        private void showImageProgressLoading(boolean show) {
            if (show) {
                coverartView.setVisibility(View.INVISIBLE);
                imageProgressView.setVisibility(View.VISIBLE);
            } else {
                imageProgressView.setVisibility(View.GONE);
                coverartView.setVisibility(View.VISIBLE);
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

    public PagedReleaseGroupCollectionAdapter(RetryCallback retryCallback, boolean isPrivate) {
        super(DIFF_CALLBACK, retryCallback);
        this.isPrivate = isPrivate;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return NetworkStateViewHolder.VIEW_HOLDER_LAYOUT;
        } else {
            return PagedReleaseGroupCollectionViewHolder.VIEW_HOLDER_LAYOUT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case PagedReleaseGroupCollectionViewHolder.VIEW_HOLDER_LAYOUT:
                return PagedReleaseGroupCollectionViewHolder.create(parent);
            case NetworkStateViewHolder.VIEW_HOLDER_LAYOUT:
                return NetworkStateViewHolder.create(parent, retryCallback);
            default:
                throw new IllegalArgumentException("unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case PagedReleaseGroupCollectionViewHolder.VIEW_HOLDER_LAYOUT:
                ReleaseGroup releaseGroup = getItem(position);
                ((PagedReleaseGroupCollectionViewHolder) holder).bindTo(releaseGroup, isPrivate);
                if (holderClickListener != null) {
                    holder.itemView.setOnClickListener(view -> holderClickListener.onClick(releaseGroup));
                }
                if (onDeleteListener != null) {
                    ((PagedReleaseGroupCollectionViewHolder) holder).setOnDeleteListener(onDeleteListener);
                }
                break;
            case NetworkStateViewHolder.VIEW_HOLDER_LAYOUT:
                ((NetworkStateViewHolder) holder).bindTo(networkState);
                break;
        }
    }

    private static DiffUtil.ItemCallback<ReleaseGroup> DIFF_CALLBACK = new DiffUtil.ItemCallback<ReleaseGroup>() {
        @Override
        public boolean areItemsTheSame(@NonNull ReleaseGroup oldItem, @NonNull ReleaseGroup newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ReleaseGroup oldItem, @NonNull ReleaseGroup newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

    public interface HolderClickListener {
        void onClick(ReleaseGroup releaseGroup);
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
