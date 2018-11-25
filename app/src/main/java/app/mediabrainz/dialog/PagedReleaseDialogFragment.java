package app.mediabrainz.dialog;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.PagedReleaseAdapter;
import app.mediabrainz.adapter.recycler.RetryCallback;
import app.mediabrainz.api.browse.ReleaseBrowseService;
import app.mediabrainz.communicator.OnReleaseCommunicator;
import app.mediabrainz.data.Status;
import app.mediabrainz.ui.ReleasesViewModel;


public class PagedReleaseDialogFragment extends DialogFragment implements
        RetryCallback {

    public static final String TAG = "PagedReleaseDialogFragment";
    private static final String RG_MBID = "RG_MBID";

    private String albumMbid;
    private ReleasesViewModel releasesViewModel;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView pagedRecycler;
    private PagedReleaseAdapter adapter;

    private TextView errorMessageTextView;
    private Button retryLoadingButton;
    private ProgressBar loadingProgressBar;
    private View itemNetworkState;

    public static PagedReleaseDialogFragment newInstance(String mbid) {
        Bundle args = new Bundle();
        args.putString(RG_MBID, mbid);
        PagedReleaseDialogFragment fragment = new PagedReleaseDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.dialog_fragment_releases, container, false);

        albumMbid = getArguments().getString(RG_MBID);

        pagedRecycler = layout.findViewById(R.id.paged_recycler);
        swipeRefreshLayout = layout.findViewById(R.id.swipe_refresh_layout);
        errorMessageTextView = layout.findViewById(R.id.errorMessageTextView);
        loadingProgressBar = layout.findViewById(R.id.loadingProgressBar);
        itemNetworkState = layout.findViewById(R.id.item_network_state);

        retryLoadingButton = layout.findViewById(R.id.retryLoadingButton);
        retryLoadingButton.setOnClickListener(view -> retry());

        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(RG_MBID, albumMbid);
    }

    @Override
    public void onStart() {
        super.onStart();
        load();
    }

    @Override
    public void onResume() {
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setLayout(width, height);
        super.onResume();
    }

    public void load() {
        adapter = new PagedReleaseAdapter(this, null);
        adapter.setHolderClickListener(r -> {
            ((OnReleaseCommunicator) getContext()).onRelease(r.getId());
            dismiss();
        });

        releasesViewModel = ViewModelProviders.of(this).get(ReleasesViewModel.class);
        releasesViewModel.load(albumMbid, ReleaseBrowseService.ReleaseBrowseEntityType.RELEASE_GROUP);
        releasesViewModel.realeseLiveData.observe(this, adapter::submitList);
        releasesViewModel.getNetworkState().observe(this, adapter::setNetworkState);

        pagedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        pagedRecycler.setNestedScrollingEnabled(true);
        pagedRecycler.setItemViewCacheSize(100);
        pagedRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        pagedRecycler.setHasFixedSize(true);
        pagedRecycler.setAdapter(adapter);

        initSwipeToRefresh();
    }

    private void initSwipeToRefresh() {
        releasesViewModel.getRefreshState().observe(this, networkState -> {
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
