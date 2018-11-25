package app.mediabrainz.api.model;

import com.squareup.moshi.Json;

import java.util.List;


public class ISRC extends BaseLookupEntity {

    @Json(name = "isrc")
    private String isrc;

    @Json(name = "recordings")
    private List<Recording> recordings;

    public ISRC() {
    }

    public String getIsrc() {
        return isrc;
    }

    public void setIsrc(String isrc) {
        this.isrc = isrc;
    }

    public List<Recording> getRecordings() {
        return recordings;
    }

    public void setRecordings(List<Recording> recordings) {
        this.recordings = recordings;
    }
}

