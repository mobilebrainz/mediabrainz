package app.mediabrainz.api.externalResources.lyrics;

import java.util.Map;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.QueryMap;
import app.mediabrainz.api.externalResources.lyrics.model.LyricsResult;
import app.mediabrainz.api.externalResources.lyrics.model.LyricsApi;


public interface LyricsRetrofitService {

    //http://lyrics.wikia.com/wikia.php?controller=LyricsApi&method=getSong&artist=iron maiden&song=iron maiden
    @Headers({"User-Agent: Mozilla/5.0 (X11; U; Linux x86_64; en-us) AppleWebKit/537.36 (KHTML, like Gecko)  Chrome/30.0.1599.114 Safari/537.36 Puffin/4.8.0.2965AP"})
    @GET("/wikia.php")
    Flowable<Result<LyricsResult>> getLyricsWikia(@QueryMap Map<String, String> params);


    //http://lyrics.wikia.com/api.php?action=lyrics&fmt=xml&func=getSong&artist=iron%20maiden&song=iron%20maiden
    @Headers({"User-Agent: Mozilla/5.0 (X11; U; Linux x86_64; en-us) AppleWebKit/537.36 (KHTML, like Gecko)  Chrome/30.0.1599.114 Safari/537.36 Puffin/4.8.0.2965AP"})
    @GET("/api.php")
    Flowable<Result<LyricsApi>> getLyricsWikiaApi(@QueryMap Map<String, String> params);
}
