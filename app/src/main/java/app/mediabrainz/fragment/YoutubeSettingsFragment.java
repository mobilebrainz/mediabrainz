package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import app.mediabrainz.R;


public class YoutubeSettingsFragment extends PreferenceFragmentCompat {

    public static YoutubeSettingsFragment newInstance() {
        Bundle args = new Bundle();
        YoutubeSettingsFragment fragment = new YoutubeSettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.youtube_preferences, rootKey);
    }

}
