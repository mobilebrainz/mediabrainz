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

    private View content;
    private RecyclerView searchRecycler;
    private View error;
    private View loading;
    private View noresults;

    @Override
    protected int initContentLayout() {
        return R.layout.activity_result_search;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        content = findViewById(R.id.content);
        error = findViewById(R.id.error);
        loading = findViewById(R.id.loading);
        noresults = findViewById(R.id.noresults);

        if (savedInstanceState != null) {
            keyword = savedInstanceState.getString(KEYWORD);
        } else {
            keyword = getIntent().getStringExtra(KEYWORD);
        }

        TextView topTitle = findViewById(R.id.toolbar_title_top);
        TextView bottomTitle = findViewById(R.id.toolbar_title_bottom);
        topTitle.setText(R.string.youtube_search);
        bottomTitle.setText(keyword);

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
        searchRecycler = findViewById(R.id.search_recycler);
        searchRecycler.setLayoutManager(new LinearLayoutManager(this));
        searchRecycler.setItemViewCacheSize(50);
        searchRecycler.setDrawingCacheEnabled(true);
        searchRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        searchRecycler.setHasFixedSize(true);
    }

    private void search() {
        noresults.setVisibility(View.GONE);
        viewError(false);
        viewProgressLoading(true);

        api.searchYoutube(keyword, YOUTUBE_API_KEY,
                youtubeSearchListResponse -> {
                    List<YoutubeSearchResult> items = youtubeSearchListResponse.getItems();
                    if (!items.isEmpty()) {
                        YoutubeSearchAdapter adapter = new YoutubeSearchAdapter(items);
                        searchRecycler.setAdapter(adapter);
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
                        noresults.setVisibility(View.VISIBLE);
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
        error.findViewById(R.id.retry_button).setOnClickListener(v -> search());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        searchRecycler.getRecycledViewPool().clear();
        searchRecycler.setAdapter(null);
    }

    @Override
    public void viewProgressLoading(boolean isView) {
        if (isView) {
            isLoading = true;
            content.setAlpha(0.3F);
            searchRecycler.setAlpha(0.3F);
            loading.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            content.setAlpha(1.0F);
            searchRecycler.setAlpha(1.0F);
            loading.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean isView) {
        if (isView) {
            isError = true;
            searchRecycler.setVisibility(View.INVISIBLE);
            content.setVisibility(View.INVISIBLE);
            error.setVisibility(View.VISIBLE);
        } else {
            isError = false;
            error.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
            searchRecycler.setVisibility(View.VISIBLE);
        }
    }

}
