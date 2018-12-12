package app.mediabrainz.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.widget.Toast;

import app.mediabrainz.R;
import app.mediabrainz.data.room.repository.RecommendRepository;
import app.mediabrainz.data.room.repository.SuggestionRepository;


public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String CLEAR_SUGGESTIONS = "clear_suggestions";
    private static final String CLEAR_RECOMMENDS = "clear_recommends";

    private SharedPreferences prefs;

    public static SettingsFragment newInstance() {
        Bundle args = new Bundle();

        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.preferences, s);

        findPreference(CLEAR_SUGGESTIONS).setOnPreferenceClickListener(preference -> {
            if (preference.getKey().equals(CLEAR_SUGGESTIONS)) {
                clearSuggestionHistory();
                return true;
            }
            return false;
        });

        findPreference(CLEAR_RECOMMENDS).setOnPreferenceClickListener(preference -> {
            if (preference.getKey().equals(CLEAR_RECOMMENDS)) {
                clearRecommends();
                return true;
            }
            return false;
        });
    }

    private void clearSuggestionHistory() {
        //todo: make progress?
        new SuggestionRepository().deleteAll(() -> {
            Toast.makeText(getActivity(), R.string.toast_search_cleared, Toast.LENGTH_SHORT).show();
        });
    }

    private void clearRecommends() {
        //todo: make progress?
        new RecommendRepository().deleteAll(() -> {
            Toast.makeText(getActivity(), R.string.toast_recommends_cleared, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

}
