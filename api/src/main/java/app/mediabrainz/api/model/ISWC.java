package app.mediabrainz.api.model;

import com.squareup.moshi.Json;

import java.util.List;


public class ISWC extends BaseLookupEntity {

    @Json(name = "work-count")
    private int workCount;

    @Json(name = "work-offset")
    private int workOffset;

    @Json(name = "works")
    private List<Work> works;

    public ISWC() {
    }

    public int getWorkCount() {
        return workCount;
    }

    public void setWorkCount(int workCount) {
        this.workCount = workCount;
    }

    public int getWorkOffset() {
        return workOffset;
    }

    public void setWorkOffset(int workOffset) {
        this.workOffset = workOffset;
    }

    public List<Work> getWorks() {
        return works;
    }

    public void setWorks(List<Work> works) {
        this.works = works;
    }
}
