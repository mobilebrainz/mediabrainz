package app.mediabrainz.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;
import android.widget.Toast;

import app.mediabrainz.R;
import app.mediabrainz.data.DatabaseHelper;
import app.mediabrainz.suggestion.SuggestionProvider;


public class SettingsFragment extends PreferenceFragment implements
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

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
        SearchRecentSuggestions suggestions =
                new SearchRecentSuggestions(getActivity(), SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
        suggestions.clearHistory();
        Toast.makeText(getActivity(), R.string.toast_search_cleared, Toast.LENGTH_SHORT).show();
    }

    private void clearRecommends() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
        databaseHelper.deleteAllTags();
        databaseHelper.close();
        Toast.makeText(getActivity(), R.string.toast_recommends_cleared, Toast.LENGTH_SHORT).show();
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
