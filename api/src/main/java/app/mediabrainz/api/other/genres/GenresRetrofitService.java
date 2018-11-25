package app.mediabrainz.api.other.genres;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.GET;


public interface GenresRetrofitService {

    String ENDPOINT = "https://raw.githubusercontent.com";

    @GET("/metabrainz/musicbrainz-server/master/entities.json")
    Flowable<Result<ResponseBody>> getEntities();

}
