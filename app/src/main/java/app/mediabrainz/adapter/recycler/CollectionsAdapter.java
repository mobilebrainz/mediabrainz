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

        private TextView collectionName;
        private TextView collectionCount;
        private ImageView collectionDelete;

        public static CollectionsViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new CollectionsViewHolder(view);
        }

        private CollectionsViewHolder(View v) {
            super(v);
            collectionName = v.findViewById(R.id.collection_name);
            collectionCount = v.findViewById(R.id.collection_count);
            collectionDelete = v.findViewById(R.id.collection_delete);
        }

        public void bindTo(Collection collection, boolean isPrivate) {
            collectionDelete.setVisibility(isPrivate ? View.VISIBLE : View.GONE);

            collectionDelete.setOnClickListener(v -> {
                if (onDeleteCollectionListener != null) {
                    onDeleteCollectionListener.onDelete(getAdapterPosition());
                }
            });

            collectionName.setText(collection.getName());
            collectionCount.setText(String.valueOf(collection.getCount()));
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
