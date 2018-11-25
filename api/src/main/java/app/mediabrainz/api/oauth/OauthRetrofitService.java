package app.mediabrainz.api.oauth;

import java.util.Map;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;


public interface OauthRetrofitService {

    @GET("/oauth2/userinfo")
    Flowable<Result<OAuthCredential.UserInfo>> getUserInfo(@Query("access_token") String accessToken);

    @GET("/oauth2/tokeninfo")
    Flowable<Result<OAuthCredential.TokenInfo>> getTokenInfo(@Query("access_token") String accessToken);

    @GET("/oauth2/authorize")
    Flowable<Result<ResponseBody>> authorize(@QueryMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/oauth2/authorize")
    Flowable<Result<ResponseBody>> login(
            @QueryMap Map<String, String> params,
            @Header("Cookie") String cookie,
            @Field("username") String username,
            @Field("password") String password);

    @FormUrlEncoded
    @POST("/oauth2/authorize")
    Flowable<Result<ResponseBody>> allowAccess(
            @QueryMap Map<String, String> params,
            @Header("Cookie") String cookie,
            @Field("confirm.submit") int confirm);

    @FormUrlEncoded
    @POST("/oauth2/token")
    Flowable<Result<OAuthCredential>> requestToken(
            @Field("code") String code,
            @Field("client_id") String clientId,
            @Field("client_secret")String clientSecret, //Is not relevant for Android application
            @Field("redirect_uri") String redirectUri,
            @Field("grant_type") String grantType);

    @FormUrlEncoded
    @POST("/oauth2/token")
    Flowable<Result<OAuthCredential>> refreshToken(
            @Field("refresh_token") String refreshToken,
            @Field("client_id") String clientId,
            @Field("client_secret")String clientSecret, //Is not relevant for Android application
            @Field("grant_type") String grantType);

}
