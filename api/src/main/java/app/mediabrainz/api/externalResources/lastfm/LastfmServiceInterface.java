package app.mediabrainz.api.externalResources.lastfm;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.externalResources.lastfm.model.LastfmResult;


public interface LastfmServiceInterface {

    @Deprecated
    Flowable<Result<LastfmResult>> getArtistInfoByMbid(String mbid);

    Flowable<Result<LastfmResult>> getArtistInfoByName(String name);

    @Deprecated
    Flowable<Result<LastfmResult>> getAlbumInfoByMbid(String mbid);

    Flowable<Result<LastfmResult>> getAlbumInfoByName(String artistName, String albumName);

    @Deprecated
    Flowable<Result<LastfmResult>> getTrackInfoByMbid(String mbid);

    Flowable<Result<LastfmResult>> getTrackInfoByName(String artistName, String trackName);

}
