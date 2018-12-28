package app.mediabrainz.adapter.recycler;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import app.mediabrainz.R;
import app.mediabrainz.api.site.TagEntity;


public class EntityTagAdapter extends BaseRecyclerViewAdapter<EntityTagAdapter.EntityTagViewHolder> {

    private List<TagEntity> tags;
    private EntityTagViewHolder.OnPlayYoutubeListener onPlayYoutubeListener;

    public static class EntityTagViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        public interface OnPlayYoutubeListener {
            void onPlay(String keyword);
        }

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_entity_tag;

        private TextView entityNameView;
        private TextView artistNameView;
        private ImageView playYoutubeView;
        private OnPlayYoutubeListener onPlayYoutubeListener;

        public static EntityTagViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new EntityTagViewHolder(view);
        }

        private EntityTagViewHolder(View v) {
            super(v);
            entityNameView = v.findViewById(R.id.entityNameView);
            artistNameView = v.findViewById(R.id.artistNameView);
            playYoutubeView = itemView.findViewById(R.id.playYoutubeView);
        }

        public void bindTo(TagEntity tag) {
            entityNameView.setText(tag.getName());
            artistNameView.setText(tag.getArtistName());

            if (onPlayYoutubeListener != null) {
                playYoutubeView.setVisibility(View.VISIBLE);
                playYoutubeView.setOnClickListener(v ->
                    onPlayYoutubeListener.onPlay(tag.getArtistName() + " - " + tag.getName()));
            }
        }

        public void setOnPlayYoutubeListener(OnPlayYoutubeListener onPlayYoutubeListener) {
            this.onPlayYoutubeListener = onPlayYoutubeListener;
        }
    }

    public EntityTagAdapter(List<TagEntity> tags) {
        this.tags = tags;
    }

    @Override
    public void onBind(EntityTagViewHolder holder, final int position) {
        holder.setOnPlayYoutubeListener(onPlayYoutubeListener);
        holder.bindTo(tags.get(position));
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    @NonNull
    @Override
    public EntityTagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return EntityTagViewHolder.create(parent);
    }

    public void setOnPlayYoutubeListener(EntityTagViewHolder.OnPlayYoutubeListener onPlayYoutubeListener) {
        this.onPlayYoutubeListener = onPlayYoutubeListener;
    }
}
