package app.mediabrainz.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.PagedReleaseAdapter;
import app.mediabrainz.adapter.recycler.RetryCallback;
import app.mediabrainz.api.browse.ReleaseBrowseService;
import app.mediabrainz.api.model.Release;
import app.mediabrainz.communicator.GetRecordingCommunicator;
import app.mediabrainz.communicator.GetReleaseCommunicator;
import app.mediabrainz.communicator.OnReleaseCommunicator;
import app.mediabrainz.viewModels.Status;
import app.mediabrainz.viewModels.ReleasesVM;


public class ReleasesFragment extends LazyFragment implements RetryCallback {

    private int type = 1;
    private ReleasesVM releasesVM;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView pagedRecyclerView;
    private PagedReleaseAdapter adapter;

    private TextView errorMessageTextView;
    private Button retryLoadingButton;
    private ProgressBar loadingProgressBar;
    private View itemNetworkStateView;

    private static final String TYPE = "TYPE";
    public static final int ALBUM_TYPE = 1;
    public static final int RECORDING_TYPE = 2;

    public static ReleasesFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt(TYPE, type);
        ReleasesFragment fragment = new ReleasesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_paged_recycler, container, false);

        type = getArguments().getInt(TYPE);

        pagedRecyclerView = layout.findViewById(R.id.pagedRecyclerView);
        swipeRefreshLayout = layout.findViewById(R.id.swipeRefreshLayout);
        errorMessageTextView = layout.findViewById(R.id.errorMessageTextView);
        loadingProgressBar = layout.findViewById(R.id.loadingProgressBar);
        itemNetworkStateView = layout.findViewById(R.id.itemNetworkStateView);

        retryLoadingButton = layout.findViewById(R.id.retryLoadingButton);
        retryLoadingButton.setOnClickListener(view -> retry());

        loadView();
        return layout;
    }

    @Override
    public void lazyLoad() {
        String mbid = null;
        String releaseId = null;
        ReleaseBrowseService.ReleaseBrowseEntityType entityType = null;
        switch (type) {
            case ALBUM_TYPE:
                Release release = ((GetReleaseCommunicator) getContext()).getRelease();
                if (release != null && release.getReleaseGroup() != null) {
                    releaseId = release.getId();
                    mbid = release.getReleaseGroup().getId();
                    entityType = ReleaseBrowseService.ReleaseBrowseEntityType.RELEASE_GROUP;
                }
                break;

            case RECORDING_TYPE:
                mbid = ((GetRecordingCommunicator) getContext()).getRecordingMbid();
                if (mbid != null) {
                    entityType = ReleaseBrowseService.ReleaseBrowseEntityType.RECORDING;
                }
                break;
        }

        if (mbid != null) {
            adapter = new PagedReleaseAdapter(this, releaseId);
            adapter.setHolderClickListener(r -> ((OnReleaseCommunicator) getContext()).onRelease(r.getId()));

            releasesVM = ViewModelProviders.of(this).get(ReleasesVM.class);
            releasesVM.load(mbid, entityType);
            releasesVM.realesesLiveData.observe(this, adapter::submitList);
            releasesVM.getNetworkState().observe(this, adapter::setNetworkState);

            pagedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            pagedRecyclerView.setNestedScrollingEnabled(true);
            pagedRecyclerView.setItemViewCacheSize(100);
            pagedRecyclerView.setHasFixedSize(true);
            pagedRecyclerView.setAdapter(adapter);

            initSwipeToRefresh();
        }
    }

    private void initSwipeToRefresh() {
        releasesVM.getRefreshState().observe(this, networkState -> {
            if (networkState != null) {

                //Show the current network state for the first getWikidata when the rating list
                //in the adapter is empty and disable swipe to scroll at the first loading
                if (adapter.getCurrentList() == null || adapter.getCurrentList().size() == 0) {
                    itemNetworkStateView.setVisibility(View.VISIBLE);
                    //errorView message
                    errorMessageTextView.setVisibility(networkState.getMessage() != null ? View.VISIBLE : View.GONE);
                    if (networkState.getMessage() != null) {
                        errorMessageTextView.setText(networkState.getMessage());
                    }
                    //loading and retry
                    retryLoadingButton.setVisibility(networkState.getStatus() == Status.ERROR ? View.VISIBLE : View.GONE);
                    loadingProgressBar.setVisibility(networkState.getStatus() == Status.LOADING ? View.VISIBLE : View.GONE);

                    swipeRefreshLayout.setEnabled(networkState.getStatus() == Status.SUCCESS);
                    pagedRecyclerView.scrollToPosition(0);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            releasesVM.refresh();
            swipeRefreshLayout.setRefreshing(false);
            pagedRecyclerView.scrollToPosition(0);
        });
    }

    @Override
    public void retry() {
        if (releasesVM != null) {
            releasesVM.retry();
        }
    }

}
