package app.mediabrainz.api.model;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * https://musicbrainz.org/doc/Recording
 */

public class Recording extends BaseLookupEntity {

    public static class RecordingSearch extends BaseSearch {
        @Json(name = "recordings")
        private List<Recording> recordings;

        public List<Recording> getRecordings() {
            return recordings;
        }

        public void setRecordings(List<Recording> recordings) {
            this.recordings = recordings;
        }
    }

    public static class RecordingBrowse {

        @Json(name = "recording-count")
        protected int count;

        @Json(name = "recording-offset")
        protected int offset;

        @Json(name = "recordings")
        private List<Recording> recordings;

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

        public List<Recording> getRecordings() {
            return recordings;
        }

        public void setRecordings(List<Recording> recordings) {
            this.recordings = recordings;
        }
    }

    @Json(name = "score")
    private int score;

    @Json(name = "title")
    private String title = "";

    @Json(name = "length")
    private Long length;

    @Json(name = "disambiguation")
    private String disambiguation = "";

    @Json(name = "video")
    private Boolean video;

    // TODO: do adapter for ISRCS
    /*
     conflict with return type isrcs field
     search: https://musicbrainz.org/ws/2/recording?fmt=json&query=isrc:USGF19942501
     lookup: https://musicbrainz.org/ws/2/recording/6da76448-982a-4a01-b65b-9a710301c9c9?fmt=json&inc=isrcs
     search return array of objects with fields id = isrc
     lookup ISRCS return array of String isrc
     */
    @Json(name = "isrcs")
    //private List<ISRC> isrcs;    // from search query=isrc - ERROR!
    private List<String> isrcs;// from lookup inc=isrcs

    //inc=aliases
    @Json(name = "aliases")
    private List<Alias> aliases;

    //inc=annotation
    @Json(name = "annotation")
    private String annotation;

    //inc=ratings
    @Json(name = "rating")
    private Rating rating;

    //inc=user-ratings
    @Json(name = "user-rating")
    private Rating userRating;

    //inc=tags
    @Json(name = "tags")
    private List<Tag> tags;

    //inc=user-tags
    @Json(name = "user-tags")
    private List<Tag> userTags;

    //inc=genres
    @Json(name = "genres")
    private List<Tag> genres;

    //inc=user-genres
    @Json(name = "user-genres")
    private List<Tag> userGenres;

    //inc=artists
    //inc=artist-credits
    @Json(name = "artist-credit")
    private List<Artist.ArtistCredit> artistCredits;

    //inc=releases
    @Json(name = "releases")
    private List<Release> releases;

    public Recording() {
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public String getDisambiguation() {
        return disambiguation;
    }

    public void setDisambiguation(String disambiguation) {
        this.disambiguation = disambiguation;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Artist.ArtistCredit> getArtistCredits() {
        return artistCredits;
    }

    public void setArtistCredits(List<Artist.ArtistCredit> artistCredits) {
        this.artistCredits = artistCredits;
    }

    public List<Release> getReleases() {
        return releases;
    }

    public void setReleases(List<Release> releases) {
        this.releases = releases;
    }

    public List<String> getIsrcs() {
        return isrcs;
    }

    public void setIsrcs(List<String> isrcs) {
        this.isrcs = isrcs;
    }

    public Boolean getVideo() {
        return video;
    }

    public void setVideo(Boolean video) {
        this.video = video;
    }

    public List<Alias> getAliases() {
        return aliases;
    }

    public void setAliases(List<Alias> aliases) {
        this.aliases = aliases;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public Rating getUserRating() {
        return userRating;
    }

    public void setUserRating(Rating userRating) {
        this.userRating = userRating;
    }

    public List<Tag> getUserTags() {
        return userTags;
    }

    public void setUserTags(List<Tag> userTags) {
        this.userTags = userTags;
    }

    public List<Tag> getGenres() {
        return genres;
    }

    public void setGenres(List<Tag> genres) {
        this.genres = genres;
    }

    public List<Tag> getUserGenres() {
        return userGenres;
    }

    public void setUserGenres(List<Tag> userGenres) {
        this.userGenres = userGenres;
    }
}
