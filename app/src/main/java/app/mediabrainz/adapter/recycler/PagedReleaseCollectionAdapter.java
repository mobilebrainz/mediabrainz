package app.mediabrainz.adapter.recycler;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.R;
import app.mediabrainz.api.coverart.CoverArtImage;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.Release;

import static app.mediabrainz.MediaBrainzApp.api;


public class PagedReleaseCollectionAdapter extends BasePagedListAdapter<Release> {

    public static class PagedReleaseCollectionViewHolder extends RecyclerView.ViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_release_collection;

        private Release release;

        private ImageView releaseImageView;
        private ProgressBar imageProgressView;
        private TextView releaseNameView;
        private TextView artistNameView;
        private ImageView deleteView;

        private PagedReleaseCollectionViewHolder(View v) {
            super(v);
            releaseImageView = v.findViewById(R.id.releaseImageView);
            imageProgressView = v.findViewById(R.id.imageProgressView);
            releaseNameView = v.findViewById(R.id.releaseNameView);
            artistNameView = v.findViewById(R.id.artistNameView);
            deleteView = v.findViewById(R.id.deleteView);
        }

        public static PagedReleaseCollectionViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new PagedReleaseCollectionViewHolder(view);
        }

        private void bindTo(Release release, boolean isPrivate) {
            deleteView.setVisibility(isPrivate ? View.VISIBLE : View.GONE);
            this.release = release;
            releaseNameView.setText(release.getTitle());
            List<Artist.ArtistCredit> artistCredits = release.getArtistCredits();
            if (artistCredits != null && !artistCredits.isEmpty()) {
                artistNameView.setText(artistCredits.get(0).getArtist().getName());
            }
            loadReleaseImage();
        }

        private void loadReleaseImage() {
            if (MediaBrainzApp.getPreferences().isLoadImagesEnabled() &&
                    release.getCoverArt() != null &&
                    release.getCoverArt().getFront() != null &&
                    release.getCoverArt().getFront()) {

                showImageProgressLoading(true);
                api.getReleaseCoverArt(
                        release.getId(),
                        coverArt -> {
                            CoverArtImage.Thumbnails thumbnails = coverArt.getFrontThumbnails();
                            if (thumbnails != null && !TextUtils.isEmpty(thumbnails.getSmall())) {
                                Picasso.get().load(thumbnails.getSmall()).fit()
                                        .into(releaseImageView, new Callback() {
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
            } else {
                showImageProgressLoading(false);
            }
        }

        private void showImageProgressLoading(boolean show) {
            if (show) {
                releaseImageView.setVisibility(View.INVISIBLE);
                imageProgressView.setVisibility(View.VISIBLE);
            } else {
                imageProgressView.setVisibility(View.GONE);
                releaseImageView.setVisibility(View.VISIBLE);
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

    public PagedReleaseCollectionAdapter(RetryCallback retryCallback, boolean isPrivate) {
        super(DIFF_CALLBACK, retryCallback);
        this.isPrivate = isPrivate;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return NetworkStateViewHolder.VIEW_HOLDER_LAYOUT;
        } else {
            return PagedReleaseCollectionViewHolder.VIEW_HOLDER_LAYOUT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case PagedReleaseCollectionViewHolder.VIEW_HOLDER_LAYOUT:
                return PagedReleaseCollectionViewHolder.create(parent);
            case NetworkStateViewHolder.VIEW_HOLDER_LAYOUT:
                return NetworkStateViewHolder.create(parent, retryCallback);
            default:
                throw new IllegalArgumentException("unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case PagedReleaseCollectionViewHolder.VIEW_HOLDER_LAYOUT:
                Release release = getItem(position);
                ((PagedReleaseCollectionViewHolder) holder).bindTo(release, isPrivate);
                if (holderClickListener != null) {
                    holder.itemView.setOnClickListener(view -> holderClickListener.onClick(release));
                }
                if (onDeleteListener != null) {
                    ((PagedReleaseCollectionViewHolder) holder).setOnDeleteListener(onDeleteListener);
                }
                break;
            case NetworkStateViewHolder.VIEW_HOLDER_LAYOUT:
                ((NetworkStateViewHolder) holder).bindTo(networkState);
                break;
        }
    }

    private static DiffUtil.ItemCallback<Release> DIFF_CALLBACK = new DiffUtil.ItemCallback<Release>() {
        @Override
        public boolean areItemsTheSame(@NonNull Release oldItem, @NonNull Release newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Release oldItem, @NonNull Release newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

    public interface HolderClickListener {
        void onClick(Release release);
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
