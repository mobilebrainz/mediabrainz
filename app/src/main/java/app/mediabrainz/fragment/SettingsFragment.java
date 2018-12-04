package app.mediabrainz.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import app.mediabrainz.R;
import app.mediabrainz.data.room.repository.RecommendRepository;
import app.mediabrainz.data.room.repository.SuggestionRepository;


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
        //todo: make progress?
        SuggestionRepository suggestionRepository = new SuggestionRepository();
        suggestionRepository.deleteAll(() -> {
            Toast.makeText(getActivity(), R.string.toast_search_cleared, Toast.LENGTH_SHORT).show();
        });
    }

    private void clearRecommends() {
        //todo: make progress?
        RecommendRepository recommendRepository = new RecommendRepository();
        recommendRepository.deleteAll(() -> {
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
