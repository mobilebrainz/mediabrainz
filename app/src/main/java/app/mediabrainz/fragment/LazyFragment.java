package app.mediabrainz.fragment;


import android.support.v4.app.Fragment;


public abstract class LazyFragment extends Fragment {

    private boolean isLoaded;

    protected abstract void lazyLoad();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed() && !isLoaded) {
            lazyLoad();
            isLoaded = true;
        }
    }

    protected void loadView() {
        if (getUserVisibleHint()) {
            lazyLoad();
            isLoaded = true;
        }
    }

}