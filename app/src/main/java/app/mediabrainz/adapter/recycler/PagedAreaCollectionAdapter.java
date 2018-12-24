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
import app.mediabrainz.api.model.Area;


public class PagedAreaCollectionAdapter extends BasePagedListAdapter<Area> {

    public static class PagedAreaCollectionViewHolder extends RecyclerView.ViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_area_collection;

        private TextView areaNameTextView;
        private ImageView deleteButton;

        private PagedAreaCollectionViewHolder(View v) {
            super(v);
            areaNameTextView = v.findViewById(R.id.area_name);
            deleteButton = v.findViewById(R.id.delete);
        }

        public static PagedAreaCollectionViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new PagedAreaCollectionViewHolder(view);
        }

        private void bindTo(Area area, boolean isPrivate) {
            deleteButton.setVisibility(isPrivate ? View.VISIBLE : View.GONE);
            areaNameTextView.setText(area.getName());
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

    public PagedAreaCollectionAdapter(RetryCallback retryCallback, boolean isPrivate) {
        super(DIFF_CALLBACK, retryCallback);
        this.isPrivate = isPrivate;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return NetworkStateViewHolder.VIEW_HOLDER_LAYOUT;
        } else {
            return PagedAreaCollectionViewHolder.VIEW_HOLDER_LAYOUT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case PagedAreaCollectionViewHolder.VIEW_HOLDER_LAYOUT:
                return PagedAreaCollectionViewHolder.create(parent);
            case NetworkStateViewHolder.VIEW_HOLDER_LAYOUT:
                return NetworkStateViewHolder.create(parent, retryCallback);
            default:
                throw new IllegalArgumentException("unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case PagedAreaCollectionViewHolder.VIEW_HOLDER_LAYOUT:
                Area area = getItem(position);
                ((PagedAreaCollectionViewHolder) holder).bindTo(area, isPrivate);
                if (holderClickListener != null) {
                    holder.itemView.setOnClickListener(view -> holderClickListener.onClick(area));
                }
                if (onDeleteListener != null) {
                    ((PagedAreaCollectionViewHolder) holder).setOnDeleteListener(onDeleteListener);
                }
                break;
            case NetworkStateViewHolder.VIEW_HOLDER_LAYOUT:
                ((NetworkStateViewHolder) holder).bindTo(networkState);
                break;
        }
    }

    private static DiffUtil.ItemCallback<Area> DIFF_CALLBACK = new DiffUtil.ItemCallback<Area>() {
        @Override
        public boolean areItemsTheSame(@NonNull Area oldItem, @NonNull Area newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Area oldItem, @NonNull Area newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

    public interface HolderClickListener {
        void onClick(Area area);
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
