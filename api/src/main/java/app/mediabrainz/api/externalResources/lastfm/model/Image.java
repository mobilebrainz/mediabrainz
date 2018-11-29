package app.mediabrainz.api.externalResources.lastfm.model;

import com.squareup.moshi.Json;


public class Image {

    public enum SizeType {

        SMALL("small"),
        MEDIUM("medium"),
        LARGE("large"),
        EXTRALARGE("extralarge"),
        MEGA("mega"),
        EMPTY("");

        private final String type;
        SizeType (String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type;
        }
    }

    @Json(name = "#text")
    private String text;

    @Json(name = "size")
    private String size;

    public Image() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
