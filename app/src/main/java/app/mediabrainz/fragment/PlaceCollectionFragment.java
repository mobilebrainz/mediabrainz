package app.mediabrainz.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.view.View;

import app.mediabrainz.adapter.recycler.PagedPlaceCollectionAdapter;
import app.mediabrainz.data.Status;
import app.mediabrainz.ui.PlaceCollectionViewModel;


public class PlaceCollectionFragment extends BaseCollectionFragment {

    private PlaceCollectionViewModel viewModel;
    private PagedPlaceCollectionAdapter adapter;

    public static PlaceCollectionFragment newInstance() {
        Bundle args = new Bundle();
        PlaceCollectionFragment fragment = new PlaceCollectionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void load() {
        error.setVisibility(View.GONE);

        if (collection != null) {
            adapter = new PagedPlaceCollectionAdapter(this, isPrivate);
            //adapter.setHolderClickListener(area -> ((OnPlaceCommunicator) getContext()).onPlace(place.getId()));

            if (isPrivate) {
                adapter.setOnDeleteListener(position -> onDelete(adapter.getCurrentList().get(position), null));
            }

            viewModel = ViewModelProviders.of(this).get(PlaceCollectionViewModel.class);
            viewModel.load(collection.getId());
            viewModel.placeCollectionLiveData.observe(this, adapter::submitList);
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
