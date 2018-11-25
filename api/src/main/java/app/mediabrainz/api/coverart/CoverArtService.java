package app.mediabrainz.api.coverart;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.core.WebService;
import app.mediabrainz.api.core.WebServiceInterface;

import static app.mediabrainz.api.coverart.CoverArtRetrofitService.RELEASE_GROUP_PATH;
import static app.mediabrainz.api.coverart.CoverArtRetrofitService.RELEASE_PATH;
import static app.mediabrainz.api.coverart.CoverArtRetrofitService.COVERART_WEB_SERVICE;


public class CoverArtService implements CoverArtServiceInterface {

    private static final WebServiceInterface<CoverArtRetrofitService> webService =
            new WebService(CoverArtRetrofitService.class, COVERART_WEB_SERVICE);

    @Override
    public Flowable<Result<ReleaseCoverArt>> getReleaseCoverArt(String mbid) {
        return webService.getJsonRetrofitService().getCoverArts(RELEASE_PATH, mbid);
    }

    @Override
    public Flowable<Result<ReleaseCoverArt>> getReleaseGroupCoverArt(String mbid) {
        return webService.getJsonRetrofitService().getCoverArts(RELEASE_GROUP_PATH, mbid);
    }
}
