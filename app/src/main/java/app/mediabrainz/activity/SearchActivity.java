package app.mediabrainz.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.R;
import app.mediabrainz.fragment.SearchFragment;
import app.mediabrainz.fragment.SelectedSearchFragment;
import app.mediabrainz.intent.ActivityFactory;

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.MediaBrainzApp.oauth;


public class SearchActivity extends BaseActivity implements
        SearchFragment.SearchFragmentListener,
        SelectedSearchFragment.SelectedSearchFragmentListener {

    private boolean isLoading;
    private boolean isError;

    private View contentView;
    private View errorView;
    protected View progressView;
    private View logInButton;

    @Override
    protected int initContentLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contentView = findViewById(R.id.contentView);
        logInButton = findViewById(R.id.logInButton);
        errorView = findViewById(R.id.errorView);
        progressView = findViewById(R.id.progressView);

        if (!oauth.hasAccount()) {
            logInButton.setVisibility(View.VISIBLE);
            logInButton.setOnClickListener(v -> {
                if (!isLoading && !isError) {
                    ActivityFactory.startLoginActivity(this);
                }
            });
        }

        if (checkNetworkConnection()) {
            load();
        } else {
            viewError(true);
            errorView.findViewById(R.id.retryButton).setOnClickListener(v -> load());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_top_nav, menu);
        return true;
    }

    private void load() {
        viewError(false);

        if (MediaBrainzApp.getGenres().isEmpty()) {
            viewProgressLoading(true);
            api.getGenres(g -> {
                        viewProgressLoading(false);
                        MediaBrainzApp.setGenres(g);
                    },
                    this::showConnectionWarning);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (oauth.hasAccount()) {
            logInButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void searchEntity(String artist, String album, String track) {
        if (!isLoading && !isError) {
            ActivityFactory.startSearchActivity(this, artist, album, track);
        }
    }

    @Override
    public void searchType(SearchType searchType, String query) {
        if (!isLoading && !isError) {
            ActivityFactory.startSearchActivity(this, query, searchType);
        }
    }

    protected void showConnectionWarning(Throwable t) {
        viewProgressLoading(false);
        viewError(true);
        errorView.findViewById(R.id.retryButton).setOnClickListener(v -> load());
    }

    protected void viewError(boolean isView) {
        if (isView) {
            isError = true;
            contentView.setVisibility(View.INVISIBLE);
            errorView.setVisibility(View.VISIBLE);
        } else {
            isError = false;
            errorView.setVisibility(View.GONE);
            contentView.setVisibility(View.VISIBLE);
        }
    }

    protected void viewProgressLoading(boolean isView) {
        if (isView) {
            isLoading = true;
            contentView.setAlpha(0.25F);
            progressView.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            contentView.setAlpha(1.0F);
            progressView.setVisibility(View.GONE);
        }
    }

}
