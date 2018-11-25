package app.mediabrainz.activity;

import android.os.Bundle;

import app.mediabrainz.R;
import app.mediabrainz.fragment.SettingsFragment;


public class SettingsActivity extends BaseActivity {

    @Override
    protected int initContentLayout() {
        return R.layout.activity_settings;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(R.id.settings_fragment, new SettingsFragment())
                .commit();
    }
}
