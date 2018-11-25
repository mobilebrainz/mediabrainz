package app.mediabrainz.adapter.recycler.expandedRecycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import app.mediabrainz.R;


public abstract class BaseHeaderViewHolder extends RecyclerView.ViewHolder {

    public interface OnHeaderClickListener {
        void onClick(BaseHeader header);
    }

    protected View container;
    protected BaseHeader header;

    public BaseHeaderViewHolder(View itemView) {
        super(itemView);
        container = itemView.findViewById(R.id.container);
    }

    protected abstract void bind(BaseHeader header);
    protected abstract void expand(boolean expand);

    public void setOnHeaderClickListener(OnHeaderClickListener onHeaderClickListener) {
        itemView.setOnClickListener(v -> {
            if (header != null) {
                onHeaderClickListener.onClick(header);
            }
        });
    }

    public final void bindView(BaseHeader header) {
        this.header = header;
        setVisibility(header.isVisible());
        bind(header);
    }

    public void setVisibility(boolean visible) {
        header.setVisible(visible);
        setVisibility();
        //container.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setVisibility() {
        container.setVisibility(header.isVisible() ? View.VISIBLE : View.GONE);
    }
}
