package app.mediabrainz.adapter.recycler;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import app.mediabrainz.R;
import app.mediabrainz.api.model.Collection;

import java.util.List;


public class CollectionsAdapter extends BaseRecyclerViewAdapter<CollectionsAdapter.CollectionsViewHolder> {

    private CollectionsViewHolder.OnDeleteCollectionListener onDeleteCollectionListener;
    private List<Collection> collections;
    private boolean isPrivate;

    public static class CollectionsViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        public interface OnDeleteCollectionListener {
            void onDelete(int position);
        }

        private OnDeleteCollectionListener onDeleteCollectionListener;

        public void setOnDeleteCollectionListener(OnDeleteCollectionListener onDeleteCollectionListener) {
            this.onDeleteCollectionListener = onDeleteCollectionListener;
        }

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_collections;

        private TextView collectionNameView;
        private TextView collectionCountView;
        private ImageView collectionDeleteView;

        public static CollectionsViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new CollectionsViewHolder(view);
        }

        private CollectionsViewHolder(View v) {
            super(v);
            collectionNameView = v.findViewById(R.id.collectionNameView);
            collectionCountView = v.findViewById(R.id.collectionCountView);
            collectionDeleteView = v.findViewById(R.id.collectionDeleteView);
        }

        public void bindTo(Collection collection, boolean isPrivate) {
            collectionDeleteView.setVisibility(isPrivate ? View.VISIBLE : View.GONE);

            collectionDeleteView.setOnClickListener(v -> {
                if (onDeleteCollectionListener != null) {
                    onDeleteCollectionListener.onDelete(getAdapterPosition());
                }
            });

            collectionNameView.setText(collection.getName());
            collectionCountView.setText(String.valueOf(collection.getCount()));
        }

    }

    public CollectionsAdapter(List<Collection> collections, boolean isPrivate) {
        this.collections = collections;
        this.isPrivate = isPrivate;
        //Collections.sort(this.collections, (t1, t2) -> t2.getCount() - t1.getCount());
    }

    @Override
    public void onBind(CollectionsViewHolder holder, final int position) {
        holder.setOnDeleteCollectionListener(onDeleteCollectionListener);
        holder.bindTo(collections.get(position), isPrivate);
    }

    @Override
    public int getItemCount() {
        return collections.size();
    }

    @NonNull
    @Override
    public CollectionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return CollectionsViewHolder.create(parent);
    }

    public void setOnDeleteCollectionListener(CollectionsViewHolder.OnDeleteCollectionListener onDeleteCollectionListener) {
        this.onDeleteCollectionListener = onDeleteCollectionListener;
    }

}
