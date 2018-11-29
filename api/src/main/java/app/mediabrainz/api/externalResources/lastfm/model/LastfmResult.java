package app.mediabrainz.api.externalResources.lastfm.model;

import com.squareup.moshi.Json;


public class LastfmResult {

    @Json(name = "error")
    private Integer error;

    @Json(name = "message")
    private String message;

    @Json(name = "artist")
    private Artist artist;

    @Json(name = "album")
    private Album album;

    @Json(name = "track")
    private Track track;

    public LastfmResult() {
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
