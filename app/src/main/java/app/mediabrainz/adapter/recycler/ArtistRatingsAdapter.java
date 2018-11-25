package app.mediabrainz.adapter.recycler;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.Objects;

import app.mediabrainz.R;
import app.mediabrainz.api.site.Rating;
import app.mediabrainz.util.ShowUtil;

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.MediaBrainzApp.oauth;


public class ArtistRatingsAdapter extends BasePagedListAdapter<Rating> {

    public static class ArtistRatingsViewHolder extends RecyclerView.ViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_artist_rating;

        private TextView artistView;
        private TextView commentView;
        private RatingBar ratingBar;
        private View ratingContainer;

        private ArtistRatingsViewHolder(View v) {
            super(v);
            artistView = v.findViewById(R.id.artist);
            commentView = v.findViewById(R.id.comment);
            ratingBar = v.findViewById(R.id.rating);
            ratingContainer = v.findViewById(R.id.rating_container);
        }

        public static ArtistRatingsViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new ArtistRatingsViewHolder(view);
        }

        private void bindTo(Rating rating) {
            artistView.setText(rating.getName());
            commentView.setText(rating.getArtistComment());
            ratingBar.setRating(rating.getRate());

            if (oauth.hasAccount() && oauth.getName().equals(rating.getUser())) {
                ratingContainer.setOnClickListener(v -> showRatingBar(rating));
            }
        }

        private void showRatingBar(Rating rating) {
            AlertDialog alertDialog = new AlertDialog.Builder(itemView.getContext()).create();
            alertDialog.show();
            Window win = alertDialog.getWindow();
            if (win != null) {
                win.setContentView(R.layout.dialog_rating_bar);
                RatingBar rb = win.findViewById(R.id.rating_bar);
                View ratingProgress = win.findViewById(R.id.loading);
                TextView title = win.findViewById(R.id.title_text);
                title.setText(itemView.getResources().getString(R.string.rate_entity, rating.getName()));
                rb.setRating(ratingBar.getRating());

                rb.setOnRatingBarChangeListener((RatingBar rateBar, float rate, boolean fromUser) -> {
                    if (ratingProgress.getVisibility() == View.INVISIBLE && fromUser) {
                        ratingProgress.setVisibility(View.VISIBLE);
                        rb.setAlpha(0.3F);
                        api.postArtistRating(
                                rating.getMbid(), rate,
                                metadata -> {
                                    ratingProgress.setVisibility(View.INVISIBLE);
                                    rb.setAlpha(1.0F);
                                    if (metadata.getMessage().getText().equals("OK")) {
                                        ratingBar.setRating(rate);
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
                    }
                });
            }
        }

    }

    public ArtistRatingsAdapter(RetryCallback retryCallback) {
        super(DIFF_CALLBACK, retryCallback);
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return NetworkStateViewHolder.VIEW_HOLDER_LAYOUT;
        } else {
            return ArtistRatingsViewHolder.VIEW_HOLDER_LAYOUT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ArtistRatingsViewHolder.VIEW_HOLDER_LAYOUT:
                return ArtistRatingsViewHolder.create(parent);
            case NetworkStateViewHolder.VIEW_HOLDER_LAYOUT:
                return NetworkStateViewHolder.create(parent, retryCallback);
            default:
                throw new IllegalArgumentException("unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ArtistRatingsViewHolder.VIEW_HOLDER_LAYOUT:
                Rating rating = getItem(position);
                ((ArtistRatingsViewHolder) holder).bindTo(rating);
                if (holderClickListener != null) {
                    holder.itemView.setOnClickListener(view -> holderClickListener.onClick(rating));
                }
                break;
            case NetworkStateViewHolder.VIEW_HOLDER_LAYOUT:
                ((NetworkStateViewHolder) holder).bindTo(networkState);
                break;
        }
    }

    private static DiffUtil.ItemCallback<Rating> DIFF_CALLBACK = new DiffUtil.ItemCallback<Rating>() {
        @Override
        public boolean areItemsTheSame(@NonNull Rating oldItem, @NonNull Rating newItem) {
            return oldItem.getMbid().equals(newItem.getMbid());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Rating oldItem, @NonNull Rating newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

    public interface HolderClickListener {
        void onClick(Rating rating);
    }

    private HolderClickListener holderClickListener;

    public void setHolderClickListener(HolderClickListener holderClickListener) {
        this.holderClickListener = holderClickListener;
    }
}
