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

import java.util.ArrayList;
import java.util.List;

import app.mediabrainz.MediaBrainzApp;
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

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.MediaBrainzApp.oauth;
import static app.mediabrainz.adapter.pager.EditTagsPagerAdapter.TagsTab.GENRES;
import static app.mediabrainz.adapter.pager.EditTagsPagerAdapter.TagsTab.TAGS;
import static app.mediabrainz.api.model.ReleaseGroup.PrimaryType.ALBUM;
import static app.mediabrainz.api.model.ReleaseGroup.SecondaryType.NOTHING;


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

    private View contentView;
    private View errorView;
    private View progressView;
    private TextView loginWarningView;
    private AutoCompleteTextView tagInputView;
    private ImageButton tagButton;

    private ViewPager pagerView;
    private TabLayout tabsView;

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

        contentView = layout.findViewById(R.id.contentView);
        errorView = layout.findViewById(R.id.errorView);
        progressView = layout.findViewById(R.id.progressView);
        loginWarningView = layout.findViewById(R.id.loginWarningView);
        tagInputView = layout.findViewById(R.id.tagInputView);
        tagButton = layout.findViewById(R.id.tagButton);

        pagerView = layout.findViewById(R.id.pagerView);
        tabsView = layout.findViewById(R.id.tabsView);

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
            pagerView.setAdapter(pagerAdapter);
            pagerView.setOffscreenPageLimit(pagerAdapter.getCount());
            tabsView.setupWithViewPager(pagerView);
            tabsView.setTabMode(TabLayout.MODE_FIXED);
            pagerAdapter.setupTabViews(tabsView);
            pagerView.setCurrentItem(tagsTab);

            api.getGenres(
                    g -> {
                        this.allGenres = g;
                        adapter = new ArrayAdapter<>(
                                getContext(),
                                android.R.layout.simple_dropdown_item_1line,
                                allGenres.toArray(new String[allGenres.size()]));
                        tagInputView.setThreshold(1);
                        tagInputView.setAdapter(adapter);
                    },
                    this::showConnectionWarning);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        loginWarningView.setVisibility(oauth.hasAccount() ? View.GONE : View.VISIBLE);
    }

    private void setEditListeners() {
        tagButton.setOnClickListener(v -> {
            if (progressView.getVisibility() == View.VISIBLE) {
                return;
            }
            if (oauth.hasAccount()) {
                String tagString = tagInputView.getText().toString().trim();
                if (TextUtils.isEmpty(tagString)) {
                    tagInputView.setText("");
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
                                    tagInputView.setText("");

                                    if (MediaBrainzApp.getPreferences().isPropagateArtistTags()) {
                                        propagateTagToAlbums(tag, voteType);
                                    } else {
                                        lazyLoad();
                                    }
                                },
                                this::showConnectionWarning
                        );
                    } else {
                        viewProgressLoading(false);
                        ShowUtil.showToast(getContext(), R.string.error_post_tag);
                    }
                },
                this::showConnectionWarning
        );
    }

    public void propagateTagToAlbums(String tag, UserTagXML.VoteType voteType) {
        api.searchOfficialReleaseGroups(artist.getId(),
                releaseGroupSearch -> {
                    if (releaseGroupSearch.getCount() > 0) {
                        api.postTagToReleaseGroups(
                                tag, voteType, releaseGroupSearch.getReleaseGroups(),
                                metadata1 -> {
                                    if (metadata1.getMessage().getText().equals("OK")) {
                                        ShowUtil.showToast(getContext(), R.string.tag_propagated_to_albums);
                                    } else {
                                        ShowUtil.showToast(getContext(), R.string.error_tag_propagation_to_albums);
                                    }
                                    lazyLoad();
                                },
                                t -> {
                                    lazyLoad();
                                    ShowUtil.showToast(getContext(), R.string.error_tag_propagation_to_albums);
                                });
                    } else {
                        lazyLoad();
                    }
                },
                t -> {
                    lazyLoad();
                    ShowUtil.showToast(getContext(), R.string.error_tag_propagation_to_albums);
                },
                100, 0, ALBUM, NOTHING);
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
                                    tagInputView.setText("");
                                    viewProgressLoading(false);
                                },
                                this::showConnectionWarning
                        );
                    } else {
                        viewProgressLoading(false);
                        ShowUtil.showToast(getContext(), R.string.error_post_tag);
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
                                    tagInputView.setText("");
                                    viewProgressLoading(false);
                                },
                                this::showConnectionWarning
                        );
                    } else {
                        viewProgressLoading(false);
                        ShowUtil.showToast(getContext(), R.string.error_post_tag);
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
            contentView.setAlpha(0.3F);
            progressView.setVisibility(View.VISIBLE);
        } else {
            contentView.setAlpha(1.0F);
            progressView.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean isView) {
        if (isView) {
            contentView.setVisibility(View.INVISIBLE);
            errorView.setVisibility(View.VISIBLE);
        } else {
            errorView.setVisibility(View.GONE);
            contentView.setVisibility(View.VISIBLE);
        }
    }

    private void showConnectionWarning(Throwable t) {
        //ShowUtil.showError(getActivity(), t);
        viewProgressLoading(false);
        viewError(true);
        errorView.setVisibility(View.VISIBLE);
        errorView.findViewById(R.id.retryButton).setOnClickListener(v -> lazyLoad());
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
