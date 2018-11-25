package app.mediabrainz.adapter.recycler.artistRelations;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.expandedRecycler.BaseHeader;
import app.mediabrainz.adapter.recycler.expandedRecycler.BaseHeaderViewHolder;


public class HeaderViewHolder extends BaseHeaderViewHolder {

    private ImageView expandView;
    private ImageView infoView;
    private TextView title;

    public HeaderViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.header_title);
        expandView = itemView.findViewById(R.id.expand_image);
        infoView = itemView.findViewById(R.id.info);
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
        expand(h.isExpand());

        infoView.setOnClickListener(
                v -> Toast.makeText(itemView.getContext(), h.getDescription(), Toast.LENGTH_LONG).show());
    }

}
