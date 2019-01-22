package app.mediabrainz.fragment;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public abstract class BaseFragment extends Fragment {

    protected <T extends ViewModel> T getViewModel(@NonNull Class<T> modelClass) {
        return ViewModelProviders.of(this).get(modelClass);
    }

    protected <T extends ViewModel> T getActivityViewModel(@NonNull Class<T> modelClass) {
        return ViewModelProviders.of(getActivity()).get(modelClass);
    }

    protected View inflate(int layoutRes, ViewGroup container) {
        return LayoutInflater.from(getContext()).inflate(layoutRes, container, false);
    }

    @MainThread
    protected void toast(final String msg) {
        if (TextUtils.isEmpty(msg)) return;
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @MainThread
    protected void toast(final int resId) {
        if (getContext() != null) {
            Toast.makeText(getContext(), getContext().getString(resId), Toast.LENGTH_SHORT).show();
        }
    }

}

