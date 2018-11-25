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
import app.mediabrainz.api.model.Area;


public class AreaCollectionAdapter extends BaseRecyclerViewAdapter<AreaCollectionAdapter.AreaCollectionViewHolder> {

    private List<Area> areas;

    public static class AreaCollectionViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_area_collection;

        private TextView areaNameTextView;
        private ImageView deleteButton;

        public static AreaCollectionViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new AreaCollectionViewHolder(view);
        }

        private AreaCollectionViewHolder(View v) {
            super(v);
            areaNameTextView = v.findViewById(R.id.area_name);
            deleteButton = v.findViewById(R.id.delete);
        }

        public void bindTo(Area area, boolean isPrivate) {
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

    public AreaCollectionAdapter(List<Area> areas, boolean isPrivate) {
        this.areas = areas;
        this.isPrivate = isPrivate;
        Collections.sort(this.areas, (a1, a2) -> (a1.getName()).compareTo(a2.getName()));
    }

    @Override
    public void onBind(AreaCollectionViewHolder holder, final int position) {
        holder.setOnDeleteListener(onDeleteListener);
        holder.bindTo(areas.get(position), isPrivate);
    }

    @Override
    public int getItemCount() {
        return areas.size();
    }

    @NonNull
    @Override
    public AreaCollectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return AreaCollectionViewHolder.create(parent);
    }

    public interface OnDeleteListener {
        void onDelete(int position);
    }

    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }
}
