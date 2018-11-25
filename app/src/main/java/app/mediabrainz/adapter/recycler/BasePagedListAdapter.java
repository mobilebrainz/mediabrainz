package app.mediabrainz.adapter.recycler;

import android.arch.paging.PagedListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;

import app.mediabrainz.data.NetworkState;


public abstract class BasePagedListAdapter<T> extends PagedListAdapter<T, RecyclerView.ViewHolder> {

    protected NetworkState networkState;
    protected RetryCallback retryCallback;

    public BasePagedListAdapter(DiffUtil.ItemCallback<T> diffCallback, RetryCallback retryCallback) {
        super(diffCallback);
        this.retryCallback = retryCallback;
    }

    protected boolean hasExtraRow() {
        return networkState != null && networkState != NetworkState.LOADED;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + (hasExtraRow() ? 1 : 0);
    }

    /**
     * Set the current network state to the adapter
     * but this work only after the initial getWikidata
     * and the adapter already have list to add new loading raw to it
     * so the initial loading state the activity responsible for handle it
     *
     * @param newNetworkState the new network state
     */
    public void setNetworkState(NetworkState newNetworkState) {
        if (getCurrentList() != null) {
            if (getCurrentList().size() != 0) {
                NetworkState previousState = this.networkState;
                boolean hadExtraRow = hasExtraRow();
                this.networkState = newNetworkState;
                boolean hasExtraRow = hasExtraRow();
                if (hadExtraRow != hasExtraRow) {
                    if (hadExtraRow) {
                        notifyItemRemoved(super.getItemCount());
                    } else {
                        notifyItemInserted(super.getItemCount());
                    }
                } else if (hasExtraRow && previousState != newNetworkState) {
                    notifyItemChanged(getItemCount() - 1);
                }
            }
        }
    }

}
