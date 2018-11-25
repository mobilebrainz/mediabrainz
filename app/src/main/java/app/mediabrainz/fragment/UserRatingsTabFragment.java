package app.mediabrainz.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import app.mediabrainz.adapter.recycler.ArtistRatingsAdapter;
import app.mediabrainz.adapter.recycler.BasePagedListAdapter;
import app.mediabrainz.adapter.recycler.RecordingRatingsAdapter;
import app.mediabrainz.adapter.recycler.ReleaseGroupRatingsAdapter;
import app.mediabrainz.adapter.recycler.RetryCallback;
import app.mediabrainz.api.site.Rating;
import app.mediabrainz.api.site.RatingServiceInterface;
import app.mediabrainz.communicator.GetUsernameCommunicator;
import app.mediabrainz.communicator.OnArtistCommunicator;
import app.mediabrainz.communicator.OnRecordingCommunicator;
import app.mediabrainz.communicator.OnReleaseGroupCommunicator;
import app.mediabrainz.data.Status;
import app.mediabrainz.ui.RatingsViewModel;


public class UserRatingsTabFragment extends Fragment implements RetryCallback {

    private static final String RATINGS_TAB = "RATINGS_TAB";

    private RatingServiceInterface.RatingType ratingType;
    private RatingsViewModel ratingsViewModel;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView pagedRecycler;
    private BasePagedListAdapter<Rating> adapter;

    private TextView errorMessageTextView;
    private Button retryLoadingButton;
    private ProgressBar loadingProgressBar;
    private View itemNetworkState;

    public static UserRatingsTabFragment newInstance(int ratingsTab) {
        Bundle args = new Bundle();
        args.putInt(RATINGS_TAB, ratingsTab);
        UserRatingsTabFragment fragment = new UserRatingsTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_paged_recycler, container, false);

        ratingType = RatingServiceInterface.RatingType.values()[getArguments().getInt(RATINGS_TAB)];

        pagedRecycler = layout.findViewById(R.id.paged_recycler);
        swipeRefreshLayout = layout.findViewById(R.id.swipe_refresh_layout);
        errorMessageTextView = layout.findViewById(R.id.errorMessageTextView);
        loadingProgressBar = layout.findViewById(R.id.loadingProgressBar);
        itemNetworkState = layout.findViewById(R.id.item_network_state);

        retryLoadingButton = layout.findViewById(R.id.retryLoadingButton);
        retryLoadingButton.setOnClickListener(view -> retry());

        load();
        return layout;
    }

    private void load() {
        String username = ((GetUsernameCommunicator) getContext()).getUsername();
        if (username != null) {
            switch (ratingType) {
                case ARTIST:
                    ArtistRatingsAdapter artistRatingsAdapter = new ArtistRatingsAdapter(this);
                    artistRatingsAdapter.setHolderClickListener(rating -> ((OnArtistCommunicator) getContext()).onArtist(rating.getMbid()));
                    adapter = artistRatingsAdapter;
                    break;

                case RELEASE_GROUP:
                    ReleaseGroupRatingsAdapter releaseGroupRatingsAdapter = new ReleaseGroupRatingsAdapter(this);
                    releaseGroupRatingsAdapter.setHolderClickListener(rating -> ((OnReleaseGroupCommunicator) getContext()).onReleaseGroup(rating.getMbid()));
                    adapter = releaseGroupRatingsAdapter;
                    break;

                case RECORDING:
                    RecordingRatingsAdapter recordingRatingsAdapter = new RecordingRatingsAdapter(this);
                    recordingRatingsAdapter.setHolderClickListener(rating -> ((OnRecordingCommunicator) getContext()).onRecording(rating.getMbid()));
                    adapter = recordingRatingsAdapter;
                    break;
            }

            ratingsViewModel = ViewModelProviders.of(this).get(RatingsViewModel.class);
            ratingsViewModel.load(ratingType, username);
            ratingsViewModel.ratingsLiveData.observe(this, adapter::submitList);
            ratingsViewModel.getNetworkState().observe(this, adapter::setNetworkState);

            pagedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            pagedRecycler.setNestedScrollingEnabled(true);
            pagedRecycler.setHasFixedSize(true);
            pagedRecycler.setAdapter(adapter);

            initSwipeToRefresh();
        }
    }

    private void initSwipeToRefresh() {
        ratingsViewModel.getRefreshState().observe(this, networkState -> {
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
            ratingsViewModel.refresh();
            swipeRefreshLayout.setRefreshing(false);
            pagedRecycler.scrollToPosition(0);
        });
    }

    @Override
    public void retry() {
        if (ratingsViewModel != null) {
            ratingsViewModel.retry();
        }
    }

}
