package app.mediabrainz.api.model;

import com.squareup.moshi.Json;


public abstract class BaseSearch {

    @Json(name = "count")
    protected int count;

    @Json(name = "offset")
    protected int offset;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
