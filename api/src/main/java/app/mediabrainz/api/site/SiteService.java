package app.mediabrainz.api.site;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.core.WebService;
import app.mediabrainz.api.core.WebServiceInterface;

import static app.mediabrainz.api.Config.WEB_SERVICE;
import static app.mediabrainz.api.model.Collection.AREA_TYPE;
import static app.mediabrainz.api.model.Collection.ARTIST_TYPE;
import static app.mediabrainz.api.model.Collection.ATTENDING_EVENT_TYPE;
import static app.mediabrainz.api.model.Collection.EVENT_TYPE;
import static app.mediabrainz.api.model.Collection.INSTRUMENT_TYPE;
import static app.mediabrainz.api.model.Collection.LABEL_TYPE;
import static app.mediabrainz.api.model.Collection.MAYBE_ATTENDING_EVENT_TYPE;
import static app.mediabrainz.api.model.Collection.OWNED_RELEASE_TYPE;
import static app.mediabrainz.api.model.Collection.PLACE_TYPE;
import static app.mediabrainz.api.model.Collection.RECORDING_TYPE;
import static app.mediabrainz.api.model.Collection.RELEASE_GROUP_TYPE;
import static app.mediabrainz.api.model.Collection.RELEASE_TYPE;
import static app.mediabrainz.api.model.Collection.SERIES_TYPE;
import static app.mediabrainz.api.model.Collection.WISHLIST_RELEASE_TYPE;
import static app.mediabrainz.api.model.Collection.WORK_TYPE;


public class SiteService implements SiteServiceInterface {

    private static final String INCORRECT_LOGIN = "Incorrect username or password";

    private static final WebServiceInterface<SiteRetrofitService> webService =
            new WebService(SiteRetrofitService.class, WEB_SERVICE);

    private String username;
    private String password;

    private static final List<String> collectionSpinnerList = new ArrayList<>(Arrays.asList(
            RELEASE_TYPE,
            OWNED_RELEASE_TYPE,
            WISHLIST_RELEASE_TYPE,
            EVENT_TYPE,
            ATTENDING_EVENT_TYPE,
            MAYBE_ATTENDING_EVENT_TYPE,
            AREA_TYPE,
            ARTIST_TYPE,
            INSTRUMENT_TYPE,
            LABEL_TYPE,
            PLACE_TYPE,
            RECORDING_TYPE,
            RELEASE_GROUP_TYPE,
            SERIES_TYPE,
            WORK_TYPE
    ));

    public static String getCollectionTypeFromSpinner(int type) {
        return collectionSpinnerList.get(type);
    }

    public static int getCollectionTypeFromSpinner(String type) {
        if (collectionSpinnerList.contains(type)) {
            return collectionSpinnerList.indexOf(type) + 1;
        } else
            return -1;
    }

    public SiteService(String username, String password) {
        this.username = username;
        this.password = password;
    }

    private String cookie;
    private long cookieTimeMillis = 0;

    private boolean isExpireCookie() {
        final long COOKIE_LIFE_TIME = 600000;
        return cookie == null || System.currentTimeMillis() - cookieTimeMillis > COOKIE_LIFE_TIME;
    }

    @Override
    public Flowable<Result<ResponseBody>> login(OnLogin onLogin) {
        if (isExpireCookie()) {
            return webService.getRetrofitService().login()
                    .flatMap(result -> {
                        if (result.isError()) {
                            return Flowable.just(result);
                        }
                        String cook = result.response().headers().get("set-cookie");
                        return webService.getRetrofitService().login(username, password, cook);
                    })
                    .flatMap(result -> {
                        if (result.isError()) {
                            return Flowable.just(result);
                        }
                        String html = result.response().body().string();
                        if (!html.contains(INCORRECT_LOGIN)) {
                            cookie = result.response().headers().get("set-cookie");
                            cookieTimeMillis = System.currentTimeMillis();
                            return onLogin.onLogin();
                        } else {
                            return Flowable.just(Result.error(new LogInException(INCORRECT_LOGIN)));
                        }
                    });
        } else {
            return onLogin.onLogin();
        }
    }

    @Override
    public Flowable<Result<ResponseBody>> deleteCollection(String collectionId) {
        return login(() -> webService.getRetrofitService().deleteCollection(collectionId, cookie, ""));
    }

    @Override
    public Flowable<Result<ResponseBody>> editCollection(String collectionId, String name, int typeFromSpinner, String description, int isPublic) {
        return login(() -> webService.getRetrofitService().editCollection(collectionId, cookie, name, typeFromSpinner, description, isPublic));
    }

    @Override
    public Flowable<Result<ResponseBody>> createCollection(String name, int typeFromSpinner, String description, int isPublic) {
        return login(() -> webService.getRetrofitService().createCollection(cookie, name, typeFromSpinner, description, isPublic));
    }

    @Override
    public Flowable<Result<ResponseBody>> sendEmail(String username, String subject, String message, boolean revealEmail) {
        return login(() -> webService.getRetrofitService().sendEmail(cookie, username, subject, message, revealEmail, false));
    }

}
