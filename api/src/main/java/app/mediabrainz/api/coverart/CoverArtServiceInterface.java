package app.mediabrainz.api.coverart;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;


public interface CoverArtServiceInterface {

    Flowable<Result<ReleaseCoverArt>> getReleaseCoverArt(String mbid);

    Flowable<Result<ReleaseCoverArt>> getReleaseGroupCoverArt(String mbid);
}
