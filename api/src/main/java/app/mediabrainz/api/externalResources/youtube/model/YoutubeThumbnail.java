package app.mediabrainz.api.externalResources.youtube.model;

import com.squareup.moshi.Json;

/*
{
    "url": string,
    "width": unsigned integer,
    "height": unsigned integer
}
*/
public class YoutubeThumbnail {

    public static class YoutubeThumbnails {

        @Json(name = "default")
        private YoutubeThumbnail defaultThumbnail;

        @Json(name = "medium")
        private YoutubeThumbnail mediumThumbnail;

        @Json(name = "high")
        private YoutubeThumbnail highThumbnail;

        public YoutubeThumbnails() {
        }

        public YoutubeThumbnail getDefaultThumbnail() {
            return defaultThumbnail;
        }

        public void setDefaultThumbnail(YoutubeThumbnail defaultThumbnail) {
            this.defaultThumbnail = defaultThumbnail;
        }

        public YoutubeThumbnail getMediumThumbnail() {
            return mediumThumbnail;
        }

        public void setMediumThumbnail(YoutubeThumbnail mediumThumbnail) {
            this.mediumThumbnail = mediumThumbnail;
        }

        public YoutubeThumbnail getHighThumbnail() {
            return highThumbnail;
        }

        public void setHighThumbnail(YoutubeThumbnail highThumbnail) {
            this.highThumbnail = highThumbnail;
        }
    }

    @Json(name = "url")
    private String url;

    @Json(name = "width")
    private int width;

    @Json(name = "height")
    private int height;

    public YoutubeThumbnail() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

}
