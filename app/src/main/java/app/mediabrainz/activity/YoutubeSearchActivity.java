package app.mediabrainz.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.YoutubeSearchAdapter;
import app.mediabrainz.api.externalResources.youtube.model.YoutubeSearchResult;
import app.mediabrainz.communicator.LoadingCommunicator;
import app.mediabrainz.dialog.LocalSettingsDialogFragment;
import app.mediabrainz.intent.ActivityFactory;

import static app.mediabrainz.MediaBrainzApp.YOUTUBE_API_KEY;
import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.dialog.LocalSettingsDialogFragment.LocalSettingsType.YOUTUBE_SETTINGS;

public class YoutubeSearchActivity extends BaseActivity implements
        LoadingCommunicator {

    public static final String KEYWORD = "KEYWORD";

    private String keyword;
    private boolean isLoading;
    private boolean isError;

    private View contentView;
    private RecyclerView searchRecyclerView;
    private View errorView;
    private View progressView;
    private View noresultsView;

    @Override
    protected int initContentLayout() {
        return R.layout.activity_result_search;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        contentView = findViewById(R.id.contentView);
        errorView = findViewById(R.id.errorView);
        progressView = findViewById(R.id.progressView);
        noresultsView = findViewById(R.id.noresultsView);

        if (savedInstanceState != null) {
            keyword = savedInstanceState.getString(KEYWORD);
        } else {
            keyword = getIntent().getStringExtra(KEYWORD);
        }

        TextView toolbarTopTitleView = findViewById(R.id.toolbarTopTitleView);
        TextView toolbarBottomTitleView = findViewById(R.id.toolbarBottomTitleView);
        toolbarTopTitleView.setText(R.string.youtube_search);
        toolbarBottomTitleView.setText(keyword);

        configSearchRecycler();
        search();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEYWORD, keyword);
    }

    @Override
    protected int getOptionsMenu() {
        return R.menu.youtube_top_nav;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_settings:
                LocalSettingsDialogFragment.newInstance(YOUTUBE_SETTINGS.ordinal()).show(getSupportFragmentManager(), LocalSettingsDialogFragment.TAG);
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void configSearchRecycler() {
        searchRecyclerView = findViewById(R.id.searchRecyclerView);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchRecyclerView.setItemViewCacheSize(50);
        searchRecyclerView.setDrawingCacheEnabled(true);
        searchRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        searchRecyclerView.setHasFixedSize(true);
    }

    private void search() {
        noresultsView.setVisibility(View.GONE);
        viewError(false);
        viewProgressLoading(true);

        api.searchYoutube(keyword, YOUTUBE_API_KEY,
                youtubeSearchListResponse -> {
                    List<YoutubeSearchResult> items = youtubeSearchListResponse.getItems();
                    if (!items.isEmpty()) {
                        YoutubeSearchAdapter adapter = new YoutubeSearchAdapter(items);
                        searchRecyclerView.setAdapter(adapter);
                        adapter.setHolderClickListener(position -> {
                            String videoId = items.get(position).getId().getVideoId();
                            if (MediaBrainzApp.getPreferences().isPlayYoutube()) {
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId)));
                                } catch (ActivityNotFoundException ex) {
                                    //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoId)));
                                    ActivityFactory.startYoutubeActivity(this, videoId);
                                }
                            } else {
                                //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoId)));
                                ActivityFactory.startYoutubeActivity(this, videoId);
                            }
                        });
                    } else {
                        noresultsView.setVisibility(View.VISIBLE);
                    }
                    viewProgressLoading(false);
                },
                this::showConnectionWarning);
    }

    @Override
    public void showConnectionWarning(Throwable t) {
        //ShowUtil.showError(this, t);
        viewProgressLoading(false);
        viewError(true);
        errorView.findViewById(R.id.retryButton).setOnClickListener(v -> search());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        searchRecyclerView.getRecycledViewPool().clear();
        searchRecyclerView.setAdapter(null);
    }

    @Override
    public void viewProgressLoading(boolean isView) {
        if (isView) {
            isLoading = true;
            contentView.setAlpha(0.3F);
            searchRecyclerView.setAlpha(0.3F);
            progressView.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            contentView.setAlpha(1.0F);
            searchRecyclerView.setAlpha(1.0F);
            progressView.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean isView) {
        if (isView) {
            isError = true;
            searchRecyclerView.setVisibility(View.INVISIBLE);
            contentView.setVisibility(View.INVISIBLE);
            errorView.setVisibility(View.VISIBLE);
        } else {
            isError = false;
            errorView.setVisibility(View.GONE);
            contentView.setVisibility(View.VISIBLE);
            searchRecyclerView.setVisibility(View.VISIBLE);
        }
    }

}
