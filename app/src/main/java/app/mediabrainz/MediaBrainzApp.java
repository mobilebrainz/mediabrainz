package app.mediabrainz;

import android.accounts.AccountManager;
import android.app.Application;
import android.content.pm.PackageManager;

import app.mediabrainz.account.OAuth;
import app.mediabrainz.account.Preferences;
import app.mediabrainz.api.Config;
import app.mediabrainz.apihandler.Api;


public class MediaBrainzApp extends Application {

    public static final String SUPPORT_MAIL = "algerd75@mail.ru";

    public static OAuth oauth;
    public static Api api;

    private static MediaBrainzApp instance;
    private static Preferences preferences;

    public void onCreate() {
        super.onCreate();
        instance = this;
        Config.setUserAgentHeader(getPackage() + "/" + getVersion() + " (" + SUPPORT_MAIL + ")");

        oauth = new OAuth(AccountManager.get(this));
        api = new Api(oauth);
        preferences = new Preferences();
    }

    public static MediaBrainzApp getContext() {
        return instance;
    }

    public static Preferences getPreferences() {
        return preferences;
    }

    public static String getVersion() {
        try {
            return instance.getPackageManager().getPackageInfo(instance.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "unknown";
        }
    }

    public static String getPackage() {
        try {
            return instance.getPackageManager().getPackageInfo(instance.getPackageName(), 0).packageName;
        } catch (PackageManager.NameNotFoundException e) {
            return "unknown";
        }
    }

}
