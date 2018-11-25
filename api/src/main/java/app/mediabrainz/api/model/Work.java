package app.mediabrainz.api.model;

import com.squareup.moshi.Json;

import java.util.List;


public class Work extends BaseLookupEntity {

    public static class WorkSearch extends BaseSearch {

        @Json(name = "works")
        private List<Work> works;

        public List<Work> getWorks() {
            return works;
        }

        public void setWorks(List<Work> works) {
            this.works = works;
        }
    }

    public static class WorkBrowse extends BaseSearch {

        @Json(name = "work-count")
        protected int count;

        @Json(name = "work-offset")
        protected int offset;

        @Json(name = "works")
        private List<Work> works;

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public void setCount(int count) {
            this.count = count;
        }

        @Override
        public int getOffset() {
            return offset;
        }

        @Override
        public void setOffset(int offset) {
            this.offset = offset;
        }

        public List<Work> getWorks() {
            return works;
        }

        public void setWorks(List<Work> works) {
            this.works = works;
        }
    }

    public static class Attribute {
        @Json(name = "type")
        private String type;

        @Json(name = "type-id")
        private String typeId;

        @Json(name = "value")
        private String value;

        public Attribute() {
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTypeId() {
            return typeId;
        }

        public void setTypeId(String typeId) {
            this.typeId = typeId;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @Json(name = "score")
    private int score;

    @Json(name = "title")
    private String title;

    @Json(name = "language")
    private String language;

    @Json(name = "languages")
    private List<String> languages;

    @Json(name = "disambiguation")
    private String disambiguation;

    @Json(name = "type")
    private String type;

    @Json(name = "type-id")
    private String typeId;

    @Json(name = "iswcs")
    private List<String> iswcs;

    @Json(name = "attributes")
    private List<Attribute> attributes;

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

    public Work() {
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<String> getIswcs() {
        return iswcs;
    }

    public void setIswcs(List<String> iswcs) {
        this.iswcs = iswcs;
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

    public String getDisambiguation() {
        return disambiguation;
    }

    public void setDisambiguation(String disambiguation) {
        this.disambiguation = disambiguation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }
}
