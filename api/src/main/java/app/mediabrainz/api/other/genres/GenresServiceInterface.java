package app.mediabrainz.api.other.genres;


import java.util.List;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;


public interface GenresServiceInterface {

    Flowable<Result<List<String>>> gerGenres();

}
