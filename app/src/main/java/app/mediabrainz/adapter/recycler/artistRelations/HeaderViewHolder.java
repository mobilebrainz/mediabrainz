package app.mediabrainz.adapter.recycler.artistRelations;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.expandedRecycler.BaseHeader;
import app.mediabrainz.adapter.recycler.expandedRecycler.BaseHeaderViewHolder;


public class HeaderViewHolder extends BaseHeaderViewHolder {

    private ImageView expandImageView;
    private ImageView infoView;
    private TextView headerTitleView;

    public HeaderViewHolder(View itemView) {
        super(itemView);
        headerTitleView = itemView.findViewById(R.id.headerTitleView);
        expandImageView = itemView.findViewById(R.id.expandImageView);
        infoView = itemView.findViewById(R.id.infoView);
    }

    @Override
    protected void expand(boolean expand) {
        if (expand) {
            expandImageView.setImageResource(R.drawable.ic_expand_less_24);
        } else {
            expandImageView.setImageResource(R.drawable.ic_expand_more_24);
        }
    }

    @Override
    protected void bind(BaseHeader header) {
        Header h = (Header) header;
        headerTitleView.setText(h.getTitle());
        expand(h.isExpand());

        infoView.setOnClickListener(
                v -> Toast.makeText(itemView.getContext(), h.getDescription(), Toast.LENGTH_LONG).show());
    }

}
