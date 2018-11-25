package app.mediabrainz.api;

import com.burgstaller.okhttp.digest.Credentials;


public class Config {

    public static String accessToken;


    public static final String WEB_SERVICE = "https://musicbrainz.org";
    public static final String WEB_SERVICE_PREFIX = "/ws/2/";
    public static final String FORMAT_JSON = "json";

    public static final String ANNOTATION_QUERY = WEB_SERVICE_PREFIX + "annotation";
    public static final String AREA_QUERY = WEB_SERVICE_PREFIX + "area";
    public static final String ARTIST_QUERY = WEB_SERVICE_PREFIX + "artist";
    public static final String CDSTUB_QUERY = WEB_SERVICE_PREFIX + "cdstub";
    public static final String COLLECTION_QUERY = WEB_SERVICE_PREFIX + "collection";
    public static final String INSTRUMENT_QUERY = WEB_SERVICE_PREFIX + "instrument";
    public static final String LABEL_QUERY = WEB_SERVICE_PREFIX + "label";
    public static final String PLACE_QUERY = WEB_SERVICE_PREFIX + "place";
    public static final String RATING_QUERY = WEB_SERVICE_PREFIX + "rating";
    public static final String RECORDING_QUERY = WEB_SERVICE_PREFIX + "recording";
    public static final String RELEASE_QUERY = WEB_SERVICE_PREFIX + "release";
    public static final String RELEASE_GROUP_QUERY = WEB_SERVICE_PREFIX + "release-group";
    public static final String TAG_QUERY = WEB_SERVICE_PREFIX + "tag";
    public static final String WORK_QUERY = WEB_SERVICE_PREFIX + "work";
    public static final String EVENT_QUERY = WEB_SERVICE_PREFIX + "event";
    public static final String ISRC_QUERY = WEB_SERVICE_PREFIX + "isrc";
    public static final String ISWC_QUERY = WEB_SERVICE_PREFIX + "iswc";
    public static final String SERIES_QUERY = WEB_SERVICE_PREFIX + "series";
    public static final String URL_QUERY = WEB_SERVICE_PREFIX + "url";
    public static final String DISCID_QUERY = WEB_SERVICE_PREFIX + "discid";

    public static String[] AUTHORIZATED_INCS = {"user-ratings", "user-tags"};

    private static final Credentials credentials = new Credentials("", "");

    public static void setCredentials(String username, String password) {
        credentials.setUserName(username);
        credentials.setPassword(password);
    }

    public static Credentials getCredentials() {
        return credentials;
    }

    //"app.mediabrainz/0.0.1 (algerd75@mail.ru)"
    public static String USER_AGENT_HEADER = "";

    public static void setUserAgentHeader(String userAgentHeader) {
        USER_AGENT_HEADER = userAgentHeader;
    }
}
