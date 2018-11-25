package app.mediabrainz.adapter.recycler.expandedRecycler;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseExpandedRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ExpandedRecyclerAdapter";

    public final static int HEADER = 0,
            ITEM_INVISIBLE = 1,
            ITEM_VISIBLE = 2;

    protected RecyclerView recyclerView;
    protected List<Section<T>> sections;
    protected List<Object> items = new ArrayList<>();
    protected List<Boolean> visibility = new ArrayList<>();

    public BaseExpandedRecyclerAdapter(List<Section<T>> sections) {
        this.sections = sections;

        for (Section<T> section : sections) {
            BaseHeader header = section.getHeader();
            header.setPosition(items.size());
            header.setSize(section.getItems().size());

            items.add(header);
            visibility.add(header.isVisible());
            for (T item : section.getItems()) {
                items.add(item);
                visibility.add(header.isVisible() ? header.isExpand() : false);
            }
        }
        items.add(null);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof BaseHeader) {
            return HEADER;
        } else if (items.get(position) != null) {
            return visibility.get(position) ? ITEM_VISIBLE : ITEM_INVISIBLE;
        } else {
            return -1;
        }
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder.getItemViewType() == HEADER) {
            ((BaseHeaderViewHolder) viewHolder).setOnHeaderClickListener(header -> expand(header, !header.isExpand()));
        }
        onBindHolder(viewHolder, position);
    }

    public final void expand(int sectionPosition, boolean expand) {
        expand(sections.get(sectionPosition).getHeader(), expand);
    }

    public final void expand(BaseHeader header, boolean expand) {
        if (header.isVisible() || !expand) {
            int pos = header.getPosition();

            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(pos);
            if (viewHolder instanceof BaseHeaderViewHolder) {
                ((BaseHeaderViewHolder) viewHolder).expand(expand);
            }
            for (int i = pos + 1; i <= pos + header.getSize(); ++i) {
                viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
                visibility.set(i, expand);
                if (viewHolder != null && (viewHolder instanceof BaseItemViewHolder)) {
                    ((BaseItemViewHolder) viewHolder).setVisibility(expand);
                }
            }
            header.setExpand(expand);
        }
    }

    public final void hide(int sectionPosition, boolean hide) {
        hide(sections.get(sectionPosition).getHeader(), hide);
    }

    public final void hide(BaseHeader header, boolean hide) {
        hide(header, hide, false);
    }

    public final void hide(BaseHeader header, boolean hide, boolean expand) {
        if (!expand) {
            expand(header, false);
        }
        int pos = header.getPosition();
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(pos);
        if (viewHolder instanceof BaseHeaderViewHolder) {
            ((BaseHeaderViewHolder) viewHolder).setVisibility(!hide);
            if (expand) {
                expand(header, !hide);
            }
        }
    }

    public final void expandAll(boolean expand) {
        for (Section<T> section : sections) {
            expand(section.getHeader(), expand);
        }
    }

    public abstract void onBindHolder(RecyclerView.ViewHolder viewHolder, int position);

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        switch (holder.getItemViewType()) {
            case HEADER:
                ((BaseHeaderViewHolder) holder).setVisibility();
                break;
            case ITEM_VISIBLE:
                ((BaseItemViewHolder) holder).setVisibility(true);
                break;
            case ITEM_INVISIBLE:
                ((BaseItemViewHolder) holder).setVisibility(false);
        }
    }

    public List<Section<T>> getSections() {
        return sections;
    }

}
