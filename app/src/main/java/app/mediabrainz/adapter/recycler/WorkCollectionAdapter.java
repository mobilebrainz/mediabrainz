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
import app.mediabrainz.api.model.Work;


public class WorkCollectionAdapter extends BaseRecyclerViewAdapter<WorkCollectionAdapter.WorkCollectionViewHolder> {

    private List<Work> works;

    public static class WorkCollectionViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_work_collection;

        private TextView workNameTextView;
        private ImageView deleteButton;

        public static WorkCollectionViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new WorkCollectionViewHolder(view);
        }

        private WorkCollectionViewHolder(View v) {
            super(v);
            workNameTextView = v.findViewById(R.id.work_name);
            deleteButton = v.findViewById(R.id.delete);
        }

        public void bindTo(Work work, boolean isPrivate) {
            deleteButton.setVisibility(isPrivate ? View.VISIBLE : View.GONE);
            workNameTextView.setText(work.getTitle());
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

    public WorkCollectionAdapter(List<Work> works, boolean isPrivate) {
        this.works = works;
        this.isPrivate = isPrivate;
        Collections.sort(this.works, (a1, a2) -> (a1.getTitle()).compareTo(a2.getTitle()));
    }

    @Override
    public void onBind(WorkCollectionViewHolder holder, final int position) {
        holder.setOnDeleteListener(onDeleteListener);
        holder.bindTo(works.get(position), isPrivate);
    }

    @Override
    public int getItemCount() {
        return works.size();
    }

    @NonNull
    @Override
    public WorkCollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return WorkCollectionViewHolder.create(parent);
    }

    public interface OnDeleteListener {
        void onDelete(int position);
    }

    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }
}
