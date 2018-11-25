package app.mediabrainz.adapter.recycler;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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

import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.R;
import app.mediabrainz.api.coverart.CoverArtImage;
import app.mediabrainz.api.model.Rating;
import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.intent.ActivityFactory;
import app.mediabrainz.util.ShowUtil;

import java.util.Collections;
import java.util.List;

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.MediaBrainzApp.oauth;


public class ReleaseGroupCollectionAdapter extends BaseRecyclerViewAdapter<ReleaseGroupCollectionAdapter.ReleaseGroupCollectionViewHolder> {

    private List<ReleaseGroup> ReleaseGroups;

    public static class ReleaseGroupCollectionViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_release_group_collection;

        private ImageView coverart;
        private ProgressBar progressLoading;
        private TextView releaseGroupName;
        private RatingBar userRating;
        private TextView allRatingView;
        private ImageView deleteBtn;
        private LinearLayout ratingContainer;

        public static ReleaseGroupCollectionViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new ReleaseGroupCollectionViewHolder(view);
        }

        private ReleaseGroupCollectionViewHolder(View v) {
            super(v);
            coverart = v.findViewById(R.id.rg_image);
            progressLoading = v.findViewById(R.id.image_loading);
            releaseGroupName = v.findViewById(R.id.rg_name);
            deleteBtn = v.findViewById(R.id.delete);
            userRating = v.findViewById(R.id.user_rating);
            allRatingView = v.findViewById(R.id.all_rating);
            ratingContainer = v.findViewById(R.id.rating_container);
        }

        public void bindTo(ReleaseGroup releaseGroup, boolean isPrivate) {
            deleteBtn.setVisibility(isPrivate ? View.VISIBLE : View.GONE);
            releaseGroupName.setText(releaseGroup.getTitle());
            setUserRating(releaseGroup);
            setAllRating(releaseGroup);

            if (MediaBrainzApp.getPreferences().isLoadImagesEnabled()) {
                loadImage(releaseGroup.getId());
            } else {
                coverart.setVisibility(View.VISIBLE);
            }
            ratingContainer.setOnClickListener(v -> showRatingBar(releaseGroup));
        }

        private void showRatingBar(ReleaseGroup releaseGroup) {
            if (oauth.hasAccount()) {
                AlertDialog alertDialog = new AlertDialog.Builder(itemView.getContext()).create();
                alertDialog.show();
                Window win = alertDialog.getWindow();
                if (win != null) {
                    win.setContentView(R.layout.dialog_rating_bar);
                    RatingBar rb = win.findViewById(R.id.rating_bar);
                    View ratingProgress = win.findViewById(R.id.loading);
                    rb.setRating(userRating.getRating());
                    TextView title = win.findViewById(R.id.title_text);
                    title.setText(itemView.getResources().getString(R.string.rate_entity, releaseGroup.getTitle()));

                    rb.setOnRatingBarChangeListener((RatingBar ratingBar, float rating, boolean fromUser) -> {
                        if (oauth.hasAccount() && ratingProgress.getVisibility() == View.INVISIBLE && fromUser) {
                            ratingProgress.setVisibility(View.VISIBLE);
                            rb.setAlpha(0.3F);
                            api.postAlbumRating(
                                    releaseGroup.getId(), rating,
                                    metadata -> {
                                        ratingProgress.setVisibility(View.INVISIBLE);
                                        rb.setAlpha(1.0F);
                                        if (metadata.getMessage().getText().equals("OK")) {
                                            userRating.setRating(rating);
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
                                        ratingProgress.setVisibility(View.INVISIBLE);
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
                userRating.setRating(r);
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

    public ReleaseGroupCollectionAdapter(List<ReleaseGroup> ReleaseGroups, boolean isPrivate) {
        this.ReleaseGroups = ReleaseGroups;
        this.isPrivate = isPrivate;
        Collections.sort(this.ReleaseGroups, (a1, a2) -> (a1.getTitle()).compareTo(a2.getTitle()));
    }

    @Override
    public void onBind(ReleaseGroupCollectionViewHolder holder, final int position) {
        holder.setOnDeleteListener(onDeleteListener);
        holder.bindTo(ReleaseGroups.get(position), isPrivate);
    }

    @Override
    public int getItemCount() {
        return ReleaseGroups.size();
    }

    @NonNull
    @Override
    public ReleaseGroupCollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ReleaseGroupCollectionViewHolder.create(parent);
    }

    public interface OnDeleteListener {
        void onDelete(int position);
    }

    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }
}
