package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.UserTagsAdapter;
import app.mediabrainz.api.model.Tag;
import app.mediabrainz.communicator.GetGenresCommunicator;
import app.mediabrainz.communicator.GetTagsCommunicator;
import app.mediabrainz.communicator.GetUsernameCommunicator;
import app.mediabrainz.communicator.OnUserTagCommunicator;

import java.util.ArrayList;
import java.util.List;


public class UserTagsTabFragment extends Fragment {

    private static final String TAGS_TAB = "TAGS_TAB";

    private Tag.TagType tagType;

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
        View layout = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        tagType = Tag.TagType.values()[getArguments().getInt(TAGS_TAB)];

        noresultsView = layout.findViewById(R.id.noresultsView);
        recyclerView = layout.findViewById(R.id.recyclerView);

        load();
        return layout;
    }

    private void configRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemViewCacheSize(100);
        recyclerView.setHasFixedSize(true);
    }

    private void load() {
        noresultsView.setVisibility(View.GONE);

        String username = ((GetUsernameCommunicator) getContext()).getUsername();

        final List<Tag> tags = new ArrayList<>();
        switch (tagType) {
            case TAG:
                tags.addAll(((GetTagsCommunicator) getParentFragment()).getTags());
                break;

            case GENRE:
                tags.addAll(((GetGenresCommunicator) getParentFragment()).getGenres());
                break;
        }
        if (username != null) {
            if (tags.isEmpty()) {
                noresultsView.setVisibility(View.VISIBLE);
            } else {
                configRecycler();
                UserTagsAdapter adapter = new UserTagsAdapter(tags);
                adapter.setHolderClickListener(position ->
                        ((OnUserTagCommunicator) getContext()).onUserTag(username, tags.get(position).getName()));
                recyclerView.setAdapter(adapter);
            }
        }
    }

}
