package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import app.mediabrainz.R;


public class ArtistSettingsFragment extends PreferenceFragmentCompat {

    public static ArtistSettingsFragment newInstance() {
        Bundle args = new Bundle();
        ArtistSettingsFragment fragment = new ArtistSettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.artist_preferences, rootKey);
    }

}
