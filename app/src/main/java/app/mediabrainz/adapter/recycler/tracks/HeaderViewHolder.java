package app.mediabrainz.adapter.recycler.tracks;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.expandedRecycler.BaseHeader;
import app.mediabrainz.adapter.recycler.expandedRecycler.BaseHeaderViewHolder;
import app.mediabrainz.util.MbUtils;


public class HeaderViewHolder extends BaseHeaderViewHolder {

    private ImageView expandImageView;
    private TextView headerTitleView;
    private TextView detailsView;

    public HeaderViewHolder(View itemView) {
        super(itemView);
        headerTitleView = itemView.findViewById(R.id.headerTitleView);
        expandImageView = itemView.findViewById(R.id.expandImageView);
        detailsView = itemView.findViewById(R.id.detailsView);
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
        detailsView.setText(itemView.getResources().getString(
                R.string.release_tracks_details,
                MbUtils.formatTime(h.getLength()),
                h.getSize()));

        expand(h.isExpand());
    }

}
