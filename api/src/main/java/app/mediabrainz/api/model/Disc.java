package app.mediabrainz.api.model;

import com.squareup.moshi.Json;

import java.util.List;


public class Disc extends BaseLookupEntity {

    @Json(name = "offset-count")
    private int offsetCount;

    @Json(name = "sectors")
    private int sectors;

    @Json(name = "offsets")
    private List<Integer> offsets;

    @Json(name = "releases")
    private List<Release> releases;

    public Disc() {
    }

    public int getOffsetCount() {
        return offsetCount;
    }

    public void setOffsetCount(int offsetCount) {
        this.offsetCount = offsetCount;
    }

    public int getSectors() {
        return sectors;
    }

    public void setSectors(int sectors) {
        this.sectors = sectors;
    }

    public List<Integer> getOffsets() {
        return offsets;
    }

    public void setOffsets(List<Integer> offsets) {
        this.offsets = offsets;
    }

    public List<Release> getReleases() {
        return releases;
    }

    public void setReleases(List<Release> releases) {
        this.releases = releases;
    }
}
