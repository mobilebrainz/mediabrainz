package app.mediabrainz.adapter.recycler.tracks;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.expandedRecycler.BaseExpandedRecyclerAdapter;
import app.mediabrainz.adapter.recycler.expandedRecycler.Section;
import app.mediabrainz.api.model.Media;


public class TracksAdapter extends BaseExpandedRecyclerAdapter<Media.Track> {

    public static final String TAG = "TracksAdapter";

    private ItemViewHolder.OnItemClickListener onItemClickListener;
    private ItemViewHolder.OnPlayYoutubeListener onPlayYoutubeListener;

    public TracksAdapter(List<Section<Media.Track>> sections) {
        super(sections);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch (viewType) {
            case ITEM_INVISIBLE:
                return new ItemViewHolder(inflater.inflate(R.layout.item_tracks, viewGroup, false), false);
            case ITEM_VISIBLE:
                return new ItemViewHolder(inflater.inflate(R.layout.item_tracks, viewGroup, false), true);
            case HEADER:
                return new HeaderViewHolder(inflater.inflate(R.layout.header_tracks, viewGroup, false));
            default:
                return new EmptyFooterViewHolder(inflater.inflate(R.layout.footer_recycler_empty, viewGroup, false));
        }
    }

    @Override
    public void onBindHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case ITEM_INVISIBLE:
            case ITEM_VISIBLE:
                ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
                itemViewHolder.setOnItemClickListener(onItemClickListener);
                itemViewHolder.setOnPlayYoutubeListener(onPlayYoutubeListener);
                itemViewHolder.bindView((Media.Track) items.get(position));
                break;
            case HEADER:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
                headerViewHolder.bindView((Header) items.get(position));
                break;
        }
    }

    public void setOnItemClickListener(ItemViewHolder.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnPlayYoutubeListener(ItemViewHolder.OnPlayYoutubeListener onPlayYoutubeListener) {
        this.onPlayYoutubeListener = onPlayYoutubeListener;
    }
}
