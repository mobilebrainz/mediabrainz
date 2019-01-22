package app.mediabrainz.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import app.mediabrainz.R;
import app.mediabrainz.adapter.pager.TagPagerAdapter;
import app.mediabrainz.communicator.GetTagCommunicator;
import app.mediabrainz.communicator.OnArtistCommunicator;
import app.mediabrainz.communicator.OnRecordingCommunicator;
import app.mediabrainz.communicator.OnReleaseCommunicator;
import app.mediabrainz.communicator.OnReleaseGroupCommunicator;
import app.mediabrainz.dialog.PagedReleaseDialogFragment;
import app.mediabrainz.intent.ActivityFactory;
import app.mediabrainz.util.ShowUtil;

import static app.mediabrainz.MediaBrainzApp.api;


public class TagActivity extends BaseNavigationActivity implements
        OnArtistCommunicator,
        OnReleaseGroupCommunicator,
        OnRecordingCommunicator,
        OnReleaseCommunicator,
        GetTagCommunicator {

    public static final String PAGER_POSITION = "PAGER_POSITION";
    public static final String MB_TAG = "MB_TAG";
    public static final String TAG_TAB_ORDINAL = "TAG_TAB_ORDINAL";
    public static final String IS_GENRE = "IS_GENRE";

    private boolean isGenre;
    private String tag;
    private int tagTabOrdianal;
    private boolean isLoading;
    private boolean isError;

    private ViewPager pagerView;
    private TabLayout tabsView;
    private View errorView;
    private View progressView;

    @Override
    protected int initContentLayout() {
        return R.layout.activity_tag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pagerView = findViewById(R.id.pagerView);
        tabsView = findViewById(R.id.tabsView);
        errorView = findViewById(R.id.errorView);
        progressView = findViewById(R.id.progressView);

        if (savedInstanceState != null) {
            isGenre = savedInstanceState.getBoolean(IS_GENRE);
            tag = savedInstanceState.getString(MB_TAG);
            tagTabOrdianal = savedInstanceState.getInt(TAG_TAB_ORDINAL);
        } else {
            isGenre = getIntent().getBooleanExtra(IS_GENRE, false);
            tag = getIntent().getStringExtra(MB_TAG);
            tagTabOrdianal = getIntent().getIntExtra(TAG_TAB_ORDINAL, 0);
        }

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbarTopTitleView = findViewById(R.id.toolbarTopTitleView);
        TextView toolbarBottomTitleView = findViewById(R.id.toolbarBottomTitleView);
        toolbarTopTitleView.setText(isGenre ? R.string.genre_title : R.string.tag_title);
        toolbarBottomTitleView.setText(tag);

        configurePager();
    }

    private void configurePager() {
        TagPagerAdapter pagerAdapter = new TagPagerAdapter(getSupportFragmentManager(), getResources());
        pagerView.setAdapter(pagerAdapter);
        pagerView.setOffscreenPageLimit(pagerAdapter.getCount());
        tabsView.setupWithViewPager(pagerView);
        pagerAdapter.setupTabViews(tabsView);
        pagerView.setCurrentItem(tagTabOrdianal);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_GENRE, isGenre);
        outState.putString(MB_TAG, tag);
        outState.putInt(TAG_TAB_ORDINAL, tagTabOrdianal);
        outState.putInt(PAGER_POSITION, tabsView.getSelectedTabPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isGenre = savedInstanceState.getBoolean(IS_GENRE);
        tag = savedInstanceState.getString(MB_TAG);
        tagTabOrdianal = savedInstanceState.getInt(TAG_TAB_ORDINAL, 0);
        pagerView.setCurrentItem(savedInstanceState.getInt(PAGER_POSITION));
    }

    private void viewProgressLoading(boolean isView) {
        if (isView) {
            isLoading = true;
            pagerView.setAlpha(0.3F);
            progressView.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            pagerView.setAlpha(1.0F);
            progressView.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean isView) {
        if (isView) {
            isError = true;
            pagerView.setVisibility(View.INVISIBLE);
            errorView.setVisibility(View.VISIBLE);
        } else {
            isError = false;
            errorView.setVisibility(View.GONE);
            pagerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public String getTag() {
        return tag;
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
                    toast("Error");
                },
                2, 0);
    }

    @Override
    public void onRelease(String releaseMbid) {
        ActivityFactory.startReleaseActivity(this, releaseMbid);
    }

}
