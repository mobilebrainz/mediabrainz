package app.mediabrainz.activity;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;


public abstract class BaseActivity extends AppCompatActivity {

    protected  <T extends ViewModel> T getViewModel(@NonNull Class<T> modelClass) {
        return ViewModelProviders.of(this).get(modelClass);
    }

    @MainThread
    protected void toast(final String msg) {
        if (TextUtils.isEmpty(msg)) return;
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @MainThread
    protected void toast(final int resId) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
    }

}
