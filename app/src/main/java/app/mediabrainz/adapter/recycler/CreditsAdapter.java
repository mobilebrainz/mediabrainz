package app.mediabrainz.adapter.recycler;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.mediabrainz.R;
import app.mediabrainz.api.core.ApiUtils;
import app.mediabrainz.api.model.relations.Relation;

import java.util.List;


public class CreditsAdapter extends BaseRecyclerViewAdapter<CreditsAdapter.CreditsViewHolder> {

    private List<Relation> artistRelations;

    public static class CreditsViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_credits;

        private TextView typeView;
        private TextView artistView;

        public static CreditsViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new CreditsViewHolder(view);
        }

        private CreditsViewHolder(View v) {
            super(v);
            typeView = v.findViewById(R.id.type);
            artistView = v.findViewById(R.id.artist);
        }

        public void bindTo(Relation relation) {
            String type = relation.getType();
            String attributes = ApiUtils.getStringFromList(relation.getAttributes(), ", ");
            typeView.setText(ApiUtils.getStringFromArray(new String[]{type, attributes}, " / "));
            artistView.setText(relation.getArtist().getName());
        }
    }

    public CreditsAdapter(List<Relation> artistRelations) {
        this.artistRelations = artistRelations;
    }

    @Override
    public void onBind(CreditsViewHolder holder, final int position) {
        holder.bindTo(artistRelations.get(position));
    }

    @Override
    public int getItemCount() {
        return artistRelations.size();
    }

    @NonNull
    @Override
    public CreditsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return CreditsViewHolder.create(parent);
    }
}
