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

        private ImageView coverart;
        private ProgressBar coverartLoading;
        private TextView date;
        private TextView releaseName;
        private TextView countryLabel;
        private TextView format;
        private TextView status;
        private TextView catalog;
        private TextView barcode;

        private PagedReleaseViewHolder(View v) {
            super(v);

            coverart = v.findViewById(R.id.coverart);
            coverartLoading = v.findViewById(R.id.coverart_loading);
            date = v.findViewById(R.id.date);
            releaseName = v.findViewById(R.id.release_name);
            countryLabel = v.findViewById(R.id.country_label);
            format = v.findViewById(R.id.format);
            status = v.findViewById(R.id.status);
            catalog = v.findViewById(R.id.catalog);
            barcode = v.findViewById(R.id.barcode);
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

            date.setText(release.getDate());
            releaseName.setText(release.getTitle());

            if (!TextUtils.isEmpty(release.getBarcode())) {
                barcode.setText(itemView.getResources().getString(R.string.r_barcode, release.getBarcode()));
            } else {
                barcode.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(release.getStatus())) {
                status.setText(itemView.getResources().getString(R.string.r_status, release.getStatus()));
            } else {
                status.setVisibility(View.GONE);
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
                    catalog.setText(itemView.getResources().getString(R.string.r_catalog, labelCatalog));
                } else {
                    catalog.setVisibility(View.GONE);
                }
            }
            countryLabel.setText(release.getCountry() + " " + labelName);

            int trackCount = 0;
            List<Media> medias = release.getMedia();
            for (Media media : medias) {
                trackCount += media.getTrackCount();
            }
            String f = StringFormat.buildReleaseFormatsString(itemView.getContext(), medias);
            format.setText(itemView.getResources().getString(R.string.r_tracks, f, trackCount));

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
                                        .into(coverart, new Callback() {
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
                coverart.setVisibility(View.INVISIBLE);
                coverartLoading.setVisibility(View.VISIBLE);
            } else {
                coverartLoading.setVisibility(View.GONE);
                coverart.setVisibility(View.VISIBLE);
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
