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
import app.mediabrainz.data.Status;
import app.mediabrainz.ui.ReleasesViewModel;


public class ReleasesFragment extends LazyFragment implements RetryCallback {

    private int type = 1;
    private ReleasesViewModel releasesViewModel;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView pagedRecycler;
    private PagedReleaseAdapter adapter;

    private TextView errorMessageTextView;
    private Button retryLoadingButton;
    private ProgressBar loadingProgressBar;
    private View itemNetworkState;

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

        pagedRecycler = layout.findViewById(R.id.paged_recycler);
        swipeRefreshLayout = layout.findViewById(R.id.swipe_refresh_layout);
        errorMessageTextView = layout.findViewById(R.id.errorMessageTextView);
        loadingProgressBar = layout.findViewById(R.id.loadingProgressBar);
        itemNetworkState = layout.findViewById(R.id.item_network_state);

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

            releasesViewModel = ViewModelProviders.of(this).get(ReleasesViewModel.class);
            releasesViewModel.load(mbid, entityType);
            releasesViewModel.realeseLiveData.observe(this, adapter::submitList);
            releasesViewModel.getNetworkState().observe(this, adapter::setNetworkState);

            pagedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            pagedRecycler.setNestedScrollingEnabled(true);
            pagedRecycler.setItemViewCacheSize(100);
            pagedRecycler.setHasFixedSize(true);
            pagedRecycler.setAdapter(adapter);

            initSwipeToRefresh();
        }
    }

    private void initSwipeToRefresh() {
        releasesViewModel.getRefreshState().observe(this, networkState -> {
            if (networkState != null) {

                //Show the current network state for the first getWikidata when the rating list
                //in the adapter is empty and disable swipe to scroll at the first loading
                if (adapter.getCurrentList() == null || adapter.getCurrentList().size() == 0) {
                    itemNetworkState.setVisibility(View.VISIBLE);
                    //error message
                    errorMessageTextView.setVisibility(networkState.getMessage() != null ? View.VISIBLE : View.GONE);
                    if (networkState.getMessage() != null) {
                        errorMessageTextView.setText(networkState.getMessage());
                    }
                    //loading and retry
                    retryLoadingButton.setVisibility(networkState.getStatus() == Status.FAILED ? View.VISIBLE : View.GONE);
                    loadingProgressBar.setVisibility(networkState.getStatus() == Status.RUNNING ? View.VISIBLE : View.GONE);

                    swipeRefreshLayout.setEnabled(networkState.getStatus() == Status.SUCCESS);
                    pagedRecycler.scrollToPosition(0);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            releasesViewModel.refresh();
            swipeRefreshLayout.setRefreshing(false);
            pagedRecycler.scrollToPosition(0);
        });
    }

    @Override
    public void retry() {
        if (releasesViewModel != null) {
            releasesViewModel.retry();
        }
    }

}
