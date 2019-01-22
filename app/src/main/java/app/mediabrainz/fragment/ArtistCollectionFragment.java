package app.mediabrainz.fragment;


import android.os.Bundle;
import android.view.View;

import app.mediabrainz.adapter.recycler.PagedArtistCollectionAdapter;
import app.mediabrainz.intent.ActivityFactory;
import app.mediabrainz.viewModels.ArtistCollectionVM;
import app.mediabrainz.viewModels.BaseCollectionVM;
import app.mediabrainz.viewModels.Status;


public class ArtistCollectionFragment extends BaseCollectionFragment {

    private ArtistCollectionVM viewModel;
    private PagedArtistCollectionAdapter adapter;

    public static ArtistCollectionFragment newInstance() {
        Bundle args = new Bundle();
        ArtistCollectionFragment fragment = new ArtistCollectionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public BaseCollectionVM initViewModel() {
        viewModel = getViewModel(ArtistCollectionVM.class);
        return viewModel;
    }

    @Override
    public void load() {
        adapter = new PagedArtistCollectionAdapter(this, isPrivate);
        adapter.setHolderClickListener(artist ->
                ActivityFactory.startArtistActivity(getContext(), artist.getId()));

        if (isPrivate) {
            adapter.setOnDeleteListener(position -> onDelete(adapter.getCurrentList().get(position)));
        }

        viewModel.load(collection.getId());
        viewModel.artistCollections.observe(this, adapter::submitList);
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
