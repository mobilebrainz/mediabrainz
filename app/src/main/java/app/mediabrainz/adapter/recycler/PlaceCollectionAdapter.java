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
import app.mediabrainz.api.model.Place;


public class PlaceCollectionAdapter extends BaseRecyclerViewAdapter<PlaceCollectionAdapter.PlaceCollectionViewHolder> {

    private List<Place> places;

    public static class PlaceCollectionViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_place_collection;

        private TextView placeNameTextView;
        private ImageView deleteButton;

        public static PlaceCollectionViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new PlaceCollectionViewHolder(view);
        }

        private PlaceCollectionViewHolder(View v) {
            super(v);
            placeNameTextView = v.findViewById(R.id.place_name);
            deleteButton = v.findViewById(R.id.delete);
        }

        public void bindTo(Place place, boolean isPrivate) {
            deleteButton.setVisibility(isPrivate ? View.VISIBLE : View.GONE);
            placeNameTextView.setText(place.getName());
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

    public PlaceCollectionAdapter(List<Place> places, boolean isPrivate) {
        this.places = places;
        this.isPrivate = isPrivate;
        Collections.sort(this.places, (a1, a2) -> (a1.getName()).compareTo(a2.getName()));
    }

    @Override
    public void onBind(PlaceCollectionViewHolder holder, final int position) {
        holder.setOnDeleteListener(onDeleteListener);
        holder.bindTo(places.get(position), isPrivate);
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    @NonNull
    @Override
    public PlaceCollectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return PlaceCollectionViewHolder.create(parent);
    }

    public interface OnDeleteListener {
        void onDelete(int position);
    }

    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }
}
