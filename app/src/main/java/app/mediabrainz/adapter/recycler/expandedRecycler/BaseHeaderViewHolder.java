package app.mediabrainz.adapter.recycler.expandedRecycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import app.mediabrainz.R;


public abstract class BaseHeaderViewHolder extends RecyclerView.ViewHolder {

    public interface OnHeaderClickListener {
        void onClick(BaseHeader header);
    }

    protected View containerView;
    protected BaseHeader header;

    public BaseHeaderViewHolder(View itemView) {
        super(itemView);
        containerView = itemView.findViewById(R.id.containerView);
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
        //containerView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setVisibility() {
        containerView.setVisibility(header.isVisible() ? View.VISIBLE : View.GONE);
    }
}
