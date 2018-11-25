package app.mediabrainz.adapter.recycler;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.mediabrainz.R;
import app.mediabrainz.api.site.TagEntity;

import java.util.List;


public class EntityTagAdapter extends BaseRecyclerViewAdapter<EntityTagAdapter.EntityTagViewHolder> {

    private List<TagEntity> tags;

    public static class EntityTagViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_entity_tag;

        private TextView entityNameView;
        private TextView artistNameView;

        public static EntityTagViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new EntityTagViewHolder(view);
        }

        private EntityTagViewHolder(View v) {
            super(v);
            entityNameView = v.findViewById(R.id.entity_name);
            artistNameView = v.findViewById(R.id.artist_name);
        }

        public void bindTo(TagEntity tag) {
            entityNameView.setText(tag.getName());
            artistNameView.setText(tag.getArtistName());
        }
    }

    public EntityTagAdapter(List<TagEntity> tags) {
        this.tags = tags;
    }

    @Override
    public void onBind(EntityTagViewHolder holder, final int position) {
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
}
