package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import app.mediabrainz.R;
import app.mediabrainz.adapter.pager.EditTagsPagerAdapter;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.Recording;
import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.api.model.Tag;
import app.mediabrainz.api.model.xml.UserTagXML;
import app.mediabrainz.communicator.GetArtistCommunicator;
import app.mediabrainz.communicator.GetRecordingCommunicator;
import app.mediabrainz.communicator.GetReleaseGroupCommunicator;
import app.mediabrainz.intent.ActivityFactory;
import app.mediabrainz.util.ShowUtil;

import java.util.ArrayList;
import java.util.List;

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.MediaBrainzApp.oauth;
import static app.mediabrainz.adapter.pager.EditTagsPagerAdapter.TagsTab.GENRES;
import static app.mediabrainz.adapter.pager.EditTagsPagerAdapter.TagsTab.TAGS;


public class EditTagsPagerFragment extends LazyFragment implements
        EditTagsTabFragment.TagInterface {

    public enum TagsPagerType {
        ARTIST, RELEASE, RECORDING
    }

    public static final String TAGS_PAGER_TYPE = "TAGS_PAGER_TYPE";
    private TagsPagerType tagsPagerType;

    private ArrayAdapter<String> adapter;

    private List<Tag> tags = new ArrayList<>();
    private List<Tag> userTags = new ArrayList<>();
    private List<Tag> genres = new ArrayList<>();
    private List<Tag> userGenres = new ArrayList<>();

    private List<String> allGenres = new ArrayList<>();
    private Artist artist;
    private ReleaseGroup releaseGroup;
    private Recording recording;

    private int tagsTab = EditTagsPagerAdapter.TagsTab.GENRES.ordinal();

    private View content;
    private View error;
    private View loading;
    private TextView loginWarning;
    private AutoCompleteTextView tagInput;
    private ImageButton tagBtn;

    private ViewPager viewPager;
    private TabLayout tabLayout;

    public static EditTagsPagerFragment newInstance(int tagsPagerType) {
        Bundle args = new Bundle();
        args.putInt(TAGS_PAGER_TYPE, tagsPagerType);
        EditTagsPagerFragment fragment = new EditTagsPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_edit_tags_pager, container, false);

        tagsPagerType = TagsPagerType.values()[getArguments().getInt(TAGS_PAGER_TYPE, 0)];

        content = layout.findViewById(R.id.content);
        error = layout.findViewById(R.id.error);
        loading = layout.findViewById(R.id.loading);
        loginWarning = layout.findViewById(R.id.login_warning);
        tagInput = layout.findViewById(R.id.tag_input);
        tagBtn = layout.findViewById(R.id.tag_btn);

        viewPager = layout.findViewById(R.id.pager);
        tabLayout = layout.findViewById(R.id.tabs);

        setEditListeners();
        loadView();
        return layout;
    }

    @Override
    public void lazyLoad() {
        viewProgressLoading(false);
        viewError(false);

        boolean isExist = true;
        switch (tagsPagerType) {
            case ARTIST:
                artist = ((GetArtistCommunicator) getContext()).getArtist();
                setTags(artist.getTags());
                setUserTags(artist.getUserTags());
                setGenres(artist.getGenres());
                setUserGenres(artist.getUserGenres());
                break;

            case RELEASE:
                releaseGroup = ((GetReleaseGroupCommunicator) getContext()).getReleaseGroup();
                setTags(releaseGroup.getTags());
                setUserTags(releaseGroup.getUserTags());
                setGenres(releaseGroup.getGenres());
                setUserGenres(releaseGroup.getUserGenres());
                break;

            case RECORDING:
                recording = ((GetRecordingCommunicator) getContext()).getRecording();
                setTags(recording.getTags());
                setUserTags(recording.getUserTags());
                setGenres(recording.getGenres());
                setUserGenres(recording.getUserGenres());
                break;

            default:
                isExist = false;
        }

        if (isExist) {
            EditTagsPagerAdapter pagerAdapter = new EditTagsPagerAdapter(getChildFragmentManager(), getResources());
            viewPager.setAdapter(pagerAdapter);
            viewPager.setOffscreenPageLimit(pagerAdapter.getCount());
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
            pagerAdapter.setupTabViews(tabLayout);
            viewPager.setCurrentItem(tagsTab);

            api.getGenres(
                    g -> {
                        this.allGenres = g;
                        adapter = new ArrayAdapter<>(
                                getContext(),
                                android.R.layout.simple_dropdown_item_1line,
                                allGenres.toArray(new String[allGenres.size()]));
                        tagInput.setThreshold(1);
                        tagInput.setAdapter(adapter);
                    },
                    this::showConnectionWarning);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        loginWarning.setVisibility(oauth.hasAccount() ? View.GONE : View.VISIBLE);
    }

    private void setEditListeners() {
        tagBtn.setOnClickListener(v -> {
            if (loading.getVisibility() == View.VISIBLE) {
                return;
            }
            if (oauth.hasAccount()) {
                String tagString = tagInput.getText().toString().trim();
                if (TextUtils.isEmpty(tagString)) {
                    tagInput.setText("");
                } else {
                    tagsTab = allGenres.contains(tagString.toLowerCase()) ? GENRES.ordinal() : TAGS.ordinal();
                    postTag(tagString, UserTagXML.VoteType.UPVOTE);
                }
            } else {
                ActivityFactory.startLoginActivity(getContext());
            }
        });
    }

    @Override
    public void postTag(String tag, UserTagXML.VoteType voteType, int tagsTab) {
        this.tagsTab = tagsTab;
        postTag(tag, voteType);
    }

    public void postArtistTag(String tag, UserTagXML.VoteType voteType) {
        api.postArtistTag(
                artist.getId(), tag, voteType,
                metadata -> {
                    if (metadata.getMessage().getText().equals("OK")) {
                        api.getArtistTags(
                                artist.getId(),
                                a -> {
                                    artist.setTags(a.getTags());
                                    artist.setUserTags(a.getUserTags());
                                    artist.setGenres(a.getGenres());
                                    artist.setUserGenres(a.getUserGenres());
                                    lazyLoad();
                                    tagInput.setText("");
                                    viewProgressLoading(false);
                                },
                                this::showConnectionWarning
                        );
                    } else {
                        viewProgressLoading(false);
                        ShowUtil.showMessage(getActivity(), "Error");
                    }
                },
                this::showConnectionWarning
        );
    }

    public void postReleaseGroupTag(String tag, UserTagXML.VoteType voteType) {
        api.postAlbumTag(
                releaseGroup.getId(), tag, voteType,
                metadata -> {
                    if (metadata.getMessage().getText().equals("OK")) {
                        api.getAlbumTags(
                                releaseGroup.getId(),
                                a -> {
                                    releaseGroup.setTags(a.getTags());
                                    releaseGroup.setUserTags(a.getUserTags());
                                    releaseGroup.setGenres(a.getGenres());
                                    releaseGroup.setUserGenres(a.getUserGenres());
                                    lazyLoad();
                                    tagInput.setText("");
                                    viewProgressLoading(false);
                                },
                                this::showConnectionWarning
                        );
                    } else {
                        viewProgressLoading(false);
                        ShowUtil.showMessage(getActivity(), "Error");
                    }
                },
                this::showConnectionWarning
        );
    }

    public void postRecordingTag(String tag, UserTagXML.VoteType voteType) {
        api.postRecordingTag(
                recording.getId(), tag, voteType,
                metadata -> {
                    if (metadata.getMessage().getText().equals("OK")) {
                        api.getRecordingTags(
                                recording.getId(),
                                a -> {
                                    recording.setTags(a.getTags());
                                    recording.setUserTags(a.getUserTags());
                                    recording.setGenres(a.getGenres());
                                    recording.setUserGenres(a.getUserGenres());
                                    lazyLoad();
                                    tagInput.setText("");
                                    viewProgressLoading(false);
                                },
                                this::showConnectionWarning
                        );
                    } else {
                        viewProgressLoading(false);
                        ShowUtil.showMessage(getActivity(), "Error");
                    }
                },
                this::showConnectionWarning
        );
    }

    public void postTag(String tag, UserTagXML.VoteType voteType) {
        viewProgressLoading(true);
        switch (tagsPagerType) {
            case ARTIST:
                postArtistTag(tag, voteType);
                break;

            case RELEASE:
                postReleaseGroupTag(tag, voteType);
                break;

            case RECORDING:
                postRecordingTag(tag, voteType);
                break;
        }
    }

    private void viewProgressLoading(boolean isView) {
        if (isView) {
            content.setAlpha(0.3F);
            loading.setVisibility(View.VISIBLE);
        } else {
            content.setAlpha(1.0F);
            loading.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean isView) {
        if (isView) {
            content.setVisibility(View.INVISIBLE);
            error.setVisibility(View.VISIBLE);
        } else {
            error.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
        }
    }

    private void showConnectionWarning(Throwable t) {
        //ShowUtil.showError(getActivity(), t);
        viewProgressLoading(false);
        viewError(true);
        error.setVisibility(View.VISIBLE);
        error.findViewById(R.id.retry_button).setOnClickListener(v -> lazyLoad());
    }

    @Override
    public List<Tag> getTags() {
        return tags;
    }

    @Override
    public List<Tag> getUserTags() {
        return userTags;
    }

    @Override
    public List<Tag> getGenres() {
        return genres;
    }

    @Override
    public List<Tag> getUserGenres() {
        return userGenres;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public void setUserTags(List<Tag> userTags) {
        this.userTags = userTags;
    }

    public void setGenres(List<Tag> genres) {
        this.genres = genres;
    }

    public void setUserGenres(List<Tag> userGenres) {
        this.userGenres = userGenres;
    }
}
