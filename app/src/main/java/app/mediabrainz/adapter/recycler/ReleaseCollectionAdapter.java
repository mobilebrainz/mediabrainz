package app.mediabrainz.adapter.recycler;

import android.support.annotation.NonNull;
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
import app.mediabrainz.api.model.Release;

import java.util.Collections;
import java.util.List;

import static app.mediabrainz.MediaBrainzApp.api;


public class ReleaseCollectionAdapter extends BaseRecyclerViewAdapter<ReleaseCollectionAdapter.ReleaseCollectionViewHolder> {

    private List<Release> releases;

    public static class ReleaseCollectionViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_release_collection;

        private Release release;

        private ImageView coverart;
        private ProgressBar progressLoading;
        private TextView releaseName;
        private ImageView deleteBtn;

        public static ReleaseCollectionViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new ReleaseCollectionViewHolder(view);
        }

        private ReleaseCollectionViewHolder(View v) {
            super(v);
            coverart = v.findViewById(R.id.release_image);
            progressLoading = v.findViewById(R.id.image_loading);
            releaseName = v.findViewById(R.id.release_name);
            deleteBtn = v.findViewById(R.id.delete);
        }

        public void bindTo(Release release, boolean isPrivate) {
            deleteBtn.setVisibility(isPrivate ? View.VISIBLE : View.GONE);
            this.release = release;
            releaseName.setText(release.getTitle());
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
                progressLoading.setVisibility(View.VISIBLE);
            } else {
                progressLoading.setVisibility(View.GONE);
                coverart.setVisibility(View.VISIBLE);
            }
        }

        public void setOnDeleteListener(OnDeleteListener listener) {
            deleteBtn.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(getAdapterPosition());
                }
            });
        }

    }

    private boolean isPrivate;

    public ReleaseCollectionAdapter(List<Release> releases, boolean isPrivate) {
        this.releases = releases;
        this.isPrivate = isPrivate;
        Collections.sort(this.releases, (a1, a2) -> (a1.getTitle()).compareTo(a2.getTitle()));
    }

    @Override
    public void onBind(ReleaseCollectionViewHolder holder, final int position) {
        holder.setOnDeleteListener(onDeleteListener);
        holder.bindTo(releases.get(position), isPrivate);
    }

    @Override
    public int getItemCount() {
        return releases.size();
    }

    @NonNull
    @Override
    public ReleaseCollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ReleaseCollectionViewHolder.create(parent);
    }

    public interface OnDeleteListener {
        void onDelete(int position);
    }

    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }
}
