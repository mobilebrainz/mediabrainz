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
import app.mediabrainz.adapter.recycler.BasePagedListAdapter;
import app.mediabrainz.adapter.recycler.PagedArtistTagAdapter;
import app.mediabrainz.adapter.recycler.PagedEntityTagAdapter;
import app.mediabrainz.adapter.recycler.RetryCallback;
import app.mediabrainz.api.site.TagEntity;
import app.mediabrainz.api.site.TagServiceInterface;
import app.mediabrainz.communicator.GetTagCommunicator;
import app.mediabrainz.communicator.OnArtistCommunicator;
import app.mediabrainz.communicator.OnRecordingCommunicator;
import app.mediabrainz.communicator.OnReleaseGroupCommunicator;
import app.mediabrainz.data.Status;
import app.mediabrainz.ui.TagViewModel;


public class TagTabFragment extends Fragment implements RetryCallback {

    private static final String TAG_TAB = "TAG_TAB";

    private TagServiceInterface.TagType tagType;
    private TagViewModel tagViewModel;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView tagRecycler;
    private BasePagedListAdapter<TagEntity> adapter;

    private TextView errorMessageTextView;
    private Button retryLoadingButton;
    private ProgressBar loadingProgressBar;
    private View itemNetworkState;

    public static TagTabFragment newInstance(int tagTab) {
        Bundle args = new Bundle();
        args.putInt(TAG_TAB, tagTab);
        TagTabFragment fragment = new TagTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_tag, container, false);

        tagType = TagServiceInterface.TagType.values()[getArguments().getInt(TAG_TAB)];

        tagRecycler = layout.findViewById(R.id.tag_recycler);
        swipeRefreshLayout = layout.findViewById(R.id.swipe_refresh_layout);
        errorMessageTextView = layout.findViewById(R.id.errorMessageTextView);
        loadingProgressBar = layout.findViewById(R.id.loadingProgressBar);
        itemNetworkState = layout.findViewById(R.id.item_network_state);

        retryLoadingButton = layout.findViewById(R.id.retryLoadingButton);
        retryLoadingButton.setOnClickListener(view -> retry());

        load();
        return layout;
    }

    public void load() {
        String tag = ((GetTagCommunicator) getContext()).getTag();
        if (tag != null) {
            switch (tagType) {
                case ARTIST:
                    PagedArtistTagAdapter pagedArtistTagAdapter = new PagedArtistTagAdapter(this);
                    pagedArtistTagAdapter.setHolderClickListener(tagEntity -> ((OnArtistCommunicator) getContext()).onArtist(tagEntity.getMbid()));
                    adapter = pagedArtistTagAdapter;
                    break;

                case RELEASE_GROUP:
                    PagedEntityTagAdapter pagedRgTagAdapter = new PagedEntityTagAdapter(this);
                    pagedRgTagAdapter.setHolderClickListener(tagEntity -> ((OnReleaseGroupCommunicator) getContext()).onReleaseGroup(tagEntity.getMbid()));
                    adapter = pagedRgTagAdapter;
                    break;

                case RECORDING:
                    PagedEntityTagAdapter pagedRecordingTagAdapter = new PagedEntityTagAdapter(this);
                    pagedRecordingTagAdapter.setHolderClickListener(tagEntity -> ((OnRecordingCommunicator) getContext()).onRecording(tagEntity.getMbid()));
                    adapter = pagedRecordingTagAdapter;
                    break;
            }

            tagViewModel = ViewModelProviders.of(this).get(TagViewModel.class);
            tagViewModel.load(tagType, tag);
            tagViewModel.tagLiveData.observe(this, adapter::submitList);
            tagViewModel.getNetworkState().observe(this, adapter::setNetworkState);

            tagRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            tagRecycler.setNestedScrollingEnabled(true);
            //tagRecycler.setHasFixedSize(true);
            tagRecycler.setAdapter(adapter);

            initSwipeToRefresh();
        }
    }

    /**
     * Init swipe to refresh and enable pull to refresh only when there are items in the adapter
     */
    private void initSwipeToRefresh() {
        tagViewModel.getRefreshState().observe(this, networkState -> {
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
                    tagRecycler.scrollToPosition(0);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            tagViewModel.refresh();
            swipeRefreshLayout.setRefreshing(false);
            tagRecycler.scrollToPosition(0);
        });
    }

    @Override
    public void retry() {
        if (tagViewModel != null) {
            tagViewModel.retry();
        }
    }

}
