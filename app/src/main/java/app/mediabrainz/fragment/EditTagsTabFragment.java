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

    private View noresults;
    private RecyclerView tagsRecycler;

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

        noresults = layout.findViewById(R.id.noresults);
        tagsRecycler = layout.findViewById(R.id.recycler);

        load();
        return layout;
    }

    private void configRecycler() {
        tagsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        tagsRecycler.setItemViewCacheSize(100);
        tagsRecycler.setHasFixedSize(true);
    }

    private void load() {
        noresults.setVisibility(View.GONE);

        TagInterface parent = (TagInterface) getParentFragment();

        final List<Tag> tags = new ArrayList<>();
        final List<Tag> userTags = new ArrayList<>();

        EditTagsPagerAdapter.TagsTab tagType = EditTagsPagerAdapter.TagsTab.values()[tagsTab];
        switch (tagType) {
            case TAGS:
                List<Tag> genres = parent.getGenres();
                for (Tag tag : parent.getTags()) {
                    if (!genres.contains(tag)) {
                        tags.add(tag);
                    }
                }
                List<Tag> userGenres = parent.getUserGenres();
                for (Tag tag : parent.getUserTags()) {
                    if (!userGenres.contains(tag)) {
                        userTags.add(tag);
                    }
                }
                break;

            case GENRES:
                tags.addAll(parent.getGenres());
                userTags.addAll(parent.getUserGenres());
                break;
        }

        if (!tags.isEmpty()) {
            configRecycler();
            TagAdapter adapter = new TagAdapter(tags, userTags);

            adapter.setHolderClickListener(pos ->
                    ((OnTagCommunicator) getContext()).onTag(tags.get(pos).getName(), tagType.equals(EditTagsPagerAdapter.TagsTab.GENRES)));
            tagsRecycler.setAdapter(adapter);

            adapter.setOnVoteTagListener((position) -> {
                if (oauth.hasAccount()) {
                    String tag = tags.get(position).getName();
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.show();
                    Window win = alertDialog.getWindow();
                    if (win != null) {
                        win.setContentView(R.layout.dialog_vote_tag);
                        ImageView voteUpBtn = win.findViewById(R.id.vote_up_btn);

                        voteUpBtn.setOnClickListener(v -> {
                            alertDialog.dismiss();
                            parent.postTag(tag, UserTagXML.VoteType.UPVOTE, tagsTab);
                        });

                        ImageView voteWithdrawBtn = win.findViewById(R.id.vote_withdraw_btn);
                        voteWithdrawBtn.setOnClickListener(v -> {
                            alertDialog.dismiss();
                            parent.postTag(tag, UserTagXML.VoteType.WITHDRAW, tagsTab);
                        });

                        ImageView voteDownBtn = win.findViewById(R.id.vote_down_btn);
                        voteDownBtn.setOnClickListener(v -> {
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
