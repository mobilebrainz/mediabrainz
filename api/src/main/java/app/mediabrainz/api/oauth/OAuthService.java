package app.mediabrainz.api.oauth;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.core.WebService;
import app.mediabrainz.api.core.WebServiceInterface;

import static app.mediabrainz.api.Config.WEB_SERVICE;


public class OAuthService implements OAuthServiceInterface {

    public static final String STATE = "1351449443";

    private static final String LOG_IN_INPUT = "You need to be logged in to view this page";
    private static final String AUTHORIZATION_INPUT = "Authorization";
    private static final String CODE_INPUT = "Success!";
    private static final String INVALID_AUTENTICATION = "Incorrect username or password";

    private static final WebServiceInterface<OauthRetrofitService> webService =
            new WebService(OauthRetrofitService.class, WEB_SERVICE);

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private Map<String, String> authorizationMap = new HashMap<>();

    public OAuthService(String clientId, String clientSecret, String redirectUri, String scope) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;

        authorizationMap.put("response_type", "code");
        authorizationMap.put("client_id", clientId);
        authorizationMap.put("redirect_uri", redirectUri);
        authorizationMap.put("scope", scope);
        authorizationMap.put("state", STATE);
    }

    @Override
    public Flowable<Result<OAuthCredential>> refreshToken(String refreshToken) {
        return webService.getJsonRetrofitService().refreshToken(refreshToken, clientId, clientSecret, "refresh_token");
    }

    @Override
    public Flowable<Result<OAuthCredential.UserInfo>> getUserInfo(String accessToken) {
        return webService.getJsonRetrofitService().getUserInfo(accessToken);
    }

    @Override
    public Flowable<Result<OAuthCredential.TokenInfo>> getTokenInfo(String accessToken) {
        return webService.getJsonRetrofitService().getTokenInfo(accessToken);
    }

    @Override
    public Flowable<Result<OAuthCredential>> authorize(String username, String password) {
        return webService.getRetrofitService().authorize(authorizationMap)
                .flatMap((Function<Result<ResponseBody>, Flowable<Result<ResponseBody>>>) result -> {
                    if (result.isError()) {
                        return Flowable.just(result);
                    }
                    String html = result.response().body().string();
                    if (html.contains(LOG_IN_INPUT)) {
                        String cookie = result.response().headers().get("set-cookie");
                        return webService.getRetrofitService().login(authorizationMap, cookie, username, password);
                    }
                    return Flowable.just(Result.error(new OAuthException()));
                })
                .flatMap((Function<Result<ResponseBody>, Flowable<Result<ResponseBody>>>) result -> {
                    if (result.isError()) {
                        return Flowable.just(result);
                    }
                    String html = result.response().body().string();
                    if (html.contains(AUTHORIZATION_INPUT)) {
                        String cookie = result.response().headers().get("set-cookie");
                        return webService.getRetrofitService().allowAccess(authorizationMap, cookie, 1);
                    } else if (html.contains(INVALID_AUTENTICATION)) {
                        return Flowable.just(Result.error(OAuthException.INVALID_AUTENTICATION_ERROR));
                    }
                    return Flowable.just(Result.error(new OAuthException()));
                })
                .flatMap((Result<ResponseBody> result) -> {
                    if (result.isError()) {
                        return Flowable.just(Result.error(result.error()));
                    }
                    String html = result.response().body().string();
                    if (html.contains(CODE_INPUT)) {
                        String code = html.substring(html.indexOf("<code>") + 6, html.indexOf("</code>"));
                        if (code.equals("")) {
                            return Flowable.just(Result.error(new OAuthException()));
                        } else {
                            return webService.getJsonRetrofitService().requestToken(
                                    code, clientId, clientSecret, redirectUri, "authorization_code");
                        }
                    }
                    return Flowable.just(Result.error(new OAuthException()));
                });
    }

}
