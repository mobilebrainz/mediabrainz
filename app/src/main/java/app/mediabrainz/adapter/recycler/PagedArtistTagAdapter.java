package app.mediabrainz.adapter.recycler;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

import app.mediabrainz.R;
import app.mediabrainz.api.site.TagEntity;


public class PagedArtistTagAdapter extends BasePagedListAdapter<TagEntity> {

    public static class PagedArtistTagViewHolder extends RecyclerView.ViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_artist_tag;

        private TextView artistNameView;
        private TextView commentView;

        private PagedArtistTagViewHolder(View v) {
            super(v);
            artistNameView = v.findViewById(R.id.artist_name);
            commentView = v.findViewById(R.id.comment);
        }

        public static PagedArtistTagViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new PagedArtistTagViewHolder(view);
        }

        private void bindTo(TagEntity tagEntity) {
            artistNameView.setText(tagEntity.getName());
            commentView.setText(tagEntity.getArtistComment());
        }
    }

    public PagedArtistTagAdapter(RetryCallback retryCallback) {
        super(DIFF_CALLBACK, retryCallback);
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return NetworkStateViewHolder.VIEW_HOLDER_LAYOUT;
        } else {
            return PagedArtistTagViewHolder.VIEW_HOLDER_LAYOUT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case PagedArtistTagViewHolder.VIEW_HOLDER_LAYOUT:
                return PagedArtistTagViewHolder.create(parent);
            case NetworkStateViewHolder.VIEW_HOLDER_LAYOUT:
                return NetworkStateViewHolder.create(parent, retryCallback);
            default:
                throw new IllegalArgumentException("unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case PagedArtistTagViewHolder.VIEW_HOLDER_LAYOUT:
                TagEntity tagEntity = getItem(position);
                ((PagedArtistTagViewHolder) holder).bindTo(tagEntity);
                if (holderClickListener != null) {
                    holder.itemView.setOnClickListener(view -> holderClickListener.onClick(tagEntity));
                }
                break;
            case NetworkStateViewHolder.VIEW_HOLDER_LAYOUT:
                ((NetworkStateViewHolder) holder).bindTo(networkState);
                break;
        }
    }

    private static DiffUtil.ItemCallback<TagEntity> DIFF_CALLBACK = new DiffUtil.ItemCallback<TagEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull TagEntity oldItem, @NonNull TagEntity newItem) {
            return oldItem.getMbid().equals(newItem.getMbid());
        }

        @Override
        public boolean areContentsTheSame(@NonNull TagEntity oldItem, @NonNull TagEntity newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

    public interface HolderClickListener {
        void onClick(TagEntity tagEntity);
    }

    private HolderClickListener holderClickListener;

    public void setHolderClickListener(HolderClickListener holderClickListener) {
        this.holderClickListener = holderClickListener;
    }
}
