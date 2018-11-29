package app.mediabrainz.api.externalResources.lastfm.model;

import com.squareup.moshi.Json;

import java.util.List;


public class Artist {

    public static class Similar {

        @Json(name = "artist")
        private List<Artist> artists;

        public Similar() {
        }

        public List<Artist> getArtists() {
            return artists;
        }

        public void setArtists(List<Artist> artists) {
            this.artists = artists;
        }
    }

    public static class Tags {

        @Json(name = "tag")
        private List<Tag> tags;

        public Tags() {
        }

        public List<Tag> getTags() {
            return tags;
        }

        public void setTags(List<Tag> tags) {
            this.tags = tags;
        }
    }

    public static class Stats {

        @Json(name = "listeners")
        private int listeners;

        @Json(name = "playcount")
        private int playcount;

        public Stats() {
        }

        public int getListeners() {
            return listeners;
        }

        public void setListeners(int listeners) {
            this.listeners = listeners;
        }

        public int getPlaycount() {
            return playcount;
        }

        public void setPlaycount(int playcount) {
            this.playcount = playcount;
        }
    }

    @Json(name = "mbid")
    private String mbid;

    @Json(name = "name")
    private String name;

    @Json(name = "url")
    private String url;

    @Json(name = "streamable")
    private int streamable;

    @Json(name = "ontour")
    private int ontour;

    @Json(name = "image")
    private List<Image> images;

    @Json(name = "stats")
    private Stats stats;

    @Json(name = "similar")
    private Similar similar;

    @Json(name = "tags")
    private Tags tags;

    @Json(name = "bio")
    private Bio bio;

    public Artist() {
    }

    public String getMbid() {
        return mbid;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
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

    public int getStreamable() {
        return streamable;
    }

    public void setStreamable(int streamable) {
        this.streamable = streamable;
    }

    public int getOntour() {
        return ontour;
    }

    public void setOntour(int ontour) {
        this.ontour = ontour;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public Similar getSimilar() {
        return similar;
    }

    public void setSimilar(Similar similar) {
        this.similar = similar;
    }

    public Tags getTags() {
        return tags;
    }

    public void setTags(Tags tags) {
        this.tags = tags;
    }

    public Bio getBio() {
        return bio;
    }

    public void setBio(Bio bio) {
        this.bio = bio;
    }
}
