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

import java.util.Objects;

import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.R;
import app.mediabrainz.apihandler.StringMapper;
import app.mediabrainz.api.coverart.CoverArtImage;
import app.mediabrainz.api.model.Rating;
import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.intent.ActivityFactory;
import app.mediabrainz.util.ShowUtil;

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.MediaBrainzApp.oauth;
import static app.mediabrainz.api.coverart.CoverArtImage.Thumbnails.SMALL_SIZE;


public class ReleaseGroupsAdapter extends BasePagedListAdapter<ReleaseGroup> {

    public static class ReleaseGroupsViewHolder extends RecyclerView.ViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_release_group;

        private ImageView imageView;
        private ProgressBar progressLoading;
        private RatingBar userRatingBar;
        private TextView ratingView;
        private TextView releaseNameView;
        private TextView releaseTypeYearView;
        private LinearLayout ratingContainer;

        private ReleaseGroupsViewHolder(View v) {
            super(v);
            imageView = itemView.findViewById(R.id.img);
            progressLoading = itemView.findViewById(R.id.loading);
            releaseNameView = itemView.findViewById(R.id.name);
            releaseTypeYearView = itemView.findViewById(R.id.type_year);

            ratingContainer = itemView.findViewById(R.id.rating_container);
            userRatingBar = itemView.findViewById(R.id.user_ratingbar);
            ratingView = itemView.findViewById(R.id.rating);
        }

        public static ReleaseGroupsViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new ReleaseGroupsViewHolder(view);
        }

        private void bindTo(ReleaseGroup releaseGroup) {
            releaseNameView.setText(releaseGroup.getTitle());

            setAllRating(releaseGroup);
            setUserRating(releaseGroup);

            String year = releaseGroup.getFirstReleaseDate();
            year = !TextUtils.isEmpty(year) ? year.substring(0, 4) : "";

            String type = StringMapper.mapReleaseGroupTypeString(releaseGroup);
            releaseTypeYearView.setText(year + " (" + type + ")");

            if (MediaBrainzApp.getPreferences().isLoadImagesEnabled()) {
                loadImage(releaseGroup.getId());
            } else {
                imageView.setVisibility(View.VISIBLE);
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
                    rb.setRating(userRatingBar.getRating());
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
                                            userRatingBar.setRating(rating);
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
                                    });
                        } else {
                            ActivityFactory.startLoginActivity(itemView.getContext());
                        }
                    });
                }
            } else {
                ActivityFactory.startLoginActivity(itemView.getContext());
            }
        }

        private void loadImage(String albumMbid) {
            showImageProgressLoading(true);
            api.getReleaseGroupCoverArt(
                    albumMbid,
                    coverArt -> {
                        CoverArtImage.Thumbnails thumbnails = coverArt.getFrontThumbnails();
                        if (thumbnails != null && !TextUtils.isEmpty(thumbnails.getSmall())) {
                            Picasso.get().load(thumbnails.getSmall())
                                    .resize(SMALL_SIZE, SMALL_SIZE)
                                    .into(imageView, new Callback() {
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
                imageView.setVisibility(View.INVISIBLE);
                progressLoading.setVisibility(View.VISIBLE);
            } else {
                progressLoading.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
            }
        }

        private void setAllRating(ReleaseGroup releaseGroup) {
            Rating rating = releaseGroup.getRating();
            if (rating != null) {
                Float r = rating.getValue();
                if (r != null) {
                    ratingView.setText(itemView.getContext().getString(R.string.rating_text, r, rating.getVotesCount()));
                } else {
                    ratingView.setText(itemView.getContext().getString(R.string.rating_text, 0.0, 0));
                }
            }
        }

        private void setUserRating(ReleaseGroup releaseGroup) {
            Rating rating = releaseGroup.getUserRating();
            if (rating != null) {
                Float r = rating.getValue();
                if (r == null) r = 0f;
                userRatingBar.setRating(r);
            }
        }

    }

    public ReleaseGroupsAdapter(RetryCallback retryCallback) {
        super(DIFF_CALLBACK, retryCallback);
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return NetworkStateViewHolder.VIEW_HOLDER_LAYOUT;
        } else {
            return ReleaseGroupsViewHolder.VIEW_HOLDER_LAYOUT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ReleaseGroupsViewHolder.VIEW_HOLDER_LAYOUT:
                return ReleaseGroupsViewHolder.create(parent);
            case NetworkStateViewHolder.VIEW_HOLDER_LAYOUT:
                return NetworkStateViewHolder.create(parent, retryCallback);
            default:
                throw new IllegalArgumentException("unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ReleaseGroupsViewHolder.VIEW_HOLDER_LAYOUT:
                ReleaseGroup releaseGroup = getItem(position);
                ((ReleaseGroupsViewHolder) holder).bindTo(releaseGroup);
                if (holderClickListener != null) {
                    holder.itemView.setOnClickListener(view -> holderClickListener.onClick(releaseGroup));
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

}
