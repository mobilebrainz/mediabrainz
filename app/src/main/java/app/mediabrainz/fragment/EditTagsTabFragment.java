package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import app.mediabrainz.R;
import app.mediabrainz.adapter.pager.EditTagsPagerAdapter;
import app.mediabrainz.adapter.recycler.TagAdapter;
import app.mediabrainz.api.model.Tag;
import app.mediabrainz.api.model.xml.UserTagXML;
import app.mediabrainz.communicator.OnTagCommunicator;
import app.mediabrainz.intent.ActivityFactory;

import java.util.ArrayList;
import java.util.List;

import static app.mediabrainz.MediaBrainzApp.oauth;


public class EditTagsTabFragment extends Fragment {

    public interface TagInterface {
        void postTag(String tag, UserTagXML.VoteType voteType, int tagsTab);

        List<Tag> getTags();

        List<Tag> getUserTags();

        List<Tag> getGenres();

        List<Tag> getUserGenres();
    }

    private static final String TAGS_TAB = "TAGS_TAB";

    private int tagsTab = 0;

    private View noresultsView;
    private RecyclerView recyclerView;

    public static EditTagsTabFragment newInstance(int tagsTab) {
        Bundle args = new Bundle();
        args.putInt(TAGS_TAB, tagsTab);
        EditTagsTabFragment fragment = new EditTagsTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        tagsTab = getArguments().getInt(TAGS_TAB);

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

        TagInterface parent = (TagInterface) getParentFragment();

        final List<Tag> tags = new ArrayList<>();
        final List<Tag> userTags = new ArrayList<>();
        final List<Tag> parentUserGenres = parent.getUserGenres();
        final List<Tag> parentUserTags = parent.getUserTags();

        EditTagsPagerAdapter.TagsTab tagType = EditTagsPagerAdapter.TagsTab.values()[tagsTab];
        switch (tagType) {
            case TAGS:
                List<Tag> genres = parent.getGenres();
                for (Tag tag : parent.getTags()) {
                    if (!genres.contains(tag)) {
                        tags.add(tag);
                    }
                }
                if (parentUserGenres != null && parentUserTags != null) {
                    for (Tag tag : parentUserTags) {
                        if (!parentUserGenres.contains(tag)) {
                            userTags.add(tag);
                        }
                    }
                }
                break;

            case GENRES:
                tags.addAll(parent.getGenres());
                if (parentUserGenres != null) {
                    userTags.addAll(parentUserGenres);
                }
                break;
        }

        if (!tags.isEmpty()) {
            configRecycler();
            TagAdapter adapter = new TagAdapter(tags, userTags);

            adapter.setHolderClickListener(pos ->
                    ((OnTagCommunicator) getContext()).onTag(tags.get(pos).getName(), tagType.equals(EditTagsPagerAdapter.TagsTab.GENRES)));
            recyclerView.setAdapter(adapter);

            adapter.setOnVoteTagListener((position) -> {
                if (oauth.hasAccount()) {
                    String tag = tags.get(position).getName();
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.show();
                    Window win = alertDialog.getWindow();
                    if (win != null) {
                        win.setContentView(R.layout.dialog_vote_tag);
                        ImageView voteUpButton = win.findViewById(R.id.voteUpButton);

                        voteUpButton.setOnClickListener(v -> {
                            alertDialog.dismiss();
                            parent.postTag(tag, UserTagXML.VoteType.UPVOTE, tagsTab);
                        });

                        ImageView voteWithdrawButton = win.findViewById(R.id.voteWithdrawButton);
                        voteWithdrawButton.setOnClickListener(v -> {
                            alertDialog.dismiss();
                            parent.postTag(tag, UserTagXML.VoteType.WITHDRAW, tagsTab);
                        });

                        ImageView voteDownButton = win.findViewById(R.id.voteDownButton);
                        voteDownButton.setOnClickListener(v -> {
                            alertDialog.dismiss();
                            parent.postTag(tag, UserTagXML.VoteType.DOWNVOTE, tagsTab);
                        });
                    }
                } else {
                    ActivityFactory.startLoginActivity(getContext());
                }
            });
        }
    }

}
