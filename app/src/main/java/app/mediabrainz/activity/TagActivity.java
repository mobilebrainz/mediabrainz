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


public class TagActivity extends BaseActivity implements
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

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private View error;
    private View loading;

    @Override
    protected int initContentLayout() {
        return R.layout.activity_tag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewPager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tabs);
        error = findViewById(R.id.error);
        loading = findViewById(R.id.loading);

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
        TextView topTitle = findViewById(R.id.toolbar_title_top);
        TextView bottomTitle = findViewById(R.id.toolbar_title_bottom);
        topTitle.setText(isGenre ? R.string.genre_title : R.string.tag_title);
        bottomTitle.setText(tag);

        configurePager();
    }

    private void configurePager() {
        TagPagerAdapter pagerAdapter = new TagPagerAdapter(getSupportFragmentManager(), getResources());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(pagerAdapter.getCount());
        tabLayout.setupWithViewPager(viewPager);
        pagerAdapter.setupTabViews(tabLayout);
        viewPager.setCurrentItem(tagTabOrdianal);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_GENRE, isGenre);
        outState.putString(MB_TAG, tag);
        outState.putInt(TAG_TAB_ORDINAL, tagTabOrdianal);
        outState.putInt(PAGER_POSITION, tabLayout.getSelectedTabPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isGenre = savedInstanceState.getBoolean(IS_GENRE);
        tag = savedInstanceState.getString(MB_TAG);
        tagTabOrdianal = savedInstanceState.getInt(TAG_TAB_ORDINAL, 0);
        viewPager.setCurrentItem(savedInstanceState.getInt(PAGER_POSITION));
    }

    private void viewProgressLoading(boolean isView) {
        if (isView) {
            isLoading = true;
            viewPager.setAlpha(0.3F);
            loading.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            viewPager.setAlpha(1.0F);
            loading.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean isView) {
        if (isView) {
            isError = true;
            viewPager.setVisibility(View.INVISIBLE);
            error.setVisibility(View.VISIBLE);
        } else {
            isError = false;
            error.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
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
                    ShowUtil.showError(this, t);
                },
                2, 0);
    }

    @Override
    public void onRelease(String releaseMbid) {
        ActivityFactory.startReleaseActivity(this, releaseMbid);
    }

}
