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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.R;
import app.mediabrainz.data.room.entity.Suggestion;
import app.mediabrainz.adapter.SuggestionListAdapter;


public class SearchFragment extends BaseFragment {

    public interface SearchFragmentListener {
        void searchEntity(String artist, String album, String track);
    }

    private AutoCompleteTextView artistFieldView;
    private AutoCompleteTextView albumFieldView;
    private AutoCompleteTextView trackFieldView;

    public static SearchFragment newInstance() {
        Bundle args = new Bundle();
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflate(R.layout.fragment_search, container);

        artistFieldView = layout.findViewById(R.id.artistFieldView);
        albumFieldView = layout.findViewById(R.id.albumFieldView);
        trackFieldView = layout.findViewById(R.id.trackFieldView);

        layout.findViewById(R.id.searchButton).setOnClickListener(view -> search());
        return layout;
    }

    private boolean search() {
        String artist = artistFieldView.getText().toString().trim();
        String album = albumFieldView.getText().toString().trim();
        String track = trackFieldView.getText().toString().trim();

        if (!TextUtils.isEmpty(track) || !TextUtils.isEmpty(album) || !TextUtils.isEmpty(artist)) {
            hideKeyboard();
            ((SearchFragmentListener) getContext()).searchEntity(artist, album, track);
        }

        artistFieldView.setText("");
        albumFieldView.setText("");
        trackFieldView.setText("");
        return false;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(artistFieldView.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(albumFieldView.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(trackFieldView.getWindowToken(), 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MediaBrainzApp.getPreferences().isSearchSuggestionsEnabled()) {
            artistFieldView.setAdapter(new SuggestionListAdapter(getContext(), Suggestion.SuggestionField.ARTIST));
            albumFieldView.setAdapter(new SuggestionListAdapter(getContext(), Suggestion.SuggestionField.ALBUM));
            trackFieldView.setAdapter(new SuggestionListAdapter(getContext(), Suggestion.SuggestionField.TRACK));
        } else {
            artistFieldView.setAdapter(new ArrayAdapter<>(getContext(), R.layout.layout_dropdown_item, new String[]{}));
            albumFieldView.setAdapter(new ArrayAdapter<>(getContext(), R.layout.layout_dropdown_item, new String[]{}));
            trackFieldView.setAdapter(new ArrayAdapter<>(getContext(), R.layout.layout_dropdown_item, new String[]{}));
        }
    }

}
