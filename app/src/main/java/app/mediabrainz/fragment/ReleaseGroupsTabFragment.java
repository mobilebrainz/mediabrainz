package app.mediabrainz.fragment;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.R;
import app.mediabrainz.adapter.pager.ReleaseGroupsPagerAdapter;
import app.mediabrainz.adapter.recycler.ReleaseGroupsAdapter;
import app.mediabrainz.adapter.recycler.RetryCallback;
import app.mediabrainz.communicator.GetArtistCommunicator;
import app.mediabrainz.communicator.OnReleaseGroupCommunicator;
import app.mediabrainz.data.Status;
import app.mediabrainz.ui.ReleaseGroupsViewModel;

import static app.mediabrainz.account.Preferences.PreferenceName.RELEASE_GROUP_OFFICIAL;


public class ReleaseGroupsTabFragment extends LazyFragment implements
        RetryCallback,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String RELEASES_TAB = "RELEASES_TAB";

    private ReleaseGroupsPagerAdapter.ReleaseTab releaseGroupType;
    private ReleaseGroupsViewModel releaseGroupsViewModel;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView pagedRecyclerView;
    private ReleaseGroupsAdapter adapter;

    private TextView errorMessageTextView;
    private Button retryLoadingButton;
    private ProgressBar loadingProgressBar;
    private View itemNetworkStateView;

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
    protected void lazyLoad() {
        String artistMbid = ((GetArtistCommunicator) getContext()).getArtistMbid();
        if (!TextUtils.isEmpty(artistMbid)) {
            adapter = new ReleaseGroupsAdapter(this);
            adapter.setHolderClickListener(releaseGroup -> ((OnReleaseGroupCommunicator) getContext()).onReleaseGroup(releaseGroup.getId()));

            releaseGroupsViewModel = ViewModelProviders.of(this).get(ReleaseGroupsViewModel.class);
            mutableIsOfficial.setValue(MediaBrainzApp.getPreferences().isReleaseGroupOfficial());
            releaseGroupsViewModel.load(artistMbid, releaseGroupType.getAlbumType(), mutableIsOfficial);
            releaseGroupsViewModel.realeseGroupLiveData.observe(this, adapter::submitList);
            releaseGroupsViewModel.getNetworkState().observe(this, adapter::setNetworkState);

            pagedRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            pagedRecyclerView.setNestedScrollingEnabled(true);
            pagedRecyclerView.setItemViewCacheSize(100);
            pagedRecyclerView.setHasFixedSize(true);
            pagedRecyclerView.setAdapter(adapter);

            MediaBrainzApp.getPreferences().registerOnSharedPreferenceChangeListener(this);

            initSwipeToRefresh();
        }
    }

    private void initSwipeToRefresh() {
        releaseGroupsViewModel.getRefreshState().observe(this, networkState -> {
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
            releaseGroupsViewModel.refresh();
            swipeRefreshLayout.setRefreshing(false);
            pagedRecyclerView.scrollToPosition(0);
        });
    }

    @Override
    public void retry() {
        if (releaseGroupsViewModel != null) {
            releaseGroupsViewModel.retry();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(RELEASE_GROUP_OFFICIAL)) {
            if (!loadView()) {
                pagedRecyclerView.setAdapter(null);
                setLoaded(false);
            }
            /*
            if (getUserVisibleHint()) {
                mutableIsOfficial.setValue(MediaBrainzApp.getPreferences().isReleaseGroupOfficial());
                releaseGroupsViewModel.refresh();
                swipeRefreshLayout.setRefreshing(false);
                pagedRecycler.scrollToPosition(0);
            } else {
                pagedRecycler.setAdapter(null);
                setLoaded(false);
            }
            */
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MediaBrainzApp.getPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

}
