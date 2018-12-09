package app.mediabrainz.api.externalResources.lastfm.model;

import com.squareup.moshi.Json;


public class Album {

    @Json(name = "wiki")
    private Bio bio;

    @Json(name = "listeners")
    private int listeners;

    @Json(name = "playcount")
    private int playcount;

    public Album() {
    }

    public Bio getBio() {
        return bio;
    }

    public void setBio(Bio bio) {
        this.bio = bio;
    }

    public int getListeners() {
        return listeners;
    }

    public void setListeners(int listeners) {
        this.listeners = listeners;
    }

    public int getPlaycount() {
        return playcount;
    }

    public void setPlaycount(int playcount) {
        this.playcount = playcount;
    }
}
