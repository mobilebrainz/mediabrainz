package app.mediabrainz.api.model;

import com.squareup.moshi.Json;


public class Rating {

    @Json(name = "value")
    private Float value;

    @Json(name = "votes-count")
    private Integer votesCount;

    public Rating() {
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public Integer getVotesCount() {
        return votesCount;
    }

    public void setVotesCount(Integer votesCount) {
        this.votesCount = votesCount;
    }
}
