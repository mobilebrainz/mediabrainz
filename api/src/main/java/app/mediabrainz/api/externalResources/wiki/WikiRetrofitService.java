package app.mediabrainz.api.externalResources.wiki;

import java.util.Map;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import app.mediabrainz.api.externalResources.wiki.model.Wikipedia;


public interface WikiRetrofitService {

    @Headers({"User-Agent: Mozilla/5.0 (X11; U; Linux x86_64; en-us) AppleWebKit/537.36 (KHTML, like Gecko)  Chrome/30.0.1599.114 Safari/537.36 Puffin/4.8.0.2965AP"})
    @GET("/w/api.php")
    Flowable<Result<Wikipedia>> getWikiMobileview(@QueryMap Map<String, String> params);

    @GET("/wiki/Special:EntityData/{Q}")
    Flowable<Result<ResponseBody>> getWikiEntityData(@Path("Q") String q);

    @GET("/w/api.php")
    Flowable<Result<ResponseBody>> getWikiSitelinks(@QueryMap Map<String, String> params);

}
