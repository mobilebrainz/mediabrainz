package app.mediabrainz.api.externalResources.youtube;


import java.util.Map;

import app.mediabrainz.api.externalResources.youtube.model.YoutubeSearchListResponse;
import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

import static app.mediabrainz.api.externalResources.youtube.YoutubeConfig.YOUTUBE_WEB_SERVICE_PREFIX;


public interface YoutubeRetrofitService {

    @GET(YOUTUBE_WEB_SERVICE_PREFIX + "search")
    Flowable<Result<YoutubeSearchListResponse>> search(@QueryMap Map<String, String> params);

}
