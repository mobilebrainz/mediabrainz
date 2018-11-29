package app.mediabrainz.api.externalResources.lastfm;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.core.WebService;
import app.mediabrainz.api.core.WebServiceInterface;
import app.mediabrainz.api.externalResources.lastfm.model.LastfmResult;

import static app.mediabrainz.api.externalResources.lastfm.LastfmConfig.LASTFM_API_KEY;
import static app.mediabrainz.api.externalResources.lastfm.LastfmConfig.LASTFM_FORMAT;
import static app.mediabrainz.api.externalResources.lastfm.LastfmConfig.LASTFM_WEB_SERVICE;

/**
 * Сайт выдаёт ошибочную информацию по редким запросам.
 * Например по двум разным группам Riverside c mbid = 077102c2-9dbf-4515-8531-3878461c68b0
 * и mbid = f554c686-ead0-42ef-9deb-3a42f7199196 выдаётся результат одной польской группы.
 * Это ведёт к отображению неправильных изображений и био на страницах редких групп.
 * Поэтому рекомендуется брать био и картинки с википедии.
 */

public class LastfmService implements LastfmServiceInterface {

    private static final WebServiceInterface<LastfmRetrofitService> webService =
            new WebService(LastfmRetrofitService.class, LASTFM_WEB_SERVICE);

    private Map<String, String> params = new HashMap<>();

    public LastfmService() {
        params.put("api_key", LASTFM_API_KEY);
        params.put("format", LASTFM_FORMAT);
    }

    /**
     * @param mbid - The musicbrainz id for the artist
     * @return
     */
    @Deprecated
    @Override
    public Flowable<Result<LastfmResult>> getArtistInfoByMbid(String mbid) {
        params.put("method", "artist.getinfo");
        params.put("mbid", mbid);
        return webService.getJsonRetrofitService().getLastfmResult(params);
    }

    @Override
    public Flowable<Result<LastfmResult>> getArtistInfoByName(String name) {
        params.put("method", "artist.getinfo");
        params.put("artist", name);
        return webService.getJsonRetrofitService().getLastfmResult(params);
    }

    /**
     * @param mbid - The musicbrainz id for the release
     * @return
     */
    @Deprecated
    @Override
    public Flowable<Result<LastfmResult>> getAlbumInfoByMbid(String mbid) {
        params.put("method", "album.getinfo");
        params.put("mbid", mbid);
        return webService.getJsonRetrofitService().getLastfmResult(params);
    }

    @Override
    public Flowable<Result<LastfmResult>> getAlbumInfoByName(String artistName, String albumName) {
        params.put("method", "album.getinfo");
        params.put("artist", artistName);
        params.put("album", albumName);
        return webService.getJsonRetrofitService().getLastfmResult(params);
    }

    @Deprecated
    @Override
    public Flowable<Result<LastfmResult>> getTrackInfoByMbid(String mbid) {
        params.put("method", "track.getinfo");
        params.put("mbid", mbid);
        return webService.getJsonRetrofitService().getLastfmResult(params);
    }

    @Override
    public Flowable<Result<LastfmResult>> getTrackInfoByName(String artistName, String trackName) {
        params.put("method", "track.getinfo");
        params.put("artist", artistName);
        params.put("track", trackName);
        return webService.getJsonRetrofitService().getLastfmResult(params);
    }

}
