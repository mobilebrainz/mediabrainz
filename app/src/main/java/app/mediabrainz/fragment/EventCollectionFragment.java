package app.mediabrainz.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.view.View;

import app.mediabrainz.adapter.recycler.PagedEventCollectionAdapter;
import app.mediabrainz.viewModels.Status;
import app.mediabrainz.viewModels.BaseCollectionVM;
import app.mediabrainz.viewModels.EventCollectionVM;


public class EventCollectionFragment extends BaseCollectionFragment {

    private EventCollectionVM viewModel;
    private PagedEventCollectionAdapter adapter;

    public static EventCollectionFragment newInstance() {
        Bundle args = new Bundle();
        EventCollectionFragment fragment = new EventCollectionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public BaseCollectionVM initViewModel() {
        viewModel = getViewModel(EventCollectionVM.class);
        return viewModel;
    }

    @Override
    public void load() {
        adapter = new PagedEventCollectionAdapter(this, isPrivate);
        //adapter.setHolderClickListener(event -> ((OnEventCommunicator) getContext()).onArea(event.getId()));

        if (isPrivate) {
            adapter.setOnDeleteListener(position -> onDelete(adapter.getCurrentList().get(position)));
        }

        viewModel.load(collection.getId());
        viewModel.eventCollections.observe(this, adapter::submitList);
        viewModel.getNetworkState().observe(this, adapter::setNetworkState);

        pagedRecyclerView.setAdapter(adapter);

        initSwipeToRefresh();
    }

    private void initSwipeToRefresh() {
        viewModel.getRefreshState().observe(this, networkState -> {
            if (networkState != null) {
                if (adapter.getCurrentList() == null || adapter.getCurrentList().size() == 0) {
                    itemNetworkStateView.setVisibility(View.VISIBLE);

                    errorMessageTextView.setVisibility(networkState.getMessage() != null ? View.VISIBLE : View.GONE);
                    if (networkState.getMessage() != null) {
                        errorMessageTextView.setText(networkState.getMessage());
                    }

                    retryLoadingButton.setVisibility(networkState.getStatus() == Status.ERROR ? View.VISIBLE : View.GONE);
                    loadingProgressBar.setVisibility(networkState.getStatus() == Status.LOADING ? View.VISIBLE : View.GONE);

                    swipeRefreshLayout.setEnabled(networkState.getStatus() == Status.SUCCESS);
                    pagedRecyclerView.scrollToPosition(0);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.refresh();
            swipeRefreshLayout.setRefreshing(false);
            pagedRecyclerView.scrollToPosition(0);
        });
    }

    @Override
    public void retry() {
        if (viewModel != null) {
            viewModel.retry();
        }
    }

}
