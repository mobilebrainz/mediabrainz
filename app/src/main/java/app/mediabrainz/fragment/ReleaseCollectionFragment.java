package app.mediabrainz.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.view.View;

import app.mediabrainz.adapter.recycler.PagedReleaseCollectionAdapter;
import app.mediabrainz.communicator.OnReleaseCommunicator;
import app.mediabrainz.viewModels.Status;
import app.mediabrainz.viewModels.BaseCollectionVM;
import app.mediabrainz.viewModels.ReleaseCollectionVM;


public class ReleaseCollectionFragment extends BaseCollectionFragment {

    private ReleaseCollectionVM viewModel;
    private PagedReleaseCollectionAdapter adapter;

    public static ReleaseCollectionFragment newInstance() {
        Bundle args = new Bundle();
        ReleaseCollectionFragment fragment = new ReleaseCollectionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public BaseCollectionVM initViewModel() {
        viewModel = ViewModelProviders.of(this).get(ReleaseCollectionVM.class);
        return viewModel;
    }

    @Override
    public void load() {
        adapter = new PagedReleaseCollectionAdapter(this, isPrivate);
        adapter.setHolderClickListener(release ->
                ((OnReleaseCommunicator) getContext()).onRelease(release.getId()));

        if (isPrivate) {
            adapter.setOnDeleteListener(position -> onDelete(adapter.getCurrentList().get(position)));
        }
        viewModel.load(collection.getId());
        viewModel.releaseCollections.observe(this, adapter::submitList);
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
