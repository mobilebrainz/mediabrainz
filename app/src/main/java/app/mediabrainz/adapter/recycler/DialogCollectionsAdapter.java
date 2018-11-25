package app.mediabrainz.adapter.recycler;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.mediabrainz.R;
import app.mediabrainz.api.model.Collection;

import java.util.Collections;
import java.util.List;


public class DialogCollectionsAdapter extends BaseRecyclerViewAdapter<DialogCollectionsAdapter.DialogCollectionsViewHolder> {

    private List<Collection> collections;

    public static class DialogCollectionsViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_dialog_collections;

        private TextView collectionName;
        private TextView collectionCount;

        public static DialogCollectionsViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new DialogCollectionsViewHolder(view);
        }

        private DialogCollectionsViewHolder(View v) {
            super(v);
            collectionName = v.findViewById(R.id.collection_name);
            collectionCount = v.findViewById(R.id.collection_count);
        }

        public void bindTo(Collection collection) {
            collectionName.setText(collection.getName());
            collectionCount.setText(String.valueOf(collection.getCount()));
        }
    }

    public DialogCollectionsAdapter(List<Collection> collections) {
        this.collections = collections;
        Collections.sort(this.collections, (c1, c2) -> (c1.getName()).compareTo(c2.getName()));
    }

    @Override
    public void onBind(DialogCollectionsViewHolder holder, final int position) {
        holder.bindTo(collections.get(position));
    }

    @Override
    public int getItemCount() {
        return collections.size();
    }

    @NonNull
    @Override
    public DialogCollectionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return DialogCollectionsViewHolder.create(parent);
    }

}
