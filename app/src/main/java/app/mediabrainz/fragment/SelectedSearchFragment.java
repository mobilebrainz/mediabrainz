package app.mediabrainz.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.R;
import app.mediabrainz.activity.SearchType;
import app.mediabrainz.data.room.entity.Suggestion;
import app.mediabrainz.intent.ActivityFactory;
import app.mediabrainz.adapter.SuggestionListAdapter;


public class SelectedSearchFragment extends Fragment {

    public interface SelectedSearchFragmentListener {
        void searchType(SearchType searchType, String query);

        List<String> getGenres();
    }

    private List<String> genres = new ArrayList<>();

    private AutoCompleteTextView searchField;
    private Spinner searchTypeSpinner;
    private ArrayAdapter<String> adapter;

    public static SearchFragment newInstance() {
        Bundle args = new Bundle();
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_selected_search, container, false);

        searchTypeSpinner = layout.findViewById(R.id.search_spin);
        searchField = layout.findViewById(R.id.query_input);

        searchField.setOnEditorActionListener((view, actionId, event) -> search());
        layout.findViewById(R.id.search_btn).setOnClickListener(view -> search());

        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupSearchTypeSpinner();
    }

    private void setupSearchTypeSpinner() {
        List<CharSequence> types = new ArrayList<>();
        for (SearchType searchType : SearchType.values()) {
            types.add(getResources().getText(searchType.getRes()));
        }
        ArrayAdapter<CharSequence> typeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchTypeSpinner.setAdapter(typeAdapter);

        searchTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                if (SearchType.TAG.ordinal() == pos) {
                    if (genres.isEmpty()) {
                        genres = ((SelectedSearchFragmentListener) getContext()).getGenres();
                    }
                    if (!genres.isEmpty()) {
                        if (adapter == null) {
                            adapter = new ArrayAdapter<>(
                                    getContext(),
                                    android.R.layout.simple_dropdown_item_1line,
                                    genres.toArray(new String[genres.size()]));
                        }
                        searchField.setThreshold(1);
                        searchField.setAdapter(adapter);
                    }
                } else if (SearchType.USER.ordinal() == pos && MediaBrainzApp.getPreferences().isSearchSuggestionsEnabled()) {
                    searchField.setThreshold(2);
                    searchField.setAdapter(new SuggestionListAdapter(getContext(), Suggestion.SuggestionField.USER));
                } else {
                    searchField.setAdapter(new ArrayAdapter<>(getContext(), R.layout.layout_dropdown_item, new String[]{}));
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private boolean search() {
        String query = searchField.getText().toString().trim().toLowerCase();
        if (!TextUtils.isEmpty(query)) {
            hideKeyboard();

            if (genres.contains(query)) {
                ActivityFactory.startTagActivity(getContext(), query, true);
            } else {
                SearchType searchType = SearchType.values()[searchTypeSpinner.getSelectedItemPosition()];
                ((SelectedSearchFragmentListener) getContext()).searchType(searchType, query);
            }
        }
        return false;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
    }

}
