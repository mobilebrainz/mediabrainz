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
import app.mediabrainz.api.model.Event;


public class PagedEventCollectionAdapter extends BasePagedListAdapter<Event> {

    public static class PagedEventCollectionViewHolder extends RecyclerView.ViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_event_collection;

        private TextView eventNameView;
        private ImageView deleteView;

        private PagedEventCollectionViewHolder(View v) {
            super(v);
            eventNameView = v.findViewById(R.id.eventNameView);
            deleteView = v.findViewById(R.id.deleteView);
        }

        public static PagedEventCollectionViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new PagedEventCollectionViewHolder(view);
        }

        private void bindTo(Event event, boolean isPrivate) {
            deleteView.setVisibility(isPrivate ? View.VISIBLE : View.GONE);
            eventNameView.setText(event.getName());
        }

        public void setOnDeleteListener(OnDeleteListener listener) {
            deleteView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(getAdapterPosition());
                }
            });
        }
    }

    private boolean isPrivate;

    public PagedEventCollectionAdapter(RetryCallback retryCallback, boolean isPrivate) {
        super(DIFF_CALLBACK, retryCallback);
        this.isPrivate = isPrivate;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return NetworkStateViewHolder.VIEW_HOLDER_LAYOUT;
        } else {
            return PagedEventCollectionViewHolder.VIEW_HOLDER_LAYOUT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case PagedEventCollectionViewHolder.VIEW_HOLDER_LAYOUT:
                return PagedEventCollectionViewHolder.create(parent);
            case NetworkStateViewHolder.VIEW_HOLDER_LAYOUT:
                return NetworkStateViewHolder.create(parent, retryCallback);
            default:
                throw new IllegalArgumentException("unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case PagedEventCollectionViewHolder.VIEW_HOLDER_LAYOUT:
                Event event = getItem(position);
                ((PagedEventCollectionViewHolder) holder).bindTo(event, isPrivate);
                if (holderClickListener != null) {
                    holder.itemView.setOnClickListener(view -> holderClickListener.onClick(event));
                }
                if (onDeleteListener != null) {
                    ((PagedEventCollectionViewHolder) holder).setOnDeleteListener(onDeleteListener);
                }
                break;
            case NetworkStateViewHolder.VIEW_HOLDER_LAYOUT:
                ((NetworkStateViewHolder) holder).bindTo(networkState);
                break;
        }
    }

    private static DiffUtil.ItemCallback<Event> DIFF_CALLBACK = new DiffUtil.ItemCallback<Event>() {
        @Override
        public boolean areItemsTheSame(@NonNull Event oldItem, @NonNull Event newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Event oldItem, @NonNull Event newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

    public interface HolderClickListener {
        void onClick(Event event);
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
