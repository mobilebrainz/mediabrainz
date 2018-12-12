package app.mediabrainz.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import app.mediabrainz.MediaBrainzApp;


public class Preferences {

    private static final String USER_PREFERENCE_FILE = "user";

    public interface PreferenceName {
        String SUGGESTIONS = "search_suggestions";
        String LOAD_IMAGES = "load_images";
        String LOAD_RATINGS = "load_ratings";
        String PROPAGATE_ARTIST_TAGS = "propagate_artist_tags";

        String RELEASE_GROUP_OFFICIAL = "release_group_official";
    }

    public void clearData() {
        SharedPreferences prefs = getUserPreferences();
        prefs.edit().clear().apply();
    }

    public void setSearchSuggestionsEnabled(boolean enabled) {
        SharedPreferences prefs = getDefaultPreferences();
        prefs.edit().putBoolean(PreferenceName.SUGGESTIONS, enabled).apply();
    }

    public boolean isSearchSuggestionsEnabled() {
        return getDefaultPreferences().getBoolean(PreferenceName.SUGGESTIONS, true);
    }

    public void setLoadImagesEnabled(boolean enabled) {
        SharedPreferences prefs = getDefaultPreferences();
        prefs.edit().putBoolean(PreferenceName.LOAD_IMAGES, enabled).apply();
    }

    public boolean isLoadImagesEnabled() {
        return getDefaultPreferences().getBoolean(PreferenceName.LOAD_IMAGES, true);
    }

    public boolean isLoadRatingsEnabled() {
        return getDefaultPreferences().getBoolean(PreferenceName.LOAD_RATINGS, true);
    }

    public boolean isPropagateArtistTags() {
        return getDefaultPreferences().getBoolean(PreferenceName.PROPAGATE_ARTIST_TAGS, false);
    }

    private SharedPreferences getDefaultPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(MediaBrainzApp.getContext());
    }

    private SharedPreferences getUserPreferences() {
        return MediaBrainzApp.getContext().getSharedPreferences(USER_PREFERENCE_FILE, Context.MODE_PRIVATE);
    }

    public boolean isReleaseGroupOfficial() {
        return getDefaultPreferences().getBoolean(PreferenceName.RELEASE_GROUP_OFFICIAL, false);
    }

    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        getDefaultPreferences().registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        getDefaultPreferences().unregisterOnSharedPreferenceChangeListener(listener);
    }

}
