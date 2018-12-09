package app.mediabrainz.api.externalResources.lastfm.model;


import com.squareup.moshi.Json;


public class Track {

    @Json(name = "name")
    private String name;

    @Json(name = "mbid")
    private String mbid;

    @Json(name = "wiki")
    private Bio bio;

    @Json(name = "listeners")
    private int listeners;

    @Json(name = "playcount")
    private int playcount;

    public Track() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMbid() {
        return mbid;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
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
