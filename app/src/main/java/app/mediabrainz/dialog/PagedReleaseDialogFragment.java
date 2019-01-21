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
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.PagedReleaseAdapter;
import app.mediabrainz.adapter.recycler.RetryCallback;
import app.mediabrainz.api.browse.ReleaseBrowseService;
import app.mediabrainz.communicator.OnReleaseCommunicator;
import app.mediabrainz.viewModels.Status;
import app.mediabrainz.viewModels.ReleasesVM;


public class PagedReleaseDialogFragment extends DialogFragment implements
        RetryCallback {

    public static final String TAG = "PagedReleaseDialogFragment";
    private static final String RG_MBID = "RG_MBID";

    private String albumMbid;
    private ReleasesVM releasesVM;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView pagedRecyclerView;
    private PagedReleaseAdapter adapter;

    private TextView errorMessageTextView;
    private Button retryLoadingButton;
    private ProgressBar loadingProgressBar;
    private View itemNetworkStateView;

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

        pagedRecyclerView = layout.findViewById(R.id.pagedRecyclerView);
        swipeRefreshLayout = layout.findViewById(R.id.swipeRefreshLayout);
        errorMessageTextView = layout.findViewById(R.id.errorMessageTextView);
        loadingProgressBar = layout.findViewById(R.id.loadingProgressBar);
        itemNetworkStateView = layout.findViewById(R.id.itemNetworkStateView);

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
        Window window = getDialog().getWindow();
        if (window != null) {
            getDialog().getWindow().setLayout(width, height);
        }
        super.onResume();
    }

    public void load() {
        adapter = new PagedReleaseAdapter(this, null);
        adapter.setHolderClickListener(r -> {
            if (getContext() instanceof OnReleaseCommunicator) {
                ((OnReleaseCommunicator) getContext()).onRelease(r.getId());
            }
            dismiss();
        });

        releasesVM = ViewModelProviders.of(this).get(ReleasesVM.class);
        releasesVM.load(albumMbid, ReleaseBrowseService.ReleaseBrowseEntityType.RELEASE_GROUP);
        releasesVM.realesesLiveData.observe(this, adapter::submitList);
        releasesVM.getNetworkState().observe(this, adapter::setNetworkState);

        pagedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pagedRecyclerView.setNestedScrollingEnabled(true);
        pagedRecyclerView.setItemViewCacheSize(100);
        pagedRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        pagedRecyclerView.setHasFixedSize(true);
        pagedRecyclerView.setAdapter(adapter);

        initSwipeToRefresh();
    }

    private void initSwipeToRefresh() {
        releasesVM.getRefreshState().observe(this, networkState -> {
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
