package app.mediabrainz.adapter.recycler;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.R;
import app.mediabrainz.api.coverart.CoverArtImage;
import app.mediabrainz.api.model.Label;
import app.mediabrainz.api.model.Media;
import app.mediabrainz.api.model.Release;
import app.mediabrainz.util.MbUtils;
import app.mediabrainz.util.StringFormat;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static app.mediabrainz.MediaBrainzApp.api;


public class ReleaseAdapter extends BaseRecyclerViewAdapter<ReleaseAdapter.ReleaseViewHolder> {

    private String releaseMbid;
    private List<Release> releases;

    public static class ReleaseViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_release;

        private CardView cardView;
        private ImageView coverart;
        private ProgressBar coverartLoading;
        private TextView date;
        private TextView releaseName;
        private TextView countryLabel;
        private TextView format;
        private TextView status;
        private TextView catalog;
        private TextView barcode;

        public static ReleaseViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new ReleaseViewHolder(view);
        }

        private ReleaseViewHolder(View v) {
            super(v);
            cardView = v.findViewById(R.id.release_card);
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

        public void bindTo(Release release, String releaseMbid) {
            if (release.getId().equals(releaseMbid)) {
                cardView.setBackgroundResource(R.color.md_orange_50);
            }

            date.setText(release.getDate());
            if (!TextUtils.isEmpty(release.getStatus())) {
                status.setText(release.getStatus());
            } else {
                status.setVisibility(View.GONE);
            }

            releaseName.setText(release.getTitle());
            if (!TextUtils.isEmpty(release.getBarcode())) {
                barcode.setText(itemView.getResources().getString(R.string.r_barcode, release.getBarcode()));
            } else {
                barcode.setVisibility(View.GONE);
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

            if (MediaBrainzApp.getPreferences().isLoadImagesEnabled()) {
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

    public ReleaseAdapter(List<Release> releases, String releaseMbid) {
        this.releases = releases;
        this.releaseMbid = releaseMbid;
        Comparator<Release> sortDate = (r1, r2) -> MbUtils.getNumberDate(r1.getDate()) - MbUtils.getNumberDate(r2.getDate());
        Collections.sort(this.releases, sortDate);
    }

    @Override
    public void onBind(ReleaseViewHolder holder, final int position) {
        holder.bindTo(releases.get(position), releaseMbid);
    }

    @Override
    public int getItemCount() {
        return releases.size();
    }

    @NonNull
    @Override
    public ReleaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ReleaseViewHolder.create(parent);
    }
}
