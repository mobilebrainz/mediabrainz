package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.RetryCallback;
import app.mediabrainz.api.model.BaseLookupEntity;
import app.mediabrainz.api.model.Collection;
import app.mediabrainz.communicator.GetCollectionCommunicator;
import app.mediabrainz.communicator.GetUsernameCommunicator;
import app.mediabrainz.communicator.ShowFloatingActionButtonCommunicator;
import app.mediabrainz.communicator.ShowTitleCommunicator;
import app.mediabrainz.functions.Action;
import app.mediabrainz.util.ShowUtil;

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.MediaBrainzApp.oauth;


public abstract class BaseCollectionFragment extends Fragment implements RetryCallback {

    public interface OnChangeCollection {
        void changeCollection();
    }

    protected boolean isPrivate;
    protected Collection collection;

    protected SwipeRefreshLayout swipeRefreshLayout;
    protected RecyclerView pagedRecyclerView;
    protected TextView errorMessageTextView;
    protected Button retryLoadingButton;
    protected ProgressBar loadingProgressBar;
    protected View itemNetworkStateView;

    protected View errorView;
    protected View progressView;

    public abstract void load();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_paged_recycler, container, false);

        String username = ((GetUsernameCommunicator) getContext()).getUsername();
        isPrivate = oauth.hasAccount() && username.equals(oauth.getName());
        collection = ((GetCollectionCommunicator) getContext()).getCollection();
        ((ShowTitleCommunicator) getContext()).getToolbarTopTitleView().setText(collection.getName());

        errorView = layout.findViewById(R.id.errorView);
        progressView = layout.findViewById(R.id.progressView);

        swipeRefreshLayout = layout.findViewById(R.id.swipeRefreshLayout);
        errorMessageTextView = layout.findViewById(R.id.errorMessageTextView);
        loadingProgressBar = layout.findViewById(R.id.loadingProgressBar);
        itemNetworkStateView = layout.findViewById(R.id.itemNetworkStateView);

        pagedRecyclerView = layout.findViewById(R.id.pagedRecyclerView);
        pagedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pagedRecyclerView.setNestedScrollingEnabled(true);
        pagedRecyclerView.setHasFixedSize(true);
        pagedRecyclerView.setItemViewCacheSize(100);

        retryLoadingButton = layout.findViewById(R.id.retryLoadingButton);
        retryLoadingButton.setOnClickListener(view -> retry());

        if (collection != null && isPrivate) {
            ((ShowFloatingActionButtonCommunicator) getContext()).showFloatingActionButton(true, ShowFloatingActionButtonCommunicator.FloatingButtonType.EDIT_COLLECTION);
        }
        load();
        return layout;
    }

    public void onDelete(BaseLookupEntity entity, Action action) {
        View titleView = getLayoutInflater().inflate(R.layout.layout_custom_alert_dialog_title, null);
        TextView titleTextView = titleView.findViewById(R.id.titleTextView);
        titleTextView.setText(R.string.collection_delete_entity);

        new AlertDialog.Builder(getContext())
                .setCustomTitle(titleView)
                .setMessage(getString(R.string.delete_alert_message))
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    progressView.setVisibility(View.VISIBLE);
                    if (!api.deleteEntityFromCollection(
                            collection, entity,
                            metadata -> {
                                progressView.setVisibility(View.GONE);
                                if (metadata.getMessage().getText().equals("OK")) {
                                    if (action != null) {
                                        action.run();
                                    } else {
                                        load();
                                    }
                                    ((OnChangeCollection) getContext()).changeCollection();
                                } else {
                                    ShowUtil.showMessage(getActivity(), "Error");
                                }
                            },
                            this::showConnectionWarning)) {
                        progressView.setVisibility(View.GONE);
                    }
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.cancel())
                .show();
    }

    private void showConnectionWarning(Throwable t) {
        progressView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        errorView.findViewById(R.id.retryButton).setOnClickListener(v -> load());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isPrivate) {
            ((ShowFloatingActionButtonCommunicator) getContext()).showFloatingActionButton(false, ShowFloatingActionButtonCommunicator.FloatingButtonType.EDIT_COLLECTION);
        }
    }
}
