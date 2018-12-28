package app.mediabrainz.activity;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import app.mediabrainz.R;
import app.mediabrainz.adapter.pager.BaseFragmentPagerAdapter;
import app.mediabrainz.communicator.ShowTitleCommunicator;
import app.mediabrainz.ui.CustomViewPager;
import app.mediabrainz.util.BottomNavigationBehavior;


public abstract class BaseBottomNavActivity extends BaseActivity implements
        ShowTitleCommunicator {

    public static final String NAV_VIEW = "NAV_VIEW";
    public static final String FRAGMENT_VIEW = "FRAGMENT_VIEW";

    protected int navViewId;
    protected int fragmentViewId = -1; //or tab id
    protected boolean isLoading;
    protected boolean isError;

    protected BottomNavigationView bottomNavView;
    protected TextView toolbarTopTitleView;
    protected TextView toolbarBottomTitleView;
    protected FrameLayout frameContainerView;
    protected View errorView;
    protected View progressView;
    protected CustomViewPager pagerView;
    protected BaseFragmentPagerAdapter bottomNavigationPagerAdapter;

    @Override
    protected int initContentLayout() {
        return R.layout.activity_bottom_nav;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (savedInstanceState != null) {
            navViewId = initDefaultNavViewId() != -1 ? savedInstanceState.getInt(NAV_VIEW, initDefaultNavViewId()) : savedInstanceState.getInt(NAV_VIEW);
            fragmentViewId = savedInstanceState.getInt(NAV_VIEW, -1);
        } else {
            navViewId = initDefaultNavViewId() != -1 ? getIntent().getIntExtra(NAV_VIEW, initDefaultNavViewId()) : savedInstanceState.getInt(NAV_VIEW);
            fragmentViewId = getIntent().getIntExtra(FRAGMENT_VIEW, -1);
        }

        errorView = findViewById(R.id.errorView);
        progressView = findViewById(R.id.progressView);
        frameContainerView = findViewById(R.id.frameContainerView);
        pagerView = findViewById(R.id.pagerView);
        toolbarTopTitleView = findViewById(R.id.toolbarTopTitleView);
        toolbarBottomTitleView = findViewById(R.id.toolbarBottomTitleView);

        onCreateActivity(savedInstanceState);

        bottomNavView = findViewById(R.id.bottomNavView);
        bottomNavView.inflateMenu(initBottomMenuId());
        bottomNavView.setOnNavigationItemSelectedListener(initOnNavigationItemSelectedListener());

        // attaching behaviours - hide / showFloatingActionButton on scroll
        ((CoordinatorLayout.LayoutParams) bottomNavView.getLayoutParams()).setBehavior(new BottomNavigationBehavior());

        load();
    }

    protected void configBottomNavigationPager() {
        bottomNavigationPagerAdapter = initBottomNavigationPagerAdapter();
        pagerView.setAdapter(bottomNavigationPagerAdapter);
        pagerView.setPagingEnabled(false);
        // lazy loading of pager fragments
        pagerView.setOffscreenPageLimit(bottomNavigationPagerAdapter.getCount());
        bottomNavView.setSelectedItemId(getNavViewId());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_VIEW, navViewId);
        outState.putInt(FRAGMENT_VIEW, fragmentViewId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        navViewId = initDefaultNavViewId() != -1 ? savedInstanceState.getInt(NAV_VIEW, initDefaultNavViewId()) : savedInstanceState.getInt(NAV_VIEW);
    }

    protected abstract void onCreateActivity(Bundle savedInstanceState);

    protected abstract BaseFragmentPagerAdapter initBottomNavigationPagerAdapter();

    protected abstract int initBottomMenuId();

    protected int initDefaultNavViewId() {
        return -1;
    }

    protected abstract BottomNavigationView.OnNavigationItemSelectedListener initOnNavigationItemSelectedListener();

    protected void load() {
        configBottomNavigationPager();
    }

    protected void viewProgressLoading(boolean isView) {
        if (isView) {
            isLoading = true;
            frameContainerView.setAlpha(0.3F);
            pagerView.setAlpha(0.3F);
            progressView.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            frameContainerView.setAlpha(1.0F);
            pagerView.setAlpha(1.0F);
            progressView.setVisibility(View.GONE);
        }
    }

    protected void viewError(boolean isView) {
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

    protected void showConnectionWarning(Throwable t) {
        //ShowUtil.showError(this, t);
        viewProgressLoading(false);
        viewError(true);
        errorView.findViewById(R.id.retry_button).setOnClickListener(v -> load());
    }

    protected void loadFragment(Fragment fragment) {
        pagerView.setVisibility(View.INVISIBLE);
        frameContainerView.setVisibility(View.VISIBLE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameContainerView, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public TextView getToolbarTopTitleView() {
        return toolbarTopTitleView;
    }

    @Override
    public TextView getToolbarBottomTitleView() {
        return toolbarBottomTitleView;
    }

    public int getNavViewId() {
        return navViewId;
    }

    public int getFragmentViewId() {
        return fragmentViewId;
    }
    public BaseFragmentPagerAdapter getBottomNavigationPagerAdapter() {
        return bottomNavigationPagerAdapter;
    }
}
