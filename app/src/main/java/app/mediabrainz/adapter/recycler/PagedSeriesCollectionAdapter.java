package app.mediabrainz.adapter.recycler;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

import app.mediabrainz.R;
import app.mediabrainz.api.model.Series;


public class PagedSeriesCollectionAdapter extends BasePagedListAdapter<Series> {

    public static class PagedSeriesCollectionViewHolder extends RecyclerView.ViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_series_collection;

        private TextView seriesNameTextView;
        private ImageView deleteButton;

        private PagedSeriesCollectionViewHolder(View v) {
            super(v);
            seriesNameTextView = v.findViewById(R.id.series_name);
            deleteButton = v.findViewById(R.id.delete);
        }

        public static PagedSeriesCollectionViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new PagedSeriesCollectionViewHolder(view);
        }

        public void bindTo(Series series, boolean isPrivate) {
            deleteButton.setVisibility(isPrivate ? View.VISIBLE : View.GONE);
            seriesNameTextView.setText(series.getName());
        }

        public void setOnDeleteListener(OnDeleteListener listener) {
            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(getAdapterPosition());
                }
            });
        }
    }

    private boolean isPrivate;

    public PagedSeriesCollectionAdapter(RetryCallback retryCallback, boolean isPrivate) {
        super(DIFF_CALLBACK, retryCallback);
        this.isPrivate = isPrivate;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return NetworkStateViewHolder.VIEW_HOLDER_LAYOUT;
        } else {
            return PagedSeriesCollectionViewHolder.VIEW_HOLDER_LAYOUT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case PagedSeriesCollectionViewHolder.VIEW_HOLDER_LAYOUT:
                return PagedSeriesCollectionViewHolder.create(parent);
            case NetworkStateViewHolder.VIEW_HOLDER_LAYOUT:
                return NetworkStateViewHolder.create(parent, retryCallback);
            default:
                throw new IllegalArgumentException("unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case PagedSeriesCollectionViewHolder.VIEW_HOLDER_LAYOUT:
                Series series = getItem(position);
                ((PagedSeriesCollectionViewHolder) holder).bindTo(series, isPrivate);
                if (holderClickListener != null) {
                    holder.itemView.setOnClickListener(view -> holderClickListener.onClick(series));
                }
                if (onDeleteListener != null) {
                    ((PagedSeriesCollectionViewHolder) holder).setOnDeleteListener(onDeleteListener);
                }
                break;
            case NetworkStateViewHolder.VIEW_HOLDER_LAYOUT:
                ((NetworkStateViewHolder) holder).bindTo(networkState);
                break;
        }
    }

    private static DiffUtil.ItemCallback<Series> DIFF_CALLBACK = new DiffUtil.ItemCallback<Series>() {
        @Override
        public boolean areItemsTheSame(@NonNull Series oldItem, @NonNull Series newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Series oldItem, @NonNull Series newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

    public interface HolderClickListener {
        void onClick(Series series);
    }

    private HolderClickListener holderClickListener;

    public void setHolderClickListener(HolderClickListener holderClickListener) {
        this.holderClickListener = holderClickListener;
    }

    public interface OnDeleteListener {
        void onDelete(int position);
    }

    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }
}
