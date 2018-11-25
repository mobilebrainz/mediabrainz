package app.mediabrainz.adapter.recycler;

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

import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.R;
import app.mediabrainz.apihandler.StringMapper;
import app.mediabrainz.api.core.ApiUtils;
import app.mediabrainz.api.coverart.CoverArtImage;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.ReleaseGroup;

import static app.mediabrainz.MediaBrainzApp.api;


public class ReleaseGroupSearchAdapter extends BaseRecyclerViewAdapter<ReleaseGroupSearchAdapter.ReleaseGroupSearchViewHolder> {

    private List<ReleaseGroup> releaseGroups;

    public static class ReleaseGroupSearchViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_search_release_group;

        private ImageView coverart;
        private ProgressBar coverartLoading;
        private TextView releaseName;
        private TextView releaseType;
        private TextView artistName;
        private TextView tags;

        public static ReleaseGroupSearchViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new ReleaseGroupSearchViewHolder(view);
        }

        private ReleaseGroupSearchViewHolder(View v) {
            super(v);
            coverart = v.findViewById(R.id.coverart);
            coverartLoading = v.findViewById(R.id.coverart_loading);
            releaseName = v.findViewById(R.id.release_name);
            releaseType = v.findViewById(R.id.release_type);
            artistName = v.findViewById(R.id.artist_name);
            tags = v.findViewById(R.id.tags);
        }

        public void bindTo(ReleaseGroup releaseGroup) {
            releaseName.setText(releaseGroup.getTitle());
            List<Artist.ArtistCredit> artists = releaseGroup.getArtistCredit();
            Artist artist = null;
            if (artists != null && !artists.isEmpty()) {
                artist = artists.get(0).getArtist();
                artistName.setText(artist.getName());
                if (releaseGroup.getTags() != null && !releaseGroup.getTags().isEmpty()) {
                    tags.setText(ApiUtils.getStringFromList(releaseGroup.getTags(), ", "));
                } else {
                    tags.setText(artist.getDisambiguation());
                }
            }
            releaseType.setText(StringMapper.mapReleaseGroupOneType(releaseGroup));

            if (MediaBrainzApp.getPreferences().isLoadImagesEnabled()) {
                loadImage(releaseGroup.getId());
            } else {
                coverart.setVisibility(View.VISIBLE);
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

    public ReleaseGroupSearchAdapter(List<ReleaseGroup> releaseGroups) {
        this.releaseGroups = releaseGroups;
    }

    @Override
    public void onBind(ReleaseGroupSearchViewHolder holder, final int position) {
        holder.bindTo(releaseGroups.get(position));
    }

    @Override
    public int getItemCount() {
        return releaseGroups.size();
    }

    @Override
    public ReleaseGroupSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ReleaseGroupSearchViewHolder.create(parent);
    }
}
