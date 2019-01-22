package app.mediabrainz.activity;

import android.os.Bundle;

import app.mediabrainz.R;
import app.mediabrainz.fragment.SettingsFragment;


public class SettingsActivity extends BaseNavigationActivity {

    @Override
    protected int initContentLayout() {
        return R.layout.activity_settings;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settingsFragment, new SettingsFragment())
                .commit();
    }
}
