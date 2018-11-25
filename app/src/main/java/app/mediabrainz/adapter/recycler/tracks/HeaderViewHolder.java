package app.mediabrainz.adapter.recycler.tracks;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.expandedRecycler.BaseHeader;
import app.mediabrainz.adapter.recycler.expandedRecycler.BaseHeaderViewHolder;
import app.mediabrainz.util.MbUtils;


public class HeaderViewHolder extends BaseHeaderViewHolder {

    private ImageView expandView;
    private TextView title;
    private TextView details;

    public HeaderViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.header_title);
        expandView = itemView.findViewById(R.id.expand_image);
        details = itemView.findViewById(R.id.details);
    }

    @Override
    protected void expand(boolean expand) {
        if (expand) {
            expandView.setImageResource(R.drawable.ic_expand_less_24);
        } else {
            expandView.setImageResource(R.drawable.ic_expand_more_24);
        }
    }

    @Override
    protected void bind(BaseHeader header) {
        Header h = (Header) header;
        title.setText(h.getTitle());
        details.setText(itemView.getResources().getString(
                R.string.release_tracks_details,
                MbUtils.formatTime(h.getLength()),
                h.getSize()));

        expand(h.isExpand());
    }

}
