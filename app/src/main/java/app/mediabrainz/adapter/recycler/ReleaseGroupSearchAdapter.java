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

        private ImageView coverartView;
        private ProgressBar coverartLoadingView;
        private TextView releaseNameView;
        private TextView releaseTypeView;
        private TextView artistNameView;
        private TextView tagsView;

        public static ReleaseGroupSearchViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new ReleaseGroupSearchViewHolder(view);
        }

        private ReleaseGroupSearchViewHolder(View v) {
            super(v);
            coverartView = v.findViewById(R.id.coverartView);
            coverartLoadingView = v.findViewById(R.id.coverartLoadingView);
            releaseNameView = v.findViewById(R.id.releaseNameView);
            releaseTypeView = v.findViewById(R.id.releaseTypeView);
            artistNameView = v.findViewById(R.id.artistNameView);
            tagsView = v.findViewById(R.id.tagsView);
        }

        public void bindTo(ReleaseGroup releaseGroup) {
            releaseNameView.setText(releaseGroup.getTitle());
            List<Artist.ArtistCredit> artists = releaseGroup.getArtistCredits();
            Artist artist;
            if (artists != null && !artists.isEmpty()) {
                artist = artists.get(0).getArtist();
                artistNameView.setText(artist.getName());
                if (releaseGroup.getTags() != null && !releaseGroup.getTags().isEmpty()) {
                    tagsView.setText(ApiUtils.getStringFromList(releaseGroup.getTags(), ", "));
                } else {
                    tagsView.setText(artist.getDisambiguation());
                }
            }
            releaseTypeView.setText(StringMapper.mapReleaseGroupOneType(releaseGroup));

            if (MediaBrainzApp.getPreferences().isLoadImagesEnabled()) {
                loadImage(releaseGroup.getId());
            } else {
                coverartView.setVisibility(View.VISIBLE);
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
                coverartLoadingView.setVisibility(View.VISIBLE);
            } else {
                coverartLoadingView.setVisibility(View.GONE);
                coverartView.setVisibility(View.VISIBLE);
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
