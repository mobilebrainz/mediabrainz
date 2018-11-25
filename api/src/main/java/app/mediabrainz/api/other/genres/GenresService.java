package app.mediabrainz.api.other.genres;

import org.json.JSONArray;
import org.json.JSONObject;
import app.mediabrainz.api.core.WebService;
import app.mediabrainz.api.core.WebServiceInterface;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava2.Result;

import static app.mediabrainz.api.other.genres.GenresRetrofitService.ENDPOINT;


public class GenresService implements GenresServiceInterface {

    private static final WebServiceInterface<GenresRetrofitService> webService =
            new WebService(GenresRetrofitService.class, ENDPOINT);

    @Override
    public Flowable<Result<List<String>>> gerGenres() {
        return webService.getRetrofitService().getEntities()
                .flatMap((Function<Result<ResponseBody>, Flowable<Result<List<String>>>>) result -> {
                    if (!result.isError()) {
                        List<String> genres = new ArrayList<>();
                        String response = result.response().body().string();
                        JSONObject mainObject = new JSONObject(response);
                        JSONArray jsongenres = mainObject.getJSONObject("tag").getJSONArray("genres");
                        for (int i = 0; i < jsongenres.length(); i++) {
                            genres.add(jsongenres.getString(i));
                        }
                        return Flowable.just(Result.response(Response.success(genres)));
                    }
                    return Flowable.error(result.error());
                });
    }

}
