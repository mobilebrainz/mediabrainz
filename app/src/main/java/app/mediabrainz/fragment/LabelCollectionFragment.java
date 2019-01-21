package app.mediabrainz.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.view.View;

import app.mediabrainz.adapter.recycler.PagedLabelCollectionAdapter;
import app.mediabrainz.viewModels.Status;
import app.mediabrainz.viewModels.BaseCollectionVM;
import app.mediabrainz.viewModels.LabelCollectionVM;


public class LabelCollectionFragment extends BaseCollectionFragment {

    private LabelCollectionVM viewModel;
    private PagedLabelCollectionAdapter adapter;

    public static LabelCollectionFragment newInstance() {
        Bundle args = new Bundle();
        LabelCollectionFragment fragment = new LabelCollectionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public BaseCollectionVM initViewModel() {
        viewModel = ViewModelProviders.of(this).get(LabelCollectionVM.class);
        return viewModel;
    }

    @Override
    public void load() {
        adapter = new PagedLabelCollectionAdapter(this, isPrivate);
        //adapter.setHolderClickListener(label -> ((OnLabelCommunicator) getContext()).onArea(label.getId()));

        if (isPrivate) {
            adapter.setOnDeleteListener(position -> onDelete(adapter.getCurrentList().get(position)));
        }
        viewModel.load(collection.getId());
        viewModel.labelCollections.observe(this, adapter::submitList);
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
