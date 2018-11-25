package app.mediabrainz.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import app.mediabrainz.R;
import app.mediabrainz.adapter.pager.BaseFragmentPagerAdapter;
import app.mediabrainz.adapter.pager.ReleaseNavigationPagerAdapter;
import app.mediabrainz.adapter.pager.TagPagerAdapter;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.Collection;
import app.mediabrainz.api.model.RelationExtractor;
import app.mediabrainz.api.model.Release;
import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.api.model.Url;
import app.mediabrainz.api.other.CollectionServiceInterface;
import app.mediabrainz.api.site.SiteService;
import app.mediabrainz.communicator.GetCollectionsCommunicator;
import app.mediabrainz.communicator.GetReleaseCommunicator;
import app.mediabrainz.communicator.GetReleaseGroupCommunicator;
import app.mediabrainz.communicator.GetUrlsCommunicator;
import app.mediabrainz.communicator.OnArtistCommunicator;
import app.mediabrainz.communicator.OnRecordingCommunicator;
import app.mediabrainz.communicator.OnReleaseCommunicator;
import app.mediabrainz.communicator.OnTagCommunicator;
import app.mediabrainz.communicator.SetWebViewCommunicator;
import app.mediabrainz.communicator.ShowFloatingActionButtonCommunicator;
import app.mediabrainz.data.DatabaseHelper;
import app.mediabrainz.dialog.CollectionsDialogFragment;
import app.mediabrainz.dialog.CreateCollectionDialogFragment;
import app.mediabrainz.intent.ActivityFactory;
import app.mediabrainz.util.FloatingActionButtonBehavior;
import app.mediabrainz.util.ShowUtil;

import java.util.ArrayList;
import java.util.List;

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.MediaBrainzApp.oauth;
import static app.mediabrainz.adapter.pager.ReleaseNavigationPagerAdapter.TAB_INFO_POS;
import static app.mediabrainz.adapter.pager.ReleaseNavigationPagerAdapter.TAB_RATINGS_POS;
import static app.mediabrainz.adapter.pager.ReleaseNavigationPagerAdapter.TAB_RELEASES_POS;
import static app.mediabrainz.adapter.pager.ReleaseNavigationPagerAdapter.TAB_TAGS_POS;
import static app.mediabrainz.adapter.pager.ReleaseNavigationPagerAdapter.TAB_TRACKS_POS;
import static app.mediabrainz.api.model.Collection.RELEASE_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.RELEASE_GROUP_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.RELEASE_GROUP_TYPE;
import static app.mediabrainz.api.model.Collection.RELEASE_TYPE;


public class ReleaseActivity extends BaseBottomNavActivity implements
        ShowFloatingActionButtonCommunicator,
        OnArtistCommunicator,
        OnReleaseCommunicator,
        OnRecordingCommunicator,
        GetCollectionsCommunicator,
        CollectionsDialogFragment.DialogFragmentListener,
        CreateCollectionDialogFragment.DialogFragmentListener,
        OnTagCommunicator,
        GetReleaseCommunicator,
        GetReleaseGroupCommunicator,
        GetUrlsCommunicator,
        SetWebViewCommunicator {

    public static final String RELEASE_MBID = "RELEASE_MBID";
    public static final int DEFAULT_RELEASE_NAV_VIEW = R.id.release_nav_tracks;

    private String releaseMbid;
    private Release release;
    private ReleaseGroup releaseGroup;
    private List<Collection> collections;
    private String entityType;

    private FloatingActionButton floatingActionButton;
    private WebView webView;

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            releaseMbid = savedInstanceState.getString(RELEASE_MBID);
        } else {
            releaseMbid = getIntent().getStringExtra(RELEASE_MBID);
        }
        floatingActionButton = findViewById(R.id.floatin_action_btn);
        ((CoordinatorLayout.LayoutParams) floatingActionButton.getLayoutParams()).setBehavior(new FloatingActionButtonBehavior());
        showFloatingActionButton(true, ShowFloatingActionButtonCommunicator.FloatingButtonType.ADD_TO_COLLECTION);
    }

    @Override
    protected int initBottomMenuId() {
        return R.menu.release_bottom_nav;
    }

    @Override
    protected int initDefaultNavViewId() {
        return DEFAULT_RELEASE_NAV_VIEW;
    }

    @Override
    protected BaseFragmentPagerAdapter initBottomNavigationPagerAdapter() {
        return new ReleaseNavigationPagerAdapter(getSupportFragmentManager(), getResources());
    }

    @Override
    protected BottomNavigationView.OnNavigationItemSelectedListener initOnNavigationItemSelectedListener() {
        return item -> {
            frameContainer.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            switch (item.getItemId()) {
                case R.id.release_nav_tracks:
                    viewPager.setCurrentItem(TAB_TRACKS_POS);
                    break;

                case R.id.release_nav_info:
                    viewPager.setCurrentItem(TAB_INFO_POS);
                    break;

                case R.id.release_nav_releases:
                    viewPager.setCurrentItem(TAB_RELEASES_POS);
                    break;

                case R.id.release_nav_ratings:
                    viewPager.setCurrentItem(TAB_RATINGS_POS);
                    break;

                case R.id.release_nav_tags:
                    viewPager.setCurrentItem(TAB_TAGS_POS);
                    break;
            }
            return true;
        };
    }

    @Override
    protected void load() {
        viewError(false);

        viewProgressLoading(true);
        api.getRelease(
                releaseMbid,
                r -> {
                    release = r;
                    api.getReleaseGroup(
                            r.getReleaseGroup().getId(),
                            rg -> {
                                viewProgressLoading(false);
                                releaseGroup = rg;
                                if (!rg.getArtistCredit().isEmpty()) {
                                    Artist.ArtistCredit artistCredit = rg.getArtistCredit().get(0);
                                    topTitle.setText(artistCredit.getName());
                                    topTitle.setOnClickListener(v -> onArtist(artistCredit.getArtist().getId()));
                                }
                                if (!TextUtils.isEmpty(rg.getTitle())) {
                                    bottomTitle.setText(rg.getTitle());
                                }
                                release.setReleaseGroup(rg);
                                configBottomNavigationPager();

                                //todo: сделать асинхронно
                                DatabaseHelper databaseHelper = new DatabaseHelper(this);
                                databaseHelper.setRecommends(rg.getTags());
                                databaseHelper.close();
                            },
                            this::showConnectionWarning
                    );
                },
                this::showConnectionWarning);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(RELEASE_MBID, releaseMbid);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        releaseMbid = savedInstanceState.getString(RELEASE_MBID);
    }

    @Override
    public void setWebView(WebView webView) {
        this.webView = webView;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (webView != null) {
            if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void showFloatingActionButton(boolean visible, FloatingButtonType floatingButtonType) {
        floatingActionButton.setVisibility(visible ? View.VISIBLE : View.GONE);

        if (floatingButtonType != null) {
            floatingActionButton.setImageResource(floatingButtonType.getImgResource());

            switch (floatingButtonType) {
                case ADD_TO_COLLECTION:
                    floatingActionButton.setOnClickListener(v -> {
                        if (!isLoading) {
                            if (oauth.hasAccount()) {
                                String[] entityTypes = {RELEASE_GROUP_ENTITY_TYPE, RELEASE_ENTITY_TYPE};

                                View titleView = getLayoutInflater().inflate(R.layout.layout_custom_alert_dialog_title, null);
                                TextView titleText = titleView.findViewById(R.id.title_text);
                                titleText.setText(R.string.release_collection_selector);

                                new AlertDialog.Builder(this)
                                        .setCustomTitle(titleView)
                                        .setItems(R.array.release_collection_types, (dialog, which) -> onReleaseType(entityTypes[which]))
                                        .show();
                            } else {
                                ActivityFactory.startLoginActivity(this);
                            }
                        }
                    });
                    break;

                default:
                    floatingActionButton.setVisibility(View.GONE);
            }
        }
    }

    public void onReleaseType(String entityType) {
        this.entityType = entityType;
        viewProgressLoading(true);
        api.getCollections(
                collectionBrowse -> {
                    viewProgressLoading(false);
                    collections = new ArrayList<>();
                    if (collectionBrowse.getCount() > 0) {
                        for (Collection collection : collectionBrowse.getCollections()) {
                            if (collection.getEntityType().equals(entityType)) {
                                if (entityType.equals(RELEASE_ENTITY_TYPE)) {
                                    collection.setCount(collection.getReleaseCount());
                                } else {
                                    collection.setCount(collection.getReleaseGroupCount());
                                }
                                collections.add(collection);
                            }
                        }
                        new CollectionsDialogFragment().show(getSupportFragmentManager(), CollectionsDialogFragment.TAG);
                    }
                },
                this::showConnectionWarning,
                100, 0
        );
    }

    @Override
    public void onCollection(String collectionMbid) {
        CollectionServiceInterface.CollectionType collectionType = CollectionServiceInterface.CollectionType.getCollectionType(entityType);
        final String mbid = (collectionType.equals(CollectionServiceInterface.CollectionType.RELEASES)) ? releaseMbid : release.getReleaseGroup().getId();

        viewProgressLoading(true);
        api.addEntityToCollection(
                collectionMbid, collectionType, mbid,
                metadata -> {
                    viewProgressLoading(false);
                    if (metadata.getMessage().getText().equals("OK")) {
                        //todo: snackbar or toast?
                        ShowUtil.showMessage(this, getString(R.string.collection_added));
                    } else {
                        ShowUtil.showMessage(this, "Error");
                    }
                },
                this::showConnectionWarning
        );
    }

    @Override
    public void onCreateCollection(String name, String description, int publ) {
        String collectionType = entityType.equals(RELEASE_ENTITY_TYPE) ? RELEASE_TYPE : RELEASE_GROUP_TYPE;
        viewProgressLoading(true);
        // create collection and add release or album to it
        api.createCollection(
                name, SiteService.getCollectionTypeFromSpinner(collectionType), description, publ,
                responseBody -> {
                    api.getCollections(
                            collectionBrowse -> {
                                String id = "";
                                if (collectionBrowse.getCount() > 0) {
                                    List<Collection> collections = collectionBrowse.getCollections();
                                    for (Collection collection : collections) {
                                        if (collection.getEntityType().equals(entityType) && collection.getName().equals(name)) {
                                            id = collection.getId();
                                            break;
                                        }
                                    }
                                }
                                if (!TextUtils.isEmpty(id)) {
                                    // add entity to collection
                                    onCollection(id);
                                } else {
                                    ShowUtil.showMessage(this, "Error");
                                    viewProgressLoading(false);
                                }
                            },
                            this::showConnectionWarning,
                            100, 0
                    );
                },
                this::showConnectionWarning);
    }

    @Override
    public void showCreateCollection() {
        new CreateCollectionDialogFragment().show(getSupportFragmentManager(), CreateCollectionDialogFragment.TAG);
    }

    @Override
    public List<Collection> getCollections() {
        return collections;
    }

    @Override
    public void onTag(String tag, boolean isGenre) {
        ActivityFactory.startTagActivity(this, tag, TagPagerAdapter.TagTab.RELEASE_GROUP, isGenre);
    }

    @Override
    public void onArtist(String artistMbid) {
        ActivityFactory.startArtistActivity(this, artistMbid);
    }

    @Override
    public void onRelease(String releaseMbid) {
        ActivityFactory.startReleaseActivity(this, releaseMbid);
    }

    @Override
    public void onRecording(String recordingMbid) {
        ActivityFactory.startRecordingActivity(this, recordingMbid);
    }

    @Override
    public String getReleaseMbid() {
        return releaseMbid;
    }

    @Override
    public Release getRelease() {
        return release;
    }

    @Override
    public List<Url> getUrls() {
        if (release != null && release.getReleaseGroup() != null) {
            return new RelationExtractor(release.getReleaseGroup()).getUrls();
        } else {
            return null;
        }
    }

    @Override
    public ReleaseGroup getReleaseGroup() {
        return releaseGroup;
    }

    @Override
    public String getReleaseGroupMbid() {
        return releaseGroup != null ? releaseGroup.getId() : null;
    }
}
