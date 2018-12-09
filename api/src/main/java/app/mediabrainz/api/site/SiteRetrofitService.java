package app.mediabrainz.api.site;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface SiteRetrofitService {

    @GET("/login")
    Flowable<Result<ResponseBody>> login();
    
    @FormUrlEncoded
    @POST("/login")
    Flowable<Result<ResponseBody>> login(
            @Field("username") String username,
            @Field("password") String password,
            @Header("Cookie") String cookie);

    @FormUrlEncoded
    @POST("/collection/{collectionId}/own_collection/delete")
    Flowable<Result<ResponseBody>> deleteCollection(
            @Path("collectionId") String collectionId,
            @Header("Cookie") String cookie,
            @Field("empty") String empty);

    @FormUrlEncoded
    @POST("/collection/{collectionId}/own_collection/edit")
    Flowable<Result<ResponseBody>> editCollection(
            @Path("collectionId") String collectionId,
            @Header("Cookie") String cookie,
            @Field("edit-list.name") String name,
            @Field("edit-list.type_id") int typeFromSpinner,
            @Field("edit-list.description") String description,
            @Field("edit-list.public") int isPublic);

    @FormUrlEncoded
    @POST("/collection/create")
    Flowable<Result<ResponseBody>> createCollection(
            @Header("Cookie") String cookie,
            @Field("edit-list.name") String name,
            @Field("edit-list.type_id") int type,
            @Field("edit-list.description") String description,
            @Field("edit-list.public") int isPublic);

    @GET("/user/{username}/ratings/{type}")
    Flowable<Result<ResponseBody>> getRatings(
            @Path("username") String user,
            @Path("type") String type,
            @Query("page") int page);


    @GET("/user/{username}/tags")
    Flowable<Result<ResponseBody>> getUserTags(@Path("username") String user);

    @GET("/user/{username}/tag/{tag}")
    Flowable<Result<ResponseBody>> getUserTagEntities(@Path("username") String user, @Path("tag") String tag);

    @GET("/tag/{tag}/{type}")
    Flowable<Result<ResponseBody>> getTagEntities(
            @Path("tag") String tag,
            @Path("type") String type,
            @Query("page") int page);

    @GET("/user/{username}")
    Flowable<Result<ResponseBody>> getUserProfile(@Path("username") String user);

    @GET("/search")
    Flowable<Result<ResponseBody>> search(
            @Query("query") String query,
            @Query("type") String type,
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("method") String method
    );

    @FormUrlEncoded
    @POST("/user/{username}/contact")
    Flowable<Result<ResponseBody>> sendEmail(
            @Header("Cookie") String cookie,
            @Path("username") String username,
            @Field("contact.subject") String subject,
            @Field("contact.body") String message,
            @Field("contact.reveal_address") boolean revealEmail,
            @Field("contact.send_to_self") boolean sendToSelf);
}
