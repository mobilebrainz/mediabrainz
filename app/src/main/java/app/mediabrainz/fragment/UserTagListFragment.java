package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;

import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.ArtistTagAdapter;
import app.mediabrainz.adapter.recycler.EntityTagAdapter;
import app.mediabrainz.api.site.TagEntity;
import app.mediabrainz.api.site.TagServiceInterface;
import app.mediabrainz.communicator.OnArtistCommunicator;
import app.mediabrainz.communicator.OnRecordingCommunicator;
import app.mediabrainz.communicator.OnReleaseGroupCommunicator;
import app.mediabrainz.viewModels.Resource;
import app.mediabrainz.viewModels.UserTagVM;

import static app.mediabrainz.adapter.pager.UserTagPagerAdapter.TAB_ARTISTS_POS;
import static app.mediabrainz.adapter.pager.UserTagPagerAdapter.TAB_RECORDINGS_POS;
import static app.mediabrainz.adapter.pager.UserTagPagerAdapter.TAB_RELEASE_GROUPS_POS;


public class UserTagListFragment extends BaseFragment {

    public static final String TAG_TYPE = "UserTagListFragment.TAG_TYPE";

    private int intTagType = -1;

    private View noresultsView;
    private RecyclerView recyclerView;

    public static UserTagListFragment newInstance(int tagType) {
        Bundle args = new Bundle();
        args.putInt(TAG_TYPE, tagType);
        UserTagListFragment fragment = new UserTagListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflate(R.layout.fragment_recycler_view, container);
        noresultsView = layout.findViewById(R.id.noresultsView);
        recyclerView = layout.findViewById(R.id.recyclerView);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null && getArguments() != null) {
            intTagType = getArguments().getInt(TAG_TYPE, -1);

            TagServiceInterface.UserTagType userTagType = null;
            switch (intTagType) {
                case TAB_ARTISTS_POS:
                    userTagType = TagServiceInterface.UserTagType.ARTISTS;
                    break;
                case TAB_RELEASE_GROUPS_POS:
                    userTagType = TagServiceInterface.UserTagType.RELEASE_GROUPS;
                    break;
                case TAB_RECORDINGS_POS:
                    userTagType = TagServiceInterface.UserTagType.RECORDINGS;
                    break;
            }

            if (userTagType != null) {
                UserTagVM userTagVM = getActivityViewModel(UserTagVM.class);

                Map<TagServiceInterface.UserTagType, List<TagEntity>> entitiesMap = userTagVM.getEntitiesMap();
                if (entitiesMap != null) {
                    List<TagEntity> tagEntities = entitiesMap.get(userTagType);
                    show(tagEntities);
                } else {
                    userTagVM.entitiesMapResource.setValue(Resource.invalidate());
                }
            }
        }
    }

    private void configRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemViewCacheSize(100);
        recyclerView.setHasFixedSize(true);
    }

    public void show(List<TagEntity> tagEntities) {
        noresultsView.setVisibility(View.GONE);

        if (tagEntities != null) {
            configRecycler();
            if (tagEntities.isEmpty()) {
                noresultsView.setVisibility(View.VISIBLE);
            } else {
                switch (intTagType) {
                    case TAB_ARTISTS_POS:
                        ArtistTagAdapter artistTagAdapter = new ArtistTagAdapter(tagEntities);
                        artistTagAdapter.setHolderClickListener(position -> {
                            if (getContext() instanceof OnArtistCommunicator) {
                                ((OnArtistCommunicator) getContext()).onArtist(tagEntities.get(position).getMbid());
                            }
                        });
                        recyclerView.setAdapter(artistTagAdapter);
                        break;
                    case TAB_RELEASE_GROUPS_POS:
                        EntityTagAdapter rgAdapter = new EntityTagAdapter(tagEntities);
                        rgAdapter.setHolderClickListener(position -> {
                            if (getContext() instanceof OnReleaseGroupCommunicator) {
                                ((OnReleaseGroupCommunicator) getContext()).onReleaseGroup(tagEntities.get(position).getMbid());
                            }
                        });
                        recyclerView.setAdapter(rgAdapter);
                        break;
                    case TAB_RECORDINGS_POS:
                        EntityTagAdapter recordingAdapter = new EntityTagAdapter(tagEntities);
                        recordingAdapter.setHolderClickListener(position -> {
                            if (getContext() instanceof OnRecordingCommunicator) {
                                ((OnRecordingCommunicator) getContext()).onRecording(tagEntities.get(position).getMbid());
                            }
                        });
                        recyclerView.setAdapter(recordingAdapter);
                        break;
                }
            }
        }
    }

}
