package app.mediabrainz.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.View;

import app.mediabrainz.R;
import app.mediabrainz.adapter.pager.UpdatableFragmentPagerAdapter;
import app.mediabrainz.adapter.pager.UserNavigationPagerAdapter;
import app.mediabrainz.api.model.Collection;
import app.mediabrainz.api.model.Release;
import app.mediabrainz.communicator.GetCollectionCommunicator;
import app.mediabrainz.communicator.GetUsernameCommunicator;
import app.mediabrainz.communicator.OnArtistCommunicator;
import app.mediabrainz.communicator.OnCollectionCommunicator;
import app.mediabrainz.communicator.OnPlayYoutubeCommunicator;
import app.mediabrainz.communicator.OnRecordingCommunicator;
import app.mediabrainz.communicator.OnReleaseCommunicator;
import app.mediabrainz.communicator.OnReleaseGroupCommunicator;
import app.mediabrainz.communicator.OnUserCommunicator;
import app.mediabrainz.communicator.OnUserTagCommunicator;
import app.mediabrainz.communicator.ShowFloatingActionButtonCommunicator;
import app.mediabrainz.dialog.PagedReleaseDialogFragment;
import app.mediabrainz.fragment.AreaCollectionFragment;
import app.mediabrainz.fragment.ArtistCollectionFragment;
import app.mediabrainz.fragment.BaseCollectionFragment;
import app.mediabrainz.fragment.CollectionCreateFragment;
import app.mediabrainz.fragment.CollectionEditFragment;
import app.mediabrainz.fragment.EventCollectionFragment;
import app.mediabrainz.fragment.LabelCollectionFragment;
import app.mediabrainz.fragment.PlaceCollectionFragment;
import app.mediabrainz.fragment.RecordingCollectionFragment;
import app.mediabrainz.fragment.ReleaseCollectionFragment;
import app.mediabrainz.fragment.ReleaseGroupCollectionFragment;
import app.mediabrainz.fragment.SeriesCollectionFragment;
import app.mediabrainz.fragment.UserProfilePagerFragment;
import app.mediabrainz.fragment.UserTagPagerFragment;
import app.mediabrainz.fragment.WorkCollectionFragment;
import app.mediabrainz.intent.ActivityFactory;
import app.mediabrainz.util.FloatingActionButtonBehavior;
import app.mediabrainz.viewModels.UserActivityVM;
import app.mediabrainz.viewModels.UsersVM;
import app.mediabrainz.viewModels.communicator.UsernameCommunicator;

import static app.mediabrainz.MediaBrainzApp.oauth;
import static app.mediabrainz.adapter.pager.UserNavigationPagerAdapter.TAB_COLLECTIONS_POS;
import static app.mediabrainz.adapter.pager.UserNavigationPagerAdapter.TAB_PROFILE_POS;
import static app.mediabrainz.adapter.pager.UserNavigationPagerAdapter.TAB_RATINGS_POS;
import static app.mediabrainz.adapter.pager.UserNavigationPagerAdapter.TAB_RECOMMENDS_POS;
import static app.mediabrainz.adapter.pager.UserNavigationPagerAdapter.TAB_SEND_MESSAGE;
import static app.mediabrainz.adapter.pager.UserNavigationPagerAdapter.TAB_TAGS_POS;
import static app.mediabrainz.api.model.Collection.AREA_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.ARTIST_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.EVENT_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.INSTRUMENT_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.LABEL_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.PLACE_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.RECORDING_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.RELEASE_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.RELEASE_GROUP_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.SERIES_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.WORK_ENTITY_TYPE;
import static app.mediabrainz.viewModels.Status.SUCCESS;


public class UserActivity extends BaseBottomNavActivity implements
        OnPlayYoutubeCommunicator,
        OnReleaseGroupCommunicator,
        OnArtistCommunicator,
        OnReleaseCommunicator,
        OnRecordingCommunicator,
        OnUserTagCommunicator,
        OnCollectionCommunicator,
        OnUserCommunicator,
        GetCollectionCommunicator,
        GetUsernameCommunicator,
        ShowFloatingActionButtonCommunicator,
        UserProfilePagerFragment.UserProfileTabOrdinalCommunicator {

    public static final String TAG = "UserActivity";
    public static final String USERNAME = "UserActivity.USERNAME";
    public static final int DEFAULT_USER_NAV_VIEW = R.id.user_navigation_profile;

    private UserActivityVM userActivityVM;
    private UsersVM usersVM;

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
    protected UpdatableFragmentPagerAdapter initBottomNavigationPagerAdapter() {
        return new UserNavigationPagerAdapter(getSupportFragmentManager(), getResources(), isPrivate);
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        userActivityVM = getViewModel(UserActivityVM.class);
        UsernameCommunicator usernameCommunicator = getViewModel(UsernameCommunicator.class);

        if (userActivityVM.getUsername() == null) {
            username = getIntent().getStringExtra(USERNAME);
            userActivityVM.setUsername(username);

            usernameCommunicator.username.setValue(username);
        } else {
            username = userActivityVM.getUsername();
        }
        usersVM = getViewModel(UsersVM.class);

        isPrivate = oauth.hasAccount() && username.equals(oauth.getName());
        toolbarBottomTitleView.setText(username);

        floatingActionButton = findViewById(R.id.floatingActionButton);
        ((CoordinatorLayout.LayoutParams) floatingActionButton.getLayoutParams()).setBehavior(new FloatingActionButtonBehavior());

        observeData();
    }

    public void observeData() {
        userActivityVM.releasesResource.observeEvent(this, resource -> {
            if (resource == null) return;
            switch (resource.getStatus()) {
                case LOADING:
                    viewProgressLoading(true);
                    break;
                case ERROR:
                    showConnectionWarning(resource.getThrowable());
                    break;
                case SUCCESS:
                    viewProgressLoading(false);
                    Release.ReleaseBrowse releaseBrowse = resource.getData();
                    String releaseGroupMbid = userActivityVM.getReleaseGroupMbid();
                    if (releaseBrowse != null && releaseGroupMbid != null) {
                        // c автоматическим переходом при 1 релизе альбома засчёт предварительной прогрузки релизов альбома
                        if (releaseBrowse.getCount() > 1) {
                            PagedReleaseDialogFragment.newInstance(releaseGroupMbid)
                                    .show(getSupportFragmentManager(), PagedReleaseDialogFragment.TAG);
                        } else if (releaseBrowse.getCount() == 1) {
                            onRelease(releaseBrowse.getReleases().get(0).getId());
                        }
                    }
                    break;
            }
        });

        usersVM.userEvent.observe(this, resource -> {
            if (resource == null || (resource.getStatus() == SUCCESS && resource.getData() == null)) {
                showFloatingActionButton(true, FloatingButtonType.ADD_TO_USERS);
            }
        });

        usersVM.insertEvent.observeEvent(this, resource -> {
            if (resource != null && resource.getStatus() == SUCCESS) {
                showFloatingActionButton(false, null);
                toast(R.string.user_added);
            }
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected BottomNavigationView.OnNavigationItemSelectedListener initOnNavigationItemSelectedListener() {
        return item -> {
            frameContainerView.setVisibility(View.GONE);
            pagerView.setVisibility(View.VISIBLE);
            floatingActionButton.setVisibility(View.GONE);

            switch (item.getItemId()) {
                case R.id.user_navigation_profile:
                    pagerView.setCurrentItem(TAB_PROFILE_POS);
                    toolbarTopTitleView.setText(R.string.title_user_profile);
                    if (!isPrivate && oauth.hasAccount()) {
                        usersVM.find(username);
                    }
                    break;

                case R.id.user_navigation_collections:
                    pagerView.setCurrentItem(TAB_COLLECTIONS_POS);
                    toolbarTopTitleView.setText(R.string.title_user_collections);
                    if (isPrivate) {
                        showFloatingActionButton(true, FloatingButtonType.ADD_TO_COLLECTION);
                    }
                    break;

                case R.id.user_navigation_ratings:
                    pagerView.setCurrentItem(TAB_RATINGS_POS);
                    toolbarTopTitleView.setText(R.string.title_user_ratings);
                    break;

                case R.id.user_navigation_tags:
                    pagerView.setCurrentItem(TAB_TAGS_POS);
                    toolbarTopTitleView.setText(R.string.title_tags_genres);
                    break;

                case R.id.user_navigation_recommends:
                    pagerView.setCurrentItem(TAB_RECOMMENDS_POS);
                    toolbarTopTitleView.setText(R.string.title_user_recommends);
                    break;

                case R.id.user_navigation_send_email:
                    pagerView.setCurrentItem(TAB_SEND_MESSAGE);
                    toolbarTopTitleView.setText(R.string.title_send_message);
                    break;
            }
            return true;
        };
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frameContainerView);
        if (fragment instanceof BaseCollectionFragment || fragment instanceof CollectionCreateFragment) {
            bottomNavView.setSelectedItemId(R.id.user_navigation_collections);
        } else if (fragment instanceof UserTagPagerFragment) {
            bottomNavView.setSelectedItemId(R.id.user_navigation_tags);
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
    public void onRelease(String releaseMbid) {
        ActivityFactory.startReleaseActivity(this, releaseMbid);
    }

    @Override
    public void onUserTag(String username, String tag) {
        loadFragment(UserTagPagerFragment.newInstance(username, tag));
        getToolbarTopTitleView().setText(tag);
    }

    @Override
    public void onCollection(Collection collection) {
        if (!isLoading) {
            this.collection = collection;
            switch (collection.getEntityType()) {
                case ARTIST_ENTITY_TYPE:
                    loadFragment(ArtistCollectionFragment.newInstance());
                    break;
                case AREA_ENTITY_TYPE:
                    loadFragment(AreaCollectionFragment.newInstance());
                    break;
                case EVENT_ENTITY_TYPE:
                    loadFragment(EventCollectionFragment.newInstance());
                    break;
                case INSTRUMENT_ENTITY_TYPE:
                    //loadFragment(InstrumentCollectionFragment.newInstance());
                    break;
                case LABEL_ENTITY_TYPE:
                    loadFragment(LabelCollectionFragment.newInstance());
                    break;
                case PLACE_ENTITY_TYPE:
                    loadFragment(PlaceCollectionFragment.newInstance());
                    break;
                case RECORDING_ENTITY_TYPE:
                    loadFragment(RecordingCollectionFragment.newInstance());
                    break;
                case RELEASE_ENTITY_TYPE:
                    loadFragment(ReleaseCollectionFragment.newInstance());
                    break;
                case RELEASE_GROUP_ENTITY_TYPE:
                    loadFragment(ReleaseGroupCollectionFragment.newInstance());
                    break;
                case SERIES_ENTITY_TYPE:
                    loadFragment(SeriesCollectionFragment.newInstance());
                    break;
                case WORK_ENTITY_TYPE:
                    loadFragment(WorkCollectionFragment.newInstance());
                    break;
            }
        }
    }

    @Override
    public Collection getCollection() {
        return collection;
    }

    @Override
    public String getCollectionMbid() {
        return collection.getId();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void showFloatingActionButton(boolean visible, FloatingButtonType floatingButtonType) {
        floatingActionButton.setVisibility(visible ? View.VISIBLE : View.GONE);

        if (floatingButtonType != null) {
            floatingActionButton.setImageResource(floatingButtonType.getImgResource());

            switch (floatingButtonType) {
                case ADD_TO_USERS:
                    floatingActionButton.setOnClickListener(v -> usersVM.insert(username));
                    break;
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
    public void onUser(String username) {
        ActivityFactory.startUserActivity(this, username);
    }

    @Override
    public int getUserProfileTabOrdinal() {
        return getFragmentViewId();
    }

    @Override
    public void onPlay(String keyword) {
        ActivityFactory.startYoutubeSearchActivity(this, keyword);
    }

    @Override
    public void onReleaseGroup(String releaseGroupMbid) {
        if (!isLoading) {
            // c автоматическим переходом при 1 релизе альбома засчёт предварительной прогрузки релизов альбома
            userActivityVM.loadReleases(releaseGroupMbid);
            // без автоматического перехода при 1 релизе альбома
            //PagedReleaseDialogFragment.newInstance(releaseGroupMbid).show(getSupportFragmentManager(), PagedReleaseDialogFragment.TAG);
        }
    }
}
