package app.mediabrainz.api.model;

import com.squareup.moshi.Json;


public class CoverArt {

    @Json(name = "back")
    private Boolean back;

    @Json(name = "darkened")
    private Boolean darkened;

    @Json(name = "front")
    private Boolean front;

    @Json(name = "artwork")
    private Boolean artwork;

    @Json(name = "count")
    private Integer count;

    public CoverArt() {
    }

    public Boolean getBack() {
        return back;
    }

    public void setBack(Boolean back) {
        this.back = back;
    }

    public Boolean getDarkened() {
        return darkened;
    }

    public void setDarkened(Boolean darkened) {
        this.darkened = darkened;
    }

    public Boolean getFront() {
        return front;
    }

    public void setFront(Boolean front) {
        this.front = front;
    }

    public Boolean getArtwork() {
        return artwork;
    }

    public void setArtwork(Boolean artwork) {
        this.artwork = artwork;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
