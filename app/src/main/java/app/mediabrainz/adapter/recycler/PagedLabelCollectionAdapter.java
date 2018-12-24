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
import app.mediabrainz.api.model.Label;


public class PagedLabelCollectionAdapter extends BasePagedListAdapter<Label> {

    public static class PagedLabelCollectionViewHolder extends RecyclerView.ViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_label_collection;

        private TextView labelNameTextView;
        private ImageView deleteButton;

        private PagedLabelCollectionViewHolder(View v) {
            super(v);
            labelNameTextView = v.findViewById(R.id.label_name);
            deleteButton = v.findViewById(R.id.delete);
        }

        public static PagedLabelCollectionViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new PagedLabelCollectionViewHolder(view);
        }

        private void bindTo(Label label, boolean isPrivate) {
            deleteButton.setVisibility(isPrivate ? View.VISIBLE : View.GONE);
            labelNameTextView.setText(label.getName());
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

    public PagedLabelCollectionAdapter(RetryCallback retryCallback, boolean isPrivate) {
        super(DIFF_CALLBACK, retryCallback);
        this.isPrivate = isPrivate;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return NetworkStateViewHolder.VIEW_HOLDER_LAYOUT;
        } else {
            return PagedLabelCollectionViewHolder.VIEW_HOLDER_LAYOUT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case PagedLabelCollectionViewHolder.VIEW_HOLDER_LAYOUT:
                return PagedLabelCollectionViewHolder.create(parent);
            case NetworkStateViewHolder.VIEW_HOLDER_LAYOUT:
                return NetworkStateViewHolder.create(parent, retryCallback);
            default:
                throw new IllegalArgumentException("unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case PagedLabelCollectionViewHolder.VIEW_HOLDER_LAYOUT:
                Label label = getItem(position);
                ((PagedLabelCollectionViewHolder) holder).bindTo(label, isPrivate);
                if (holderClickListener != null) {
                    holder.itemView.setOnClickListener(view -> holderClickListener.onClick(label));
                }
                if (onDeleteListener != null) {
                    ((PagedLabelCollectionViewHolder) holder).setOnDeleteListener(onDeleteListener);
                }
                break;
            case NetworkStateViewHolder.VIEW_HOLDER_LAYOUT:
                ((NetworkStateViewHolder) holder).bindTo(networkState);
                break;
        }
    }

    private static DiffUtil.ItemCallback<Label> DIFF_CALLBACK = new DiffUtil.ItemCallback<Label>() {
        @Override
        public boolean areItemsTheSame(@NonNull Label oldItem, @NonNull Label newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Label oldItem, @NonNull Label newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

    public interface HolderClickListener {
        void onClick(Label label);
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
