package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
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
import app.mediabrainz.communicator.GetUserTagEntitiesCommunicator;
import app.mediabrainz.communicator.OnArtistCommunicator;
import app.mediabrainz.communicator.OnRecordingCommunicator;
import app.mediabrainz.communicator.OnReleaseGroupCommunicator;

import static app.mediabrainz.adapter.pager.UserTagPagerAdapter.TAB_ARTISTS_POS;
import static app.mediabrainz.adapter.pager.UserTagPagerAdapter.TAB_RECORDINGS_POS;
import static app.mediabrainz.adapter.pager.UserTagPagerAdapter.TAB_RELEASE_GROUPS_POS;


public class UserTagListFragment extends Fragment {

    public static final String TAG_TYPE = "TAG_TYPE";

    private int intTagType = -1;
    private TagServiceInterface.UserTagType userTagType;


    private View noresults;
    private RecyclerView recycler;

    public static UserTagListFragment newInstance(int tagType) {
        Bundle args = new Bundle();
        args.putInt(TAG_TYPE, tagType);
        UserTagListFragment fragment = new UserTagListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_user_tag_list, container, false);
        noresults = layout.findViewById(R.id.noresults);
        recycler = layout.findViewById(R.id.recycler);

        intTagType = getArguments().getInt(TAG_TYPE);

        configRecycler();
        load();
        return layout;
    }

    private void configRecycler() {
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setItemViewCacheSize(100);
        recycler.setHasFixedSize(true);
    }

    public void load() {
        noresults.setVisibility(View.GONE);

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
        List<TagEntity> tagEntities = ((GetUserTagEntitiesCommunicator) getParentFragment()).getEntities(userTagType);
        if (tagEntities != null) {
            if (tagEntities.isEmpty()) {
                noresults.setVisibility(View.VISIBLE);
            } else {
                switch (intTagType) {
                    case TAB_ARTISTS_POS:
                        ArtistTagAdapter artistTagAdapter = new ArtistTagAdapter(tagEntities);
                        artistTagAdapter.setHolderClickListener(position ->
                                ((OnArtistCommunicator) getContext()).onArtist(tagEntities.get(position).getMbid()));
                        recycler.setAdapter(artistTagAdapter);
                        break;
                    case TAB_RELEASE_GROUPS_POS:
                        EntityTagAdapter rgAdapter = new EntityTagAdapter(tagEntities);
                        rgAdapter.setHolderClickListener(position ->
                                ((OnReleaseGroupCommunicator) getContext()).onReleaseGroup(tagEntities.get(position).getMbid()));
                        recycler.setAdapter(rgAdapter);
                        break;
                    case TAB_RECORDINGS_POS:
                        EntityTagAdapter recordingAdapter = new EntityTagAdapter(tagEntities);
                        recordingAdapter.setHolderClickListener(position ->
                                ((OnRecordingCommunicator) getContext()).onRecording(tagEntities.get(position).getMbid()));
                        recycler.setAdapter(recordingAdapter);
                        break;
                }
            }
        }
    }

}
