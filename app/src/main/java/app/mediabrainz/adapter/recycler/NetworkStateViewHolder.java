package app.mediabrainz.adapter.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import app.mediabrainz.R;
import app.mediabrainz.data.NetworkState;
import app.mediabrainz.data.Status;


public class NetworkStateViewHolder extends RecyclerView.ViewHolder {

    static final int VIEW_HOLDER_LAYOUT = R.layout.item_network_state;

    private TextView errorMessageTextView;
    private Button retryLoadingButton;
    private ProgressBar loadingProgressBar;

    private NetworkStateViewHolder(View itemView, RetryCallback retryCallback) {
        super(itemView);
        errorMessageTextView = itemView.findViewById(R.id.errorMessageTextView);
        loadingProgressBar = itemView.findViewById(R.id.loadingProgressBar);
        retryLoadingButton = itemView.findViewById(R.id.retryLoadingButton);
        retryLoadingButton.setOnClickListener(v -> retryCallback.retry());
    }

    public void bindTo(NetworkState networkState) {
        //error message
        errorMessageTextView.setVisibility(networkState.getMessage() != null ? View.VISIBLE : View.GONE);
        if (networkState.getMessage() != null) {
            errorMessageTextView.setText(networkState.getMessage());
        }

        //loading and retry
        retryLoadingButton.setVisibility(networkState.getStatus() == Status.FAILED ? View.VISIBLE : View.GONE);
        loadingProgressBar.setVisibility(networkState.getStatus() == Status.RUNNING ? View.VISIBLE : View.GONE);
    }

    public static NetworkStateViewHolder create(ViewGroup parent, RetryCallback retryCallback) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
        return new NetworkStateViewHolder(view, retryCallback);
    }

}
