package app.mediabrainz.intent;

import android.content.Context;
import android.content.Intent;

import app.mediabrainz.activity.AboutActivity;
import app.mediabrainz.activity.ArtistActivity;
import app.mediabrainz.activity.ImageActivity;
import app.mediabrainz.activity.LoginActivity;
import app.mediabrainz.activity.MainActivity;
import app.mediabrainz.activity.RecordingActivity;
import app.mediabrainz.activity.ReleaseActivity;
import app.mediabrainz.activity.SearchActivity;
import app.mediabrainz.activity.SearchType;
import app.mediabrainz.activity.SettingsActivity;
import app.mediabrainz.activity.TagActivity;
import app.mediabrainz.activity.UserActivity;
import app.mediabrainz.adapter.pager.TagPagerAdapter;

import static app.mediabrainz.activity.UserActivity.DEFAULT_USER_NAV_VIEW;


public class ActivityFactory {

    public static void startSearchActivity(Context context, String searchQuery, SearchType searchType) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(SearchActivity.SEARCH_QUERY, searchQuery);
        intent.putExtra(SearchActivity.SEARCH_TYPE, searchType.ordinal());
        context.startActivity(intent);
    }

    public static void startSearchActivity(Context context, String artist, String album, String recording) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(SearchActivity.QUERY, artist);
        intent.putExtra(SearchActivity.ALBUM_SEARCH, album);
        intent.putExtra(SearchActivity.TRACK_SEARCH, recording);
        context.startActivity(intent);
    }

    public static void startTagActivity(Context context, String tag, TagPagerAdapter.TagTab tagTab, boolean isGenre) {
        Intent intent = new Intent(context, TagActivity.class);
        intent.putExtra(TagActivity.IS_GENRE, isGenre);
        intent.putExtra(TagActivity.MB_TAG, tag);
        intent.putExtra(TagActivity.TAG_TAB_ORDINAL, tagTab.ordinal());
        context.startActivity(intent);
    }

    public static void startTagActivity(Context context, String tag, boolean isGenre) {
        startTagActivity(context, tag, TagPagerAdapter.TagTab.ARTIST, isGenre);
    }

    public static void startUserActivity(Context context, String username) {
        startUserActivity(context, username, DEFAULT_USER_NAV_VIEW);
    }

    public static void startUserActivity(Context context, String username, int userNavigationView) {
        Intent intent = new Intent(context, UserActivity.class);
        intent.putExtra(UserActivity.USERNAME, username);
        intent.putExtra(UserActivity.NAV_VIEW, userNavigationView);
        context.startActivity(intent);
    }

    public static void startLoginActivity(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    public static void startSettingsActivity(Context context) {
        context.startActivity(new Intent(context, SettingsActivity.class));
    }

    public static void startMainActivity(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    public static void startImageActivity(Context context, String imageUrl) {
        Intent intent = new Intent(context, ImageActivity.class);
        intent.putExtra(ImageActivity.IMAGE_URL, imageUrl);
        context.startActivity(intent);
    }

    public static void startAboutActivity(Context context) {
        context.startActivity(new Intent(context, AboutActivity.class));
    }

    public static void startArtistActivity(Context context, String artistMbid) {
        startArtistActivity(context, artistMbid, ArtistActivity.DEFAULT_ARTIST_NAV_VIEW);
    }

    public static void startArtistActivity(Context context, String artistMbid, int navView) {
        Intent intent = new Intent(context, ArtistActivity.class);
        intent.putExtra(ArtistActivity.ARTIST_MBID, artistMbid);
        intent.putExtra(ArtistActivity.NAV_VIEW, navView);
        context.startActivity(intent);
    }

    public static void startReleaseActivity(Context context, String releaseMbid) {
        startReleaseActivity(context, releaseMbid, ReleaseActivity.DEFAULT_RELEASE_NAV_VIEW);
    }

    public static void startReleaseActivity(Context context, String releaseMbid, int navView) {
        Intent intent = new Intent(context, ReleaseActivity.class);
        intent.putExtra(ReleaseActivity.RELEASE_MBID, releaseMbid);
        intent.putExtra(ReleaseActivity.NAV_VIEW, navView);
        context.startActivity(intent);
    }

    public static void startRecordingActivity(Context context, String recordingMbid) {
        startRecordingActivity(context, recordingMbid, RecordingActivity.DEFAULT_RECORDING_NAV_VIEW);
    }

    public static void startRecordingActivity(Context context, String recordingMbid, int navView) {
        Intent intent = new Intent(context, RecordingActivity.class);
        intent.putExtra(RecordingActivity.RECORDING_MBID, recordingMbid);
        intent.putExtra(RecordingActivity.NAV_VIEW, navView);
        context.startActivity(intent);
    }

}
