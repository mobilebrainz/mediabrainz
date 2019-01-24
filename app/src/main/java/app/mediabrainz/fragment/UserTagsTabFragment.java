package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.UserTagsAdapter;
import app.mediabrainz.api.model.Tag;
import app.mediabrainz.communicator.OnUserTagCommunicator;
import app.mediabrainz.viewModels.UserTagsPagerVM;

import static app.mediabrainz.viewModels.Status.SUCCESS;


public class UserTagsTabFragment extends BaseFragment {

    private static final String TAGS_TAB = "UserTagsTabFragment.TAGS_TAB";

    private Tag.TagType tagType;
    private UserTagsPagerVM userTagsPagerVM;

    private View noresultsView;
    private RecyclerView recyclerView;

    public static UserTagsTabFragment newInstance(int tagsTab) {
        Bundle args = new Bundle();
        args.putInt(TAGS_TAB, tagsTab);
        UserTagsTabFragment fragment = new UserTagsTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflate(R.layout.fragment_recycler_view, container);
        noresultsView = layout.findViewById(R.id.noresultsView);

        recyclerView = layout.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemViewCacheSize(100);
        recyclerView.setHasFixedSize(true);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null && getArguments() != null) {
            tagType = Tag.TagType.values()[getArguments().getInt(TAGS_TAB)];

            userTagsPagerVM = getActivityViewModel(UserTagsPagerVM.class);
            userTagsPagerVM.userTagsResource.observe(this, resource -> {
                if (resource != null && resource.getStatus() == SUCCESS) {
                    show(resource.getData());
                }
            });
        }
    }

    private void show(Map<Tag.TagType, List<Tag>> tagMap) {
        noresultsView.setVisibility(View.GONE);

        if (tagMap == null || tagMap.isEmpty()) {
            noresultsView.setVisibility(View.VISIBLE);
            return;
        }

        final List<Tag> tags = new ArrayList<>();
        List<Tag> t;
        switch (tagType) {
            case TAG:
                if ((t = tagMap.get(Tag.TagType.TAG)) != null) {
                    tags.addAll(t);
                }
                break;
            case GENRE:
                if ((t = tagMap.get(Tag.TagType.GENRE)) != null) {
                    tags.addAll(t);
                }
                break;
        }

        if (tags.isEmpty()) {
            noresultsView.setVisibility(View.VISIBLE);
        } else {
            final UserTagsAdapter adapter = new UserTagsAdapter(tags);
            adapter.setHolderClickListener(position -> {
                if (getContext() instanceof OnUserTagCommunicator) {
                    ((OnUserTagCommunicator) getContext()).onUserTag(tags.get(position).getName());
                }
            });
            recyclerView.setAdapter(adapter);
        }
    }

}
