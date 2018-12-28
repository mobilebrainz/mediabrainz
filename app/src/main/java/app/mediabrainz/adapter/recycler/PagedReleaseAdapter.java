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
import app.mediabrainz.api.model.Label;
import app.mediabrainz.api.model.Media;
import app.mediabrainz.api.model.Release;
import app.mediabrainz.util.StringFormat;

import static app.mediabrainz.MediaBrainzApp.api;


public class PagedReleaseAdapter extends BasePagedListAdapter<Release> {

    private String releaseMbid;

    public static class PagedReleaseViewHolder extends RecyclerView.ViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_release;

        private ImageView coverartView;
        private ProgressBar coverartLoadingView;
        private TextView dateView;
        private TextView releaseNameView;
        private TextView countryLabelView;
        private TextView formatView;
        private TextView statusView;
        private TextView catalogView;
        private TextView barcodeView;

        private PagedReleaseViewHolder(View v) {
            super(v);

            coverartView = v.findViewById(R.id.coverartView);
            coverartLoadingView = v.findViewById(R.id.coverartLoadingView);
            dateView = v.findViewById(R.id.dateView);
            releaseNameView = v.findViewById(R.id.releaseNameView);
            countryLabelView = v.findViewById(R.id.countryLabelView);
            formatView = v.findViewById(R.id.formatView);
            statusView = v.findViewById(R.id.statusView);
            catalogView = v.findViewById(R.id.catalogView);
            barcodeView = v.findViewById(R.id.barcodeView);
        }

        public static PagedReleaseViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new PagedReleaseViewHolder(view);
        }

        public void bindTo(Release release, String releaseMbid) {
            if (release.getId().equals(releaseMbid)) {
                itemView.setBackgroundResource(R.color.md_orange_50);
            }

            dateView.setText(release.getDate());
            releaseNameView.setText(release.getTitle());

            if (!TextUtils.isEmpty(release.getBarcode())) {
                barcodeView.setText(itemView.getResources().getString(R.string.r_barcode, release.getBarcode()));
            } else {
                barcodeView.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(release.getStatus())) {
                statusView.setText(itemView.getResources().getString(R.string.r_status, release.getStatus()));
            } else {
                statusView.setVisibility(View.GONE);
            }

            List<Label.LabelInfo> labelInfos = release.getLabelInfo();
            String labelName = "";
            if (labelInfos != null && !labelInfos.isEmpty()) {
                Label label = labelInfos.get(0).getLabel();
                if (label != null) {
                    labelName = label.getName();
                }
                String labelCatalog = labelInfos.get(0).getCatalogNumber();
                if (!TextUtils.isEmpty(labelCatalog)) {
                    catalogView.setText(itemView.getResources().getString(R.string.r_catalog, labelCatalog));
                } else {
                    catalogView.setVisibility(View.GONE);
                }
            }
            countryLabelView.setText(release.getCountry() + " " + labelName);

            int trackCount = 0;
            List<Media> medias = release.getMedia();
            for (Media media : medias) {
                trackCount += media.getTrackCount();
            }
            String f = StringFormat.buildReleaseFormatsString(itemView.getContext(), medias);
            formatView.setText(itemView.getResources().getString(R.string.r_tracks, f, trackCount));

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
            } else {
                showImageProgressLoading(false);
            }
        }

        private void showImageProgressLoading(boolean show) {
            if (show) {
                coverartView.setVisibility(View.INVISIBLE);
                coverartLoadingView.setVisibility(View.VISIBLE);
            } else {
                coverartLoadingView.setVisibility(View.GONE);
                coverartView.setVisibility(View.VISIBLE);
            }
        }

    }

    public PagedReleaseAdapter(RetryCallback retryCallback, String releaseMbid) {
        super(DIFF_CALLBACK, retryCallback);
        this.releaseMbid = releaseMbid;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return NetworkStateViewHolder.VIEW_HOLDER_LAYOUT;
        } else {
            return PagedReleaseViewHolder.VIEW_HOLDER_LAYOUT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case PagedReleaseViewHolder.VIEW_HOLDER_LAYOUT:
                return PagedReleaseViewHolder.create(parent);
            case NetworkStateViewHolder.VIEW_HOLDER_LAYOUT:
                return NetworkStateViewHolder.create(parent, retryCallback);
            default:
                throw new IllegalArgumentException("unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case PagedReleaseViewHolder.VIEW_HOLDER_LAYOUT:
                Release release = getItem(position);
                ((PagedReleaseViewHolder) holder).bindTo(release, releaseMbid);
                if (holderClickListener != null) {
                    holder.itemView.setOnClickListener(view -> holderClickListener.onClick(release));
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
}
