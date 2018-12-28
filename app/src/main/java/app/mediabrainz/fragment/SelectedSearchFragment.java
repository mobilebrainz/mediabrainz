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
import app.mediabrainz.adapter.SuggestionListAdapter;
import app.mediabrainz.data.room.entity.Suggestion;
import app.mediabrainz.intent.ActivityFactory;


public class SelectedSearchFragment extends Fragment {

    public interface SelectedSearchFragmentListener {
        void searchType(SearchType searchType, String query);
    }

    private List<String> genres = new ArrayList<>();

    private AutoCompleteTextView queryInputView;
    private Spinner searchSpinner;
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

        searchSpinner = layout.findViewById(R.id.searchSpinner);
        queryInputView = layout.findViewById(R.id.queryInputView);

        queryInputView.setOnEditorActionListener((view, actionId, event) -> search());
        layout.findViewById(R.id.searchButton).setOnClickListener(view -> search());

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
        searchSpinner.setAdapter(typeAdapter);

        searchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                if (SearchType.TAG.ordinal() == pos) {
                    genres = MediaBrainzApp.getGenres();
                    if (!genres.isEmpty()) {
                        if (adapter == null) {
                            adapter = new ArrayAdapter<>(
                                    getContext(),
                                    android.R.layout.simple_dropdown_item_1line,
                                    genres.toArray(new String[genres.size()]));
                        }
                        queryInputView.setThreshold(1);
                        queryInputView.setAdapter(adapter);
                    }
                } else if (SearchType.USER.ordinal() == pos && MediaBrainzApp.getPreferences().isSearchSuggestionsEnabled()) {
                    queryInputView.setThreshold(2);
                    queryInputView.setAdapter(new SuggestionListAdapter(getContext(), Suggestion.SuggestionField.USER));
                } else {
                    queryInputView.setAdapter(new ArrayAdapter<>(getContext(), R.layout.layout_dropdown_item, new String[]{}));
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private boolean search() {
        String query = queryInputView.getText().toString().trim().toLowerCase();
        if (!TextUtils.isEmpty(query)) {
            hideKeyboard();

            if (genres.contains(query)) {
                ActivityFactory.startTagActivity(getContext(), query, true);
            } else {
                SearchType searchType = SearchType.values()[searchSpinner.getSelectedItemPosition()];
                ((SelectedSearchFragmentListener) getContext()).searchType(searchType, query);
            }
        }
        return false;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(queryInputView.getWindowToken(), 0);
    }

}
