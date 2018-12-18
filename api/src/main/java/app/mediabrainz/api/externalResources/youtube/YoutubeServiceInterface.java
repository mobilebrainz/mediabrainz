package app.mediabrainz.api.externalResources.youtube;

import app.mediabrainz.api.externalResources.youtube.model.YoutubeSearchListResponse;
import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;


public interface YoutubeServiceInterface {

    Flowable<Result<YoutubeSearchListResponse>> search(String keyword, String youtubeApiKey);

}
