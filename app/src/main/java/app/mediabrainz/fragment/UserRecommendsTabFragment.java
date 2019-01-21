package app.mediabrainz.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.ArtistTagAdapter;
import app.mediabrainz.adapter.recycler.EntityTagAdapter;
import app.mediabrainz.api.site.TagEntity;
import app.mediabrainz.api.site.TagServiceInterface;
import app.mediabrainz.communicator.OnArtistCommunicator;
import app.mediabrainz.communicator.OnPlayYoutubeCommunicator;
import app.mediabrainz.communicator.OnRecordingCommunicator;
import app.mediabrainz.communicator.OnReleaseGroupCommunicator;
import app.mediabrainz.viewModels.UserRecommendsTabVM;


public class UserRecommendsTabFragment extends Fragment {

    private static final String RECOMMENDS_TAB = "RECOMMENDS_TAB";

    private TagServiceInterface.TagType tagType;

    private UserRecommendsTabVM userRecommendsTabVM;

    private boolean isLoading;
    private boolean isError;

    private View errorView;
    private View progressView;
    private View noresultsView;
    private RecyclerView recyclerView;


    public static UserRecommendsTabFragment newInstance(int recommendsTab) {
        Bundle args = new Bundle();
        args.putInt(RECOMMENDS_TAB, recommendsTab);
        UserRecommendsTabFragment fragment = new UserRecommendsTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        errorView = layout.findViewById(R.id.errorView);
        progressView = layout.findViewById(R.id.progressView);
        noresultsView = layout.findViewById(R.id.noresultsView);
        recyclerView = layout.findViewById(R.id.recyclerView);

        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tagType = TagServiceInterface.TagType.values()[getArguments().getInt(RECOMMENDS_TAB)];

        if (getActivity() != null && tagType != null) {

            userRecommendsTabVM = ViewModelProviders
                    .of(this, new UserRecommendsTabVM.Factory(tagType))
                    .get(UserRecommendsTabVM.class);

            userRecommendsTabVM.recommendsResource.observe(this, resource -> {
                if (resource == null) return;
                switch (resource.getStatus()) {
                    case LOADING:
                        viewProgressLoading(true);
                        break;
                    case ERROR:
                        showConnectionWarning(resource.getThrowable());
                        break;
                    case SUCCESS:
                        viewProgressLoading(false);
                        show(resource.getData());
                        break;
                }
            });
            load();
        }
    }

    private void load() {
        noresultsView.setVisibility(View.GONE);
        viewError(false);
        viewProgressLoading(false);
        userRecommendsTabVM.load();
    }

    private void show(List<TagEntity> recommends) {
        if (recommends.isEmpty()) {
            noresultsView.setVisibility(View.VISIBLE);
        } else {
            configRecycler();
            switch (tagType) {
                case ARTIST:
                    ArtistTagAdapter artistTagAdapter = new ArtistTagAdapter(recommends);
                    artistTagAdapter.setHolderClickListener(position ->
                            ((OnArtistCommunicator) getContext()).onArtist(recommends.get(position).getMbid()));
                    recyclerView.setAdapter(artistTagAdapter);
                    break;

                case RELEASE_GROUP:
                    EntityTagAdapter rgAdapter = new EntityTagAdapter(recommends);
                    rgAdapter.setHolderClickListener(position ->
                            ((OnReleaseGroupCommunicator) getContext()).onReleaseGroup(recommends.get(position).getMbid()));
                    recyclerView.setAdapter(rgAdapter);
                    break;

                case RECORDING:
                    EntityTagAdapter recordingAdapter = new EntityTagAdapter(recommends);
                    recordingAdapter.setHolderClickListener(position ->
                            ((OnRecordingCommunicator) getContext()).onRecording(recommends.get(position).getMbid()));
                    recordingAdapter.setOnPlayYoutubeListener(keyword ->
                            ((OnPlayYoutubeCommunicator) getContext()).onPlay(keyword));
                    recyclerView.setAdapter(recordingAdapter);
                    break;
            }
        }
    }

    private void configRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemViewCacheSize(100);
        recyclerView.setHasFixedSize(true);
    }

    private void viewProgressLoading(boolean isView) {
        if (isView) {
            isLoading = true;
            progressView.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            progressView.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean isView) {
        if (isView) {
            isError = true;
            errorView.setVisibility(View.VISIBLE);
        } else {
            isError = false;
            errorView.setVisibility(View.GONE);
        }
    }

    private void showConnectionWarning(Throwable t) {
        //ShowUtil.showError(getContext(), t);
        viewProgressLoading(false);
        viewError(true);
        errorView.findViewById(R.id.retryButton).setOnClickListener(v -> load());
    }

}
