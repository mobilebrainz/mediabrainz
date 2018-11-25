package app.mediabrainz.activity;

import android.os.Bundle;
import android.view.View;

import app.mediabrainz.R;
import app.mediabrainz.fragment.SearchFragment;
import app.mediabrainz.fragment.SelectedSearchFragment;
import app.mediabrainz.intent.ActivityFactory;

import java.util.ArrayList;
import java.util.List;

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.MediaBrainzApp.oauth;


public class MainActivity extends BaseActivity implements
        SearchFragment.SearchFragmentListener,
        SelectedSearchFragment.SelectedSearchFragmentListener {

    private List<String> genres = new ArrayList<>();
    private boolean isLoading;
    private boolean isError;

    private View contentView;
    private View errorView;
    protected View loading;
    private View logInBtn;

    @Override
    protected int initContentLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contentView = findViewById(R.id.content);
        logInBtn = findViewById(R.id.log_in_btn);
        errorView = findViewById(R.id.error);
        loading = findViewById(R.id.loading);

        if (!oauth.hasAccount()) {
            logInBtn.setVisibility(View.VISIBLE);
            logInBtn.setOnClickListener(v -> {
                if (!isLoading && !isError) {
                    ActivityFactory.startLoginActivity(this);
                }
            });
        }
        load();
    }

    private void load() {
        viewError(false);

        viewProgressLoading(true);
        api.getGenres(g -> {
                    viewProgressLoading(false);
                    genres = g;
                },
                this::showConnectionWarning);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (oauth.hasAccount()) {
            logInBtn.setVisibility(View.GONE);
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
        errorView.findViewById(R.id.retry_button).setOnClickListener(v -> load());
    }

    @Override
    public List<String> getGenres() {
        return genres;
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
            loading.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            contentView.setAlpha(1.0F);
            loading.setVisibility(View.GONE);
        }
    }

}
