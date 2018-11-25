package app.mediabrainz.api.model;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * https://musicbrainz.org/doc/Area
 */

public class Area extends BaseLookupEntity {

    public static class AreaSearch extends BaseSearch {
        @Json(name = "areas")
        private List<Area> areas;

        public List<Area> getAreas() {
            return areas;
        }

        public void setAreas(List<Area> areas) {
            this.areas = areas;
        }
    }

    public static class AreaBrowse {

        @Json(name = "area-count")
        protected int count;

        @Json(name = "area-offset")
        protected int offset;

        @Json(name = "areas")
        private List<Area> areas;

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

        public List<Area> getAreas() {
            return areas;
        }

        public void setAreas(List<Area> areas) {
            this.areas = areas;
        }
    }

    public enum Type {
        /**
         * Country is used for areas included (or previously included) in ISO 3166-1,
         * e.g. United States.
         */
        COUNTRY("country"),

        /**
         * Subdivision is used for the main administrative divisions of a country,
         * e.g. California, Ontario, Okinawa. These are considered when displaying the parent areas
         * for a given area.
         */
        SUBDIVISION("cubdivision"),

        /**
         * County is used for smaller administrative divisions of a country which are
         * not the main administrative divisions but are also not municipalities,
         * e.g. counties in the USA. These are not considered when displaying the parent areas for a given area.
         */
        COUNTY("county"),

        /**
         * Municipality is used for small administrative divisions which, for urban municipalities,
         * often contain a single city and a few surrounding villages. Rural municipalities typically
         * group several villages together.

         */
        MUNICIPALITY("municipality"),

        /**
         * City is used for settlements of any size, including towns and villages.
         */
        CITY("city"),

        /**
         * District is used for a division of a large city, e.g. Queens.
         */
        DISTRICT("district"),

        /**
         * Island is used for islands and atolls which don't form subdivisions of their own,
         * e.g. Skye. These are not considered when displaying the parent areas for a given area.
         */
        ISLAND("island");

        private final String type;
        Type (String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type;
        }
    }

    //Area.AlbumType
    @Json(name = "type")
    private String type;

    @Json(name = "type-id")
    private String typeId;

    @Json(name = "score")
    private int score;

    @Json(name = "disambiguation")
    private String disambiguation = "";

    @Json(name = "name")
    private String name = "";

    @Json(name = "sort-name")
    private String sortName = "";

    @Json(name = "life-span")
    private LifeSpan lifeSpan;

    @Json(name = "iso-3166-1-codes")
    private List<String > iso1;

    @Json(name = "iso-3166-2-codes")
    private List<String > iso2;

    @Json(name = "iso-3166-3-codes")
    private List<String > iso3;

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

    public Area() {
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public LifeSpan getLifeSpan() {
        return lifeSpan;
    }

    public void setLifeSpan(LifeSpan lifeSpan) {
        this.lifeSpan = lifeSpan;
    }

    public List<String> getIso1() {
        return iso1;
    }

    public void setIso1(List<String> iso1) {
        this.iso1 = iso1;
    }

    public List<String> getIso2() {
        return iso2;
    }

    public void setIso2(List<String> iso2) {
        this.iso2 = iso2;
    }

    public List<String> getIso3() {
        return iso3;
    }

    public void setIso3(List<String> iso3) {
        this.iso3 = iso3;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
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
