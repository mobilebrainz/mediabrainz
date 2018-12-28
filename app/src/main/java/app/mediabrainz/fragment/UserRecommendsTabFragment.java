package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
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
import app.mediabrainz.data.room.repository.RecommendRepository;

import static app.mediabrainz.MediaBrainzApp.api;


public class UserRecommendsTabFragment extends Fragment {

    private static final String RECOMMENDS_TAB = "RECOMMENDS_TAB";
    //LIMIT_RECOMMENDS range: 0 - 200
    private final int LIMIT_RECOMMENDS = 100;

    private TagServiceInterface.TagType tagType;

    private boolean isLoading;
    private boolean isError;
    private String primaryTag;
    private String secondaryTag;
    private int tagRate = 0;

    private View errorView;
    private View progressView;
    private View noresultsView;
    private RecyclerView recycler;


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

        tagType = TagServiceInterface.TagType.values()[getArguments().getInt(RECOMMENDS_TAB)];

        errorView = layout.findViewById(R.id.errorView);
        progressView = layout.findViewById(R.id.progressView);
        noresultsView = layout.findViewById(R.id.noresultsView);
        recycler = layout.findViewById(R.id.recyclerView);

        load();
        return layout;
    }

    private void configRecycler() {
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setItemViewCacheSize(100);
        recycler.setHasFixedSize(true);
    }

    private void load() {
        viewError(false);
        noresultsView.setVisibility(View.GONE);

        viewProgressLoading(true);
        new RecommendRepository().getAll(recommends -> {
            if (!recommends.isEmpty()) {
                primaryTag = recommends.get(0).getTag();
                int primaryNumber = recommends.get(0).getNumber();
                if (recommends.size() > 1) {
                    secondaryTag = recommends.get(1).getTag();
                    int secondaryNumber = recommends.get(1).getNumber();
                    tagRate = primaryNumber / secondaryNumber;
                }
                api.getTagEntities(tagType, primaryTag, 1,
                        primaryPage -> {
                            if (secondaryTag != null) {
                                api.getTagEntities(tagType, secondaryTag, 1,
                                        secondaryPage -> handleResult(primaryPage, secondaryPage),
                                        this::showConnectionWarning);
                            } else {
                                handleResult(primaryPage, null);
                            }
                        },
                        this::showConnectionWarning);

            } else {
                viewProgressLoading(false);
                noresultsView.setVisibility(View.VISIBLE);
            }
        });
    }

    // сделать общий список из соотношения тегов rateTags
    // перемешать из соотношения rateTags (напр. rateTags=2.6, округл до 2 и тогда после каждых 2 primaryTag должен идти 1 secondaryTag
    private void handleResult(TagEntity.Page primaryPage, TagEntity.Page secondaryPage) {
        viewProgressLoading(false);

        final List<TagEntity> recommends = new ArrayList<>();
        if (secondaryPage == null || secondaryPage.getTagEntities().isEmpty()) {
            recommends.addAll(primaryPage.getTagEntities());
        } else {
            int i = 0;
            int k = 0;
            List<TagEntity> secondaryEntities = secondaryPage.getTagEntities();
            int secondarySize = secondaryEntities.size();
            for (TagEntity primaryEntity : primaryPage.getTagEntities()) {
                recommends.add(primaryEntity);
                i++;
                if (i % tagRate == 0 && secondarySize > k) {
                    recommends.add(secondaryEntities.get(k));
                    k++;
                }
                if (i + k == LIMIT_RECOMMENDS) {
                    break;
                }
            }
        }

        if (!recommends.isEmpty()) {
            configRecycler();
            switch (tagType) {
                case ARTIST:
                    ArtistTagAdapter artistTagAdapter = new ArtistTagAdapter(recommends);
                    artistTagAdapter.setHolderClickListener(position ->
                            ((OnArtistCommunicator) getContext()).onArtist(recommends.get(position).getMbid()));
                    recycler.setAdapter(artistTagAdapter);
                    break;

                case RELEASE_GROUP:
                    EntityTagAdapter rgAdapter = new EntityTagAdapter(recommends);
                    rgAdapter.setHolderClickListener(position ->
                            ((OnReleaseGroupCommunicator) getContext()).onReleaseGroup(recommends.get(position).getMbid()));
                    recycler.setAdapter(rgAdapter);
                    break;

                case RECORDING:
                    EntityTagAdapter recordingAdapter = new EntityTagAdapter(recommends);
                    recordingAdapter.setHolderClickListener(position ->
                            ((OnRecordingCommunicator) getContext()).onRecording(recommends.get(position).getMbid()));
                    recordingAdapter.setOnPlayYoutubeListener(keyword ->
                            ((OnPlayYoutubeCommunicator) getContext()).onPlay(keyword));
                    recycler.setAdapter(recordingAdapter);
                    break;
            }
        } else {
            noresultsView.setVisibility(View.VISIBLE);
        }
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
