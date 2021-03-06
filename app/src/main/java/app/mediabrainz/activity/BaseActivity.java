package app.mediabrainz.activity;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.R;
import app.mediabrainz.adapter.SuggestionListAdapter;
import app.mediabrainz.adapter.pager.UserProfilePagerAdapter;
import app.mediabrainz.apihandler.Api;
import app.mediabrainz.data.room.entity.Suggestion;
import app.mediabrainz.intent.ActivityFactory;
import app.mediabrainz.intent.zxing.IntentIntegrator;
import app.mediabrainz.util.MbUtils;

import static app.mediabrainz.MediaBrainzApp.SUPPORT_MAIL;
import static app.mediabrainz.MediaBrainzApp.oauth;



public abstract class BaseActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SearchView.OnQueryTextListener {

    private final int DEFAULT_OPTIONS_MENU = R.menu.base_top_nav;

    protected DrawerLayout drawerView;
    protected NavigationView navView;
    protected Toolbar toolbarView;

    abstract protected int initContentLayout();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(initContentLayout());

        toolbarView = findViewById(R.id.toolbarView);
        setSupportActionBar(toolbarView);

        navView = findViewById(R.id.navView);
        navView.setNavigationItemSelectedListener(this);

        drawerView = findViewById(R.id.drawerView);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerView,
                toolbarView,
                R.string.nav_open_drawer,
                R.string.nav_close_drawer);
        drawerView.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (oauth.hasAccount()) {
            if (navView.getMenu().findItem(R.id.nav_user_logout) == null) {
                navView.getMenu().clear();
                navView.inflateMenu(R.menu.drawer_nav);
            }
        } else {
            if (navView.getMenu().findItem(R.id.nav_user_login) == null) {
                navView.getMenu().clear();
                navView.inflateMenu(R.menu.guest_drawer_nav);
            }
        }
    }

    protected boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isNetworkConnected = (networkInfo != null && networkInfo.isConnected());
        if (!isNetworkConnected) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.error_connect_title)
                    .setMessage(R.string.error_connect_message)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert).show();
        }
        return isNetworkConnected;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_search:
                ActivityFactory.startMainActivity(this);
                break;
            case R.id.nav_scan_barcode:
                IntentIntegrator.initiateScan(this, getString(R.string.zx_title), getString(R.string.zx_message),
                        getString(R.string.zx_pos), getString(R.string.zx_neg), IntentIntegrator.PRODUCT_CODE_TYPES);
                break;
            case R.id.nav_settings:
                ActivityFactory.startSettingsActivity(this);
                break;
            case R.id.nav_user_login:
                ActivityFactory.startLoginActivity(this);
                break;

            // User nav:
            case R.id.nav_user_profile:
                startUserActivity(R.id.user_navigation_profile);
                break;
            case R.id.nav_user_collections:
                startUserActivity(R.id.user_navigation_collections);
                break;
            case R.id.nav_user_ratings:
                startUserActivity(R.id.user_navigation_ratings);
                break;
            case R.id.nav_user_tags:
                startUserActivity(R.id.user_navigation_tags);
                break;
            case R.id.nav_user_recommends:
                startUserActivity(R.id.user_navigation_recommends);
                break;
            case R.id.nav_user_users:
                startUserActivity(R.id.user_navigation_profile, UserProfilePagerAdapter.TAB_USERS_POS);
                break;

            case R.id.nav_user_logout:
                //todo: add confirm dialog?
                oauth.logOut();
                ActivityFactory.startMainActivity(this);
                break;

            // Support:
            case R.id.nav_feedback:
                sendEmail();
                break;
            case R.id.nav_about:
                ActivityFactory.startAboutActivity(this);
                break;
        }
        drawerView.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sendEmail() {
        try {
            startActivity(Intent.createChooser(
                    MbUtils.emailIntent(SUPPORT_MAIL, Api.CLIENT), getString(R.string.choose_email_client)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, getString(R.string.send_mail_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void startUserActivity(int userNavigationView) {
        if (oauth.hasAccount()) {
            ActivityFactory.startUserActivity(this, oauth.getName(), userNavigationView);
        } else {
            ActivityFactory.startLoginActivity(this);
        }
    }

    private void startUserActivity(int userNavigationView, int userFragmentView) {
        if (oauth.hasAccount()) {
            ActivityFactory.startUserActivity(this, oauth.getName(), userNavigationView, userFragmentView);
        } else {
            ActivityFactory.startLoginActivity(this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == IntentIntegrator.BARCODE_REQUEST) {
            String barcode = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent).getContents();
            if (barcode != null) {
                ActivityFactory.startSearchActivity(this, barcode, SearchType.BARCODE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerView.isDrawerOpen(GravityCompat.START)) {
            drawerView.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(getOptionsMenu(), menu);
        if(menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }
        createSearchOptionsMenu(menu);
        return true;
    }

    @SuppressLint("RestrictedApi")
    private void createSearchOptionsMenu(Menu menu) {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(this);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, ResultSearchActivity.class)));
        searchView.setIconifiedByDefault(false);

        if (MediaBrainzApp.getPreferences().isSearchSuggestionsEnabled()) {
            SearchView.SearchAutoComplete searchSrcTextView = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchSrcTextView.setThreshold(1);
            searchSrcTextView.setAdapter(new SuggestionListAdapter(this, Suggestion.SuggestionField.ARTIST));
            searchSrcTextView.setOnItemClickListener((adapterView, view, position, id) -> {
                Suggestion suggestion = (Suggestion) adapterView.getItemAtPosition(position);
                searchView.setQuery(suggestion.toString(), true);
                searchView.clearFocus();
            });
        }
    }

    protected void shareActionText(String text) {
        ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(text)
                .startChooser();
    }

    protected int getOptionsMenu() {
        return DEFAULT_OPTIONS_MENU;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_user_profile:
                if (oauth.hasAccount()) {
                    ActivityFactory.startUserActivity(this, oauth.getName());
                } else {
                    ActivityFactory.startLoginActivity(this);
                }
                return true;

            case R.id.action_scan_barcode:
                IntentIntegrator.initiateScan(this, getString(R.string.zx_title), getString(R.string.zx_message),
                        getString(R.string.zx_pos), getString(R.string.zx_neg), IntentIntegrator.PRODUCT_CODE_TYPES);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        ActivityFactory.startSearchActivity(this, query, null, null);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

}
