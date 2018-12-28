package app.mediabrainz.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.view.View;

import app.mediabrainz.adapter.recycler.PagedReleaseGroupCollectionAdapter;
import app.mediabrainz.communicator.OnReleaseGroupCommunicator;
import app.mediabrainz.data.Status;
import app.mediabrainz.ui.ReleaseGroupCollectionViewModel;


public class ReleaseGroupCollectionFragment extends BaseCollectionFragment {

    private ReleaseGroupCollectionViewModel viewModel;
    private PagedReleaseGroupCollectionAdapter adapter;

    public static ReleaseGroupCollectionFragment newInstance() {
        Bundle args = new Bundle();
        ReleaseGroupCollectionFragment fragment = new ReleaseGroupCollectionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void load() {
        errorView.setVisibility(View.GONE);

        if (collection != null) {
            adapter = new PagedReleaseGroupCollectionAdapter(this, isPrivate);
            adapter.setHolderClickListener(releaseGroup ->
                    ((OnReleaseGroupCommunicator) getContext()).onReleaseGroup(releaseGroup.getId()));

            if (isPrivate) {
                adapter.setOnDeleteListener(position -> onDelete(adapter.getCurrentList().get(position), null));
            }

            viewModel = ViewModelProviders.of(this).get(ReleaseGroupCollectionViewModel.class);
            viewModel.load(collection.getId());
            viewModel.rgCollectionLiveData.observe(this, adapter::submitList);
            viewModel.getNetworkState().observe(this, adapter::setNetworkState);

            pagedRecyclerView.setAdapter(adapter);

            initSwipeToRefresh();
        }
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

                    retryLoadingButton.setVisibility(networkState.getStatus() == Status.FAILED ? View.VISIBLE : View.GONE);
                    loadingProgressBar.setVisibility(networkState.getStatus() == Status.RUNNING ? View.VISIBLE : View.GONE);

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
