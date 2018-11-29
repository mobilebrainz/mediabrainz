package app.mediabrainz.api.externalResources.lastfm.model;

import com.squareup.moshi.Json;


public class Bio {

    @Json(name = "published")
    private String published;

    @Json(name = "summary")
    private String summary;

    @Json(name = "content")
    private String content;

    public Bio() {
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
