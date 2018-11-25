package app.mediabrainz.suggestion;

import android.content.SearchRecentSuggestionsProvider;


public class SuggestionProvider extends SearchRecentSuggestionsProvider {

    public final static String AUTHORITY = "app.mediabrainz.SearchSuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }

}
