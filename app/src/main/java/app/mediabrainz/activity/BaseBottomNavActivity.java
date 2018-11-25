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

    protected int navViewId;
    protected boolean isLoading;
    protected boolean isError;

    protected BottomNavigationView bottomNavigationView;
    protected TextView topTitle;
    protected TextView bottomTitle;
    protected FrameLayout frameContainer;
    protected View error;
    protected View loading;
    protected CustomViewPager viewPager;
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
        } else {
            navViewId = initDefaultNavViewId() != -1 ? getIntent().getIntExtra(NAV_VIEW, initDefaultNavViewId()) : savedInstanceState.getInt(NAV_VIEW);
        }

        error = findViewById(R.id.error);
        loading = findViewById(R.id.loading);
        frameContainer = findViewById(R.id.frame_container);
        viewPager = findViewById(R.id.viewpager);
        topTitle = findViewById(R.id.toolbar_title_top);
        bottomTitle = findViewById(R.id.toolbar_title_bottom);

        onCreateActivity(savedInstanceState);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.inflateMenu(initBottomMenuId());
        bottomNavigationView.setOnNavigationItemSelectedListener(initOnNavigationItemSelectedListener());

        // attaching behaviours - hide / showFloatingActionButton on scroll
        ((CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams()).setBehavior(new BottomNavigationBehavior());

        load();
    }

    protected void configBottomNavigationPager() {
        bottomNavigationPagerAdapter = initBottomNavigationPagerAdapter();
        viewPager.setAdapter(bottomNavigationPagerAdapter);
        viewPager.setPagingEnabled(false);
        // lazy loading of pager fragments
        viewPager.setOffscreenPageLimit(bottomNavigationPagerAdapter.getCount());
        bottomNavigationView.setSelectedItemId(getNavViewId());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_VIEW, navViewId);
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
            frameContainer.setAlpha(0.3F);
            viewPager.setAlpha(0.3F);
            loading.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            frameContainer.setAlpha(1.0F);
            viewPager.setAlpha(1.0F);
            loading.setVisibility(View.GONE);
        }
    }

    protected void viewError(boolean isView) {
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

    protected void showConnectionWarning(Throwable t) {
        //ShowUtil.showError(this, t);
        viewProgressLoading(false);
        viewError(true);
        error.findViewById(R.id.retry_button).setOnClickListener(v -> load());
    }

    protected void loadFragment(Fragment fragment) {
        viewPager.setVisibility(View.INVISIBLE);
        frameContainer.setVisibility(View.VISIBLE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public TextView getTopTitle() {
        return topTitle;
    }

    @Override
    public TextView getBottomTitle() {
        return bottomTitle;
    }

    public int getNavViewId() {
        return navViewId;
    }

    public BaseFragmentPagerAdapter getBottomNavigationPagerAdapter() {
        return bottomNavigationPagerAdapter;
    }
}
