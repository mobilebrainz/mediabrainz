package app.mediabrainz.api.externalResources.lastfm.model;

import com.squareup.moshi.Json;


public class Tag {

    @Json(name = "name")
    private String name;

    @Json(name = "url")
    private String url;

    public Tag() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
