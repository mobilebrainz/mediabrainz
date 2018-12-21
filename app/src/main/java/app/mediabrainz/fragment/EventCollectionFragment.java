package app.mediabrainz.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.view.View;

import app.mediabrainz.adapter.recycler.PagedEventCollectionAdapter;
import app.mediabrainz.data.Status;
import app.mediabrainz.ui.EventCollectionViewModel;


public class EventCollectionFragment extends BaseCollectionFragment {

    private EventCollectionViewModel viewModel;
    private PagedEventCollectionAdapter adapter;

    public static EventCollectionFragment newInstance() {
        Bundle args = new Bundle();
        EventCollectionFragment fragment = new EventCollectionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void load() {
        error.setVisibility(View.GONE);

        if (collection != null) {
            adapter = new PagedEventCollectionAdapter(this, isPrivate);
            //adapter.setHolderClickListener(event -> ((OnEventCommunicator) getContext()).onArea(event.getId()));

            if (isPrivate) {
                adapter.setOnDeleteListener(position -> onDelete(adapter.getCurrentList().get(position), null));
            }

            viewModel = ViewModelProviders.of(this).get(EventCollectionViewModel.class);
            viewModel.load(collection.getId());
            viewModel.eventCollectionLiveData.observe(this, adapter::submitList);
            viewModel.getNetworkState().observe(this, adapter::setNetworkState);

            pagedRecycler.setAdapter(adapter);

            initSwipeToRefresh();
        }
    }

    private void initSwipeToRefresh() {
        viewModel.getRefreshState().observe(this, networkState -> {
            if (networkState != null) {
                if (adapter.getCurrentList() == null || adapter.getCurrentList().size() == 0) {
                    itemNetworkState.setVisibility(View.VISIBLE);

                    errorMessageTextView.setVisibility(networkState.getMessage() != null ? View.VISIBLE : View.GONE);
                    if (networkState.getMessage() != null) {
                        errorMessageTextView.setText(networkState.getMessage());
                    }

                    retryLoadingButton.setVisibility(networkState.getStatus() == Status.FAILED ? View.VISIBLE : View.GONE);
                    loadingProgressBar.setVisibility(networkState.getStatus() == Status.RUNNING ? View.VISIBLE : View.GONE);

                    swipeRefreshLayout.setEnabled(networkState.getStatus() == Status.SUCCESS);
                    pagedRecycler.scrollToPosition(0);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.refresh();
            swipeRefreshLayout.setRefreshing(false);
            pagedRecycler.scrollToPosition(0);
        });
    }

    @Override
    public void retry() {
        if (viewModel != null) {
            viewModel.retry();
        }
    }

}
