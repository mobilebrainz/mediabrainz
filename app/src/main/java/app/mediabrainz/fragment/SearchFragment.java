package app.mediabrainz.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;

import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.R;
import app.mediabrainz.suggestion.SuggestionHelper;


public class SearchFragment extends Fragment {

    public interface SearchFragmentListener {
        void searchEntity(String artist, String album, String track);
    }

    private SuggestionHelper suggestionHelper;

    private AutoCompleteTextView artistField;
    private AutoCompleteTextView albumField;
    private AutoCompleteTextView trackField;

    public static SearchFragment newInstance() {
        Bundle args = new Bundle();
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_search, container, false);

        artistField = layout.findViewById(R.id.artist_field);
        albumField = layout.findViewById(R.id.album_field);
        trackField = layout.findViewById(R.id.track_field);

        layout.findViewById(R.id.search_btn).setOnClickListener(view -> search());
        return layout;
    }

    private boolean search() {
        String artist = artistField.getText().toString().trim();
        String album = albumField.getText().toString().trim();
        String track = trackField.getText().toString().trim();

        if (!TextUtils.isEmpty(track) || !TextUtils.isEmpty(album) || !TextUtils.isEmpty(artist)) {
            hideKeyboard();
            ((SearchFragmentListener) getContext()).searchEntity(artist, album, track);
        }

        artistField.setText("");
        albumField.setText("");
        trackField.setText("");
        return false;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(artistField.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(albumField.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(trackField.getWindowToken(), 0);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        suggestionHelper = new SuggestionHelper(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MediaBrainzApp.getPreferences().isSearchSuggestionsEnabled()) {
            artistField.setAdapter(suggestionHelper.getAdapter());
            albumField.setAdapter(suggestionHelper.getAdapter());
            trackField.setAdapter(suggestionHelper.getAdapter());
        } else {
            artistField.setAdapter(suggestionHelper.getEmptyAdapter());
            albumField.setAdapter(suggestionHelper.getEmptyAdapter());
            trackField.setAdapter(suggestionHelper.getEmptyAdapter());
        }
    }

}
