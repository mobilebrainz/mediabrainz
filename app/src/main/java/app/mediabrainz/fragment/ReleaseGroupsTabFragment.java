package app.mediabrainz.fragment;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import app.mediabrainz.R;
import app.mediabrainz.adapter.pager.ReleaseGroupsPagerAdapter;
import app.mediabrainz.adapter.recycler.ReleaseGroupsAdapter;
import app.mediabrainz.adapter.recycler.RetryCallback;
import app.mediabrainz.communicator.GetArtistCommunicator;
import app.mediabrainz.communicator.OnReleaseGroupCommunicator;
import app.mediabrainz.data.Status;
import app.mediabrainz.ui.ReleaseGroupsViewModel;


public class ReleaseGroupsTabFragment extends LazyFragment implements RetryCallback {

    private static final String RELEASES_TAB = "RELEASES_TAB";

    private ReleaseGroupsPagerAdapter.ReleaseTab releaseGroupType;
    private ReleaseGroupsViewModel releaseGroupsViewModel;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView pagedRecycler;
    private ReleaseGroupsAdapter adapter;

    private CheckBox officialCheckBox;
    private TextView errorMessageTextView;
    private Button retryLoadingButton;
    private ProgressBar loadingProgressBar;
    private View itemNetworkState;

    private MutableLiveData<Boolean> mutableIsOfficial = new MutableLiveData<>();


    public static ReleaseGroupsTabFragment newInstance(int releasesTab) {
        Bundle args = new Bundle();
        args.putInt(RELEASES_TAB, releasesTab);
        ReleaseGroupsTabFragment fragment = new ReleaseGroupsTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_release_groups, container, false);

        releaseGroupType = ReleaseGroupsPagerAdapter.ReleaseTab.values()[getArguments().getInt(RELEASES_TAB)];

        officialCheckBox = layout.findViewById(R.id.official_checkbox);
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
    protected void lazyLoad() {
        String artistMbid = ((GetArtistCommunicator) getContext()).getArtistMbid();
        if (!TextUtils.isEmpty(artistMbid)) {
            adapter = new ReleaseGroupsAdapter(this);
            adapter.setHolderClickListener(releaseGroup -> ((OnReleaseGroupCommunicator) getContext()).onReleaseGroup(releaseGroup.getId()));

            releaseGroupsViewModel = ViewModelProviders.of(this).get(ReleaseGroupsViewModel.class);
            mutableIsOfficial.setValue(officialCheckBox.isChecked());
            releaseGroupsViewModel.load(artistMbid, releaseGroupType.getAlbumType(), mutableIsOfficial);
            releaseGroupsViewModel.realeseGroupLiveData.observe(this, adapter::submitList);
            releaseGroupsViewModel.getNetworkState().observe(this, adapter::setNetworkState);

            pagedRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
            pagedRecycler.setNestedScrollingEnabled(true);
            pagedRecycler.setItemViewCacheSize(100);
            pagedRecycler.setHasFixedSize(true);
            pagedRecycler.setAdapter(adapter);

            officialCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                mutableIsOfficial.setValue(isChecked);
                releaseGroupsViewModel.refresh();
                swipeRefreshLayout.setRefreshing(false);
                pagedRecycler.scrollToPosition(0);
            });

            initSwipeToRefresh();
        }
    }

    private void initSwipeToRefresh() {
        releaseGroupsViewModel.getRefreshState().observe(this, networkState -> {
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
            releaseGroupsViewModel.refresh();
            swipeRefreshLayout.setRefreshing(false);
            pagedRecycler.scrollToPosition(0);
        });
    }

    @Override
    public void retry() {
        if (releaseGroupsViewModel != null) {
            releaseGroupsViewModel.retry();
        }
    }

}
