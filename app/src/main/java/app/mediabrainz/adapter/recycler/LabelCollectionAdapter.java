package app.mediabrainz.adapter.recycler;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import app.mediabrainz.R;
import app.mediabrainz.api.model.Label;


public class LabelCollectionAdapter extends BaseRecyclerViewAdapter<LabelCollectionAdapter.LabelCollectionViewHolder> {

    private List<Label> labels;

    public static class LabelCollectionViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_label_collection;

        private TextView labelNameTextView;
        private ImageView deleteButton;

        public static LabelCollectionViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new LabelCollectionViewHolder(view);
        }

        public LabelCollectionViewHolder(View v) {
            super(v);
            labelNameTextView = v.findViewById(R.id.label_name);
            deleteButton = v.findViewById(R.id.delete);
        }

        public void bindTo(Label label, boolean isPrivate) {
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

    public LabelCollectionAdapter(List<Label> labels, boolean isPrivate) {
        this.labels = labels;
        this.isPrivate = isPrivate;
        Collections.sort(this.labels, (a1, a2) -> (a1.getName()).compareTo(a2.getName()));
    }

    @Override
    public void onBind(LabelCollectionViewHolder holder, final int position) {
        holder.setOnDeleteListener(onDeleteListener);
        holder.bindTo(labels.get(position), isPrivate);
    }

    @Override
    public int getItemCount() {
        return labels.size();
    }

    @NonNull
    @Override
    public LabelCollectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return LabelCollectionViewHolder.create(parent);
    }

    public interface OnDeleteListener {
        void onDelete(int position);
    }

    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }
}
