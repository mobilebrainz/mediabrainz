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

        private ImageView coverartView;
        private ProgressBar coverartLoadingView;
        private TextView dateView;
        private TextView releaseNameView;
        private TextView countryLabelView;
        private TextView formatView;
        private TextView statusView;
        private TextView catalogView;
        private TextView barcodeView;

        public static ReleaseViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new ReleaseViewHolder(view);
        }

        private ReleaseViewHolder(View v) {
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

        public void bindTo(Release release, String releaseMbid) {
            if (release.getId().equals(releaseMbid)) {
                itemView.setBackgroundResource(R.color.md_orange_50);
            }

            dateView.setText(release.getDate());
            if (!TextUtils.isEmpty(release.getStatus())) {
                statusView.setText(release.getStatus());
            } else {
                statusView.setVisibility(View.GONE);
            }

            releaseNameView.setText(release.getTitle());
            if (!TextUtils.isEmpty(release.getBarcode())) {
                barcodeView.setText(itemView.getResources().getString(R.string.r_barcode, release.getBarcode()));
            } else {
                barcodeView.setVisibility(View.GONE);
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

            if (MediaBrainzApp.getPreferences().isLoadImagesEnabled()) {
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
