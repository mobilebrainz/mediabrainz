package app.mediabrainz.api.model;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * https://musicbrainz.org/doc/Label
 */

public class Label extends BaseLookupEntity {

    public static class LabelSearch extends BaseSearch {
        @Json(name = "labels")
        private List<Label> labels;

        public List<Label> getLabels() {
            return labels;
        }

        public void setLabels(List<Label> labels) {
            this.labels = labels;
        }
    }

    public static class LabelBrowse {

        @Json(name = "label-count")
        protected int count;

        @Json(name = "label-offset")
        protected int offset;

        @Json(name = "labels")
        private List<Label> labels;

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

        public List<Label> getLabels() {
            return labels;
        }

        public void setLabels(List<Label> labels) {
            this.labels = labels;
        }
    }

    public static class LabelInfo {
        @Json(name = "catalog-number")
        private String catalogNumber;

        @Json(name = "label")
        private Label label;

        public LabelInfo() {
        }

        public Label getLabel() {
            return label;
        }

        public void setLabel(Label label) {
            this.label = label;
        }

        public String getCatalogNumber() {
            return catalogNumber;
        }

        public void setCatalogNumber(String catalogNumber) {
            this.catalogNumber = catalogNumber;
        }
    }

    public enum LabelType {
        IMPRINT("imprint"),
        PRODUCTION("production"),
        ORIGINAL_PRODUCTION("original production"),
        BOOTLEG_PRODUCTION("bootleg production"),
        REISSUE_PRODUCTION("reissue production"),
        DISTRIBUTOR("distributor"),
        HOLDING("holding"),
        RIGHTS_SOCIETY("rights society");

        private final String type;
        LabelType(String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type;
        }
    }

    @Json(name = "name")
    private String name;

    @Json(name = "sort-name")
    private String sortName;

    // Label.LabelType
    @Json(name = "type")
    private String type;

    @Json(name = "type-id")
    private String typeId;

    @Json(name = "score")
    private int score;

    @Json(name = "country")
    private String country;

    @Json(name = "area")
    private Area area;

    @Json(name = "life-span")
    private LifeSpan lifeSpan;

    @Json(name = "label-code")
    private String labelCode;

    @Json(name = "disambiguation")
    private String disambiguation;

    @Json(name = "isnis")
    private List<String> isnis;

    @Json(name = "ipis")
    private List<String> ipis;

    //inc=releases
    @Json(name = "releases")
    private List<Release> releases;

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

    public Label() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public LifeSpan getLifeSpan() {
        return lifeSpan;
    }

    public void setLifeSpan(LifeSpan lifeSpan) {
        this.lifeSpan = lifeSpan;
    }

    public String getDisambiguation() {
        return disambiguation;
    }

    public void setDisambiguation(String disambiguation) {
        this.disambiguation = disambiguation;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getLabelCode() {
        return labelCode;
    }

    public void setLabelCode(String labelCode) {
        this.labelCode = labelCode;
    }

    public List<String> getIsnis() {
        return isnis;
    }

    public void setIsnis(List<String> isnis) {
        this.isnis = isnis;
    }

    public List<String> getIpis() {
        return ipis;
    }

    public void setIpis(List<String> ipis) {
        this.ipis = ipis;
    }

    public List<Release> getReleases() {
        return releases;
    }

    public void setReleases(List<Release> releases) {
        this.releases = releases;
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
}
