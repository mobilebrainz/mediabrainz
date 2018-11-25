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
import app.mediabrainz.api.model.Event;


public class EventCollectionAdapter extends BaseRecyclerViewAdapter<EventCollectionAdapter.EventCollectionViewHolder> {

    private List<Event> events;

    public static class EventCollectionViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_event_collection;

        private TextView eventNameTextView;
        private ImageView deleteButton;

        public static EventCollectionViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new EventCollectionViewHolder(view);
        }

        private EventCollectionViewHolder(View v) {
            super(v);
            eventNameTextView = v.findViewById(R.id.event_name);
            deleteButton = v.findViewById(R.id.delete);
        }

        public void bindTo(Event event, boolean isPrivate) {
            deleteButton.setVisibility(isPrivate ? View.VISIBLE : View.GONE);
            eventNameTextView.setText(event.getName());
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

    public EventCollectionAdapter(List<Event> events, boolean isPrivate) {
        this.events = events;
        this.isPrivate = isPrivate;
        Collections.sort(this.events, (a1, a2) -> (a1.getName()).compareTo(a2.getName()));
    }

    @Override
    public void onBind(EventCollectionViewHolder holder, final int position) {
        holder.setOnDeleteListener(onDeleteListener);
        holder.bindTo(events.get(position), isPrivate);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    @NonNull
    @Override
    public EventCollectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return EventCollectionViewHolder.create(parent);
    }

    public interface OnDeleteListener {
        void onDelete(int position);
    }

    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }
}
