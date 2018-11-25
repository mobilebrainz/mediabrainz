package app.mediabrainz.util;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.FrameLayout;


public class FloatingActionButtonBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {

    public FloatingActionButtonBehavior() {
        super();
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        boolean dependsOn = dependency instanceof FrameLayout;
        return dependsOn;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes, int type) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dx, int dy, int[] consumed, int type) {
        if (dy < 0) {
            showView(child);
        } else if (dy > 0) {
            hideView(child);
        }
    }

    private void hideView(FloatingActionButton view) {
        view.animate().translationY(view.getHeight() + 250);
    }

    private void showView(FloatingActionButton view) {
        view.animate().translationY(0);
    }
}
