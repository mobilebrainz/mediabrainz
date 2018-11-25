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
import app.mediabrainz.api.model.Series;


public class SeriesCollectionAdapter extends BaseRecyclerViewAdapter<SeriesCollectionAdapter.SeriesCollectionViewHolder> {

    private List<Series> serieses;

    public static class SeriesCollectionViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_series_collection;

        private TextView seriesNameTextView;
        private ImageView deleteButton;

        public static SeriesCollectionViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new SeriesCollectionViewHolder(view);
        }

        private SeriesCollectionViewHolder(View v) {
            super(v);
            seriesNameTextView = v.findViewById(R.id.series_name);
            deleteButton = v.findViewById(R.id.delete);
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

    public SeriesCollectionAdapter(List<Series> serieses, boolean isPrivate) {
        this.serieses = serieses;
        this.isPrivate = isPrivate;
        Collections.sort(this.serieses, (a1, a2) -> (a1.getName()).compareTo(a2.getName()));
    }

    @Override
    public void onBind(SeriesCollectionViewHolder holder, final int position) {
        holder.setOnDeleteListener(onDeleteListener);
        holder.bindTo(serieses.get(position), isPrivate);
    }

    @Override
    public int getItemCount() {
        return serieses.size();
    }

    @NonNull
    @Override
    public SeriesCollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return SeriesCollectionViewHolder.create(parent);
    }

    public interface OnDeleteListener {
        void onDelete(int position);
    }

    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }
}
