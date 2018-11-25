package app.mediabrainz.api.model;

import com.squareup.moshi.Json;

import app.mediabrainz.api.core.ApiUtils;


public class Url extends BaseLookupEntity implements Comparable<Url> {

    @Json(name = "resource")
    private String resource;

    private String type = "";

    public Url() {
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int compareTo(Url another) {
        return getType().compareTo(another.getType());
    }

    public String getPrettyUrl() {
        // Remove http:// and trailing /
        String url = resource.replace("http://", "");
        url = url.replace("https://", "");
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    public String getPrettyType() {
        type = type.replace('_', ' ');
        return ApiUtils.initialCaps(type);
    }
}
