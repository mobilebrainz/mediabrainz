package app.mediabrainz.api.externalResources.lastfm;

import java.util.Map;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import app.mediabrainz.api.externalResources.lastfm.model.LastfmResult;


public interface LastfmRetrofitService {

    // mbidы сильно отличются в ластфи и мб, поэтому лучше искать инфу по имени артиста
    //http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&format=json&mbid=985c5cc9-2dc1-49ac-8806-5b66586a2d58&api_key=b25b959554ed76058ac220b7b2e0a026
    //http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&format=json&artist=riverside&api_key=b25b959554ed76058ac220b7b2e0a026
    @GET("/2.0/")
    Flowable<Result<LastfmResult>> getLastfmResult(@QueryMap Map<String, String> params);

}
