package app.mediabrainz.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;

import app.mediabrainz.R;
import app.mediabrainz.adapter.pager.BaseFragmentPagerAdapter;
import app.mediabrainz.adapter.pager.CollectionsPagerAdapter;
import app.mediabrainz.adapter.pager.UserNavigationPagerAdapter;
import app.mediabrainz.api.model.Collection;
import app.mediabrainz.api.site.SiteService;
import app.mediabrainz.communicator.GetCollectionCommunicator;
import app.mediabrainz.communicator.GetUsernameCommunicator;
import app.mediabrainz.communicator.OnArtistCommunicator;
import app.mediabrainz.communicator.OnCollectionCommunicator;
import app.mediabrainz.communicator.OnCreateCollectionCommunicator;
import app.mediabrainz.communicator.OnEditCollectionCommunicator;
import app.mediabrainz.communicator.OnRecordingCommunicator;
import app.mediabrainz.communicator.OnReleaseCommunicator;
import app.mediabrainz.communicator.OnReleaseGroupCommunicator;
import app.mediabrainz.communicator.OnUserTagCommunicator;
import app.mediabrainz.communicator.ShowFloatingActionButtonCommunicator;
import app.mediabrainz.dialog.PagedReleaseDialogFragment;
import app.mediabrainz.fragment.CollectionCreateFragment;
import app.mediabrainz.fragment.CollectionEditFragment;
import app.mediabrainz.fragment.CollectionFragment;
import app.mediabrainz.fragment.CollectionsPagerFragment;
import app.mediabrainz.fragment.UserTagPagerFragment;
import app.mediabrainz.intent.ActivityFactory;
import app.mediabrainz.util.FloatingActionButtonBehavior;
import app.mediabrainz.util.ShowUtil;

import java.util.List;

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.MediaBrainzApp.oauth;
import static app.mediabrainz.adapter.pager.UserNavigationPagerAdapter.TAB_COLLECTIONS_POS;
import static app.mediabrainz.adapter.pager.UserNavigationPagerAdapter.TAB_PROFILE_POS;
import static app.mediabrainz.adapter.pager.UserNavigationPagerAdapter.TAB_RATINGS_POS;
import static app.mediabrainz.adapter.pager.UserNavigationPagerAdapter.TAB_RECOMMENDS_POS;
import static app.mediabrainz.adapter.pager.UserNavigationPagerAdapter.TAB_TAGS_POS;


public class UserActivity extends BaseBottomNavActivity implements
        GetUsernameCommunicator,
        OnArtistCommunicator,
        OnReleaseGroupCommunicator,
        OnReleaseCommunicator,
        OnRecordingCommunicator,
        OnUserTagCommunicator,
        OnCollectionCommunicator,
        GetCollectionCommunicator,
        OnCreateCollectionCommunicator,
        ShowFloatingActionButtonCommunicator,
        OnEditCollectionCommunicator,
        CollectionsPagerFragment.CollectionTabOrdinalCommunicator {

    public static final String USERNAME = "USERNAME";
    public static final int DEFAULT_USER_NAV_VIEW = R.id.user_navigation_profile;

    private int collectionTabOrdinal = -1;
    private String username;
    private Collection collection;
    private boolean isPrivate;

    private FloatingActionButton floatingActionButton;

    @Override
    protected int initBottomMenuId() {
        return isPrivate ? R.menu.private_user_bottom_nav : R.menu.user_bottom_nav;
    }

    @Override
    protected int initDefaultNavViewId() {
        return DEFAULT_USER_NAV_VIEW;
    }

    @Override
    protected BaseFragmentPagerAdapter initBottomNavigationPagerAdapter() {
        return new UserNavigationPagerAdapter(getSupportFragmentManager(), getResources());
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            username = savedInstanceState.getString(USERNAME);
        } else {
            username = getIntent().getStringExtra(USERNAME);
        }
        isPrivate = oauth.hasAccount() && username.equals(oauth.getName());
        bottomTitle.setText(username);

        floatingActionButton = findViewById(R.id.floatin_action_btn);
        ((CoordinatorLayout.LayoutParams) floatingActionButton.getLayoutParams()).setBehavior(new FloatingActionButtonBehavior());
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected BottomNavigationView.OnNavigationItemSelectedListener initOnNavigationItemSelectedListener() {
        return item -> {
            frameContainer.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            floatingActionButton.setVisibility(View.GONE);

            switch (item.getItemId()) {
                case R.id.user_navigation_profile:
                    viewPager.setCurrentItem(TAB_PROFILE_POS);
                    topTitle.setText(R.string.title_user_profile);
                    break;

                case R.id.user_navigation_collections:
                    viewPager.setCurrentItem(TAB_COLLECTIONS_POS);
                    topTitle.setText(R.string.title_user_collections);
                    if (isPrivate) {
                        showFloatingActionButton(true, FloatingButtonType.ADD_TO_COLLECTION);
                    }
                    break;

                case R.id.user_navigation_ratings:
                    viewPager.setCurrentItem(TAB_RATINGS_POS);
                    topTitle.setText(R.string.title_user_ratings);
                    break;

                case R.id.user_navigation_tags:
                    viewPager.setCurrentItem(TAB_TAGS_POS);
                    topTitle.setText(R.string.title_tags_genres);
                    break;

                case R.id.user_navigation_recommends:
                    viewPager.setCurrentItem(TAB_RECOMMENDS_POS);
                    topTitle.setText(R.string.title_user_recommends);
                    break;
            }
            return true;
        };
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(USERNAME, username);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        username = savedInstanceState.getString(USERNAME);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
        if (fragment instanceof CollectionFragment || fragment instanceof CollectionCreateFragment) {
            bottomNavigationView.setSelectedItemId(R.id.user_navigation_collections);
        } else if (fragment instanceof UserTagPagerFragment) {
            bottomNavigationView.setSelectedItemId(R.id.user_navigation_tags);
        }
        super.onBackPressed();
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void onArtist(String artistMbid) {
        ActivityFactory.startArtistActivity(this, artistMbid);
    }

    @Override
    public void onRecording(String recordingMbid) {
        ActivityFactory.startRecordingActivity(this, recordingMbid);
    }

    @Override
    public void onReleaseGroup(String releaseGroupMbid) {
        if (!isLoading) {
            // c автоматическим переходом при 1 релизе альбома засчёт предварительной прогрузки релизов альбома
            showReleases(releaseGroupMbid);
            // без автоматического перехода при 1 релизе альбома
            //PagedReleaseDialogFragment.newInstance(releaseGroupMbid).show(getSupportFragmentManager(), PagedReleaseDialogFragment.TAG);
        }
    }

    private void showReleases(String releaseGroupMbid) {
        viewProgressLoading(true);
        api.getReleasesByAlbum(
                releaseGroupMbid,
                releaseBrowse -> {
                    viewProgressLoading(false);
                    if (releaseBrowse.getCount() > 1) {
                        PagedReleaseDialogFragment.newInstance(releaseGroupMbid).show(getSupportFragmentManager(), PagedReleaseDialogFragment.TAG);
                    } else if (releaseBrowse.getCount() == 1) {
                        onRelease(releaseBrowse.getReleases().get(0).getId());
                    }
                },
                t -> {
                    viewProgressLoading(false);
                    ShowUtil.showError(this, t);
                },
                2, 0);
    }

    @Override
    public void onRelease(String releaseMbid) {
        ActivityFactory.startReleaseActivity(this, releaseMbid);
    }

    @Override
    public void onUserTag(String username, String tag) {
        loadFragment(UserTagPagerFragment.newInstance(username, tag));
    }

    @Override
    public void onCollection(Collection collection) {
        if (!isLoading) {
            this.collection = collection;
            loadFragment(CollectionFragment.newInstance());
        }
    }

    @Override
    public Collection getCollection() {
        return collection;
    }

    @Override
    public String getCollectionMbid() {
        return null;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void showFloatingActionButton(boolean visible, FloatingButtonType floatingButtonType) {
        floatingActionButton.setVisibility(visible ? View.VISIBLE : View.GONE);

        if (floatingButtonType != null) {
            floatingActionButton.setImageResource(floatingButtonType.getImgResource());

            switch (floatingButtonType) {
                case ADD_TO_COLLECTION:
                    floatingActionButton.setOnClickListener(v -> loadFragment(CollectionCreateFragment.newInstance()));
                    break;
                case EDIT_COLLECTION:
                    floatingActionButton.setOnClickListener(v -> loadFragment(CollectionEditFragment.newInstance()));
                    break;
                default:
                    floatingActionButton.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onCreateCollection(String name, int type, String description, int publ, EditText editText) {
        if (!isLoading) {
            viewProgressLoading(true);
            api.getCollections(
                    collectionBrowse -> {
                        boolean existName = false;
                        if (collectionBrowse.getCount() > 0) {
                            List<Collection> collections = collectionBrowse.getCollections();
                            for (Collection collection : collections) {
                                if (collection.getName().equalsIgnoreCase(name) &&
                                        collection.getType().equalsIgnoreCase(SiteService.getCollectionTypeFromSpinner(type - 1))) {
                                    viewProgressLoading(false);
                                    String errorString = getString(R.string.collection_create_exist_name);
                                    if (editText != null) {
                                        editText.setError(errorString);
                                    } else {
                                        ShowUtil.showToast(this, errorString);
                                    }
                                    existName = true;
                                    break;
                                }
                            }
                        }
                        if (!existName) {
                            api.createCollection(name, type, description, publ,
                                    responseBody -> {
                                        viewProgressLoading(false);
                                        collectionTabOrdinal = CollectionsPagerAdapter.collectionTabTypeSpinner[type - 1].ordinal();
                                        ((BaseFragmentPagerAdapter.Updatable) getBottomNavigationPagerAdapter().getFragment(TAB_COLLECTIONS_POS)).update();
                                        bottomNavigationView.setSelectedItemId(R.id.user_navigation_collections);
                                    },
                                    t -> {
                                        viewProgressLoading(false);
                                        ShowUtil.showError(this, t);
                                    });
                        }
                    },
                    t -> {
                        viewProgressLoading(false);
                        ShowUtil.showError(this, t);
                    },
                    100, 0);
        }
    }

    @Override
    public void onEditCollection(String name, int type, String description, int isPublic) {
        if (!isLoading) {
            viewProgressLoading(true);
            api.editCollection(collection, name, type, description, isPublic,
                    responseBody -> {
                        viewProgressLoading(false);
                        collectionTabOrdinal = CollectionsPagerAdapter.collectionTabTypeSpinner[type - 1].ordinal();
                        ((BaseFragmentPagerAdapter.Updatable) getBottomNavigationPagerAdapter().getFragment(TAB_COLLECTIONS_POS)).update();
                        bottomNavigationView.setSelectedItemId(R.id.user_navigation_collections);
                    },
                    t -> {
                        viewProgressLoading(false);
                        ShowUtil.showError(this, t);
                    });
        }
    }

    @Override
    public int getCollectionTabOrdinal() {
        return collectionTabOrdinal;
    }
}
