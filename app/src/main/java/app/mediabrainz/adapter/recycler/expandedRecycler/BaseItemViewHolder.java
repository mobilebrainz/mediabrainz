package app.mediabrainz.adapter.recycler.expandedRecycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import app.mediabrainz.R;


public abstract class BaseItemViewHolder extends RecyclerView.ViewHolder {

    protected View container;

    public BaseItemViewHolder(View itemView, boolean visible) {
        super(itemView);
        container = itemView.findViewById(R.id.container);
        setVisibility(visible);
    }

    public void setVisibility(boolean visible) {
        container.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
