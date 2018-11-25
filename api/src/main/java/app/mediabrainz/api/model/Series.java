package app.mediabrainz.api.model;

import com.squareup.moshi.Json;

import java.util.List;


public class Series extends BaseLookupEntity {

    public static class SeriesSearch extends BaseSearch {
        @Json(name = "series")
        private List<Series> series;

        public List<Series> getSeries() {
            return series;
        }

        public void setSeries(List<Series> series) {
            this.series = series;
        }
    }

    public static class SeriesBrowse {

        @Json(name = "series-count")
        protected int count;

        @Json(name = "series-offset")
        protected int offset;

        @Json(name = "series")
        private List<Series> series;

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

        public List<Series> getSeries() {
            return series;
        }

        public void setSeries(List<Series> series) {
            this.series = series;
        }
    }

    public enum SeriesType {
        RELEASE_GROUP("Release group"),
        RELEASE("Release"),
        RECORDING("Recording"),
        WORK("Work"),
        CATALOQUE("Catalogue"),
        EVENT("Event"),
        TOUR("Tour"),
        FESTIVAL("Festival"),
        RUN("Run");

        private final String type;
        SeriesType(String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type;
        }
    }

    @Json(name = "score")
    private int score;

    @Json(name = "name")
    private String name;

    //Series.SeriesType
    @Json(name = "type")
    private String type;

    @Json(name = "type-id")
    private String typeId;

    @Json(name = "disambiguation")
    private String disambiguation;

    //inc=aliases
    @Json(name = "aliases")
    private List<Alias> aliases;

    //inc=annotation
    @Json(name = "annotation")
    private String annotation;

    //inc=tags
    @Json(name = "tags")
    private List<Tag> tags;

    //inc=user-tags
    @Json(name = "user-tags")
    private List<Tag> userTags;

    public Series() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisambiguation() {
        return disambiguation;
    }

    public void setDisambiguation(String disambiguation) {
        this.disambiguation = disambiguation;
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

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Tag> getUserTags() {
        return userTags;
    }

    public void setUserTags(List<Tag> userTags) {
        this.userTags = userTags;
    }
}
