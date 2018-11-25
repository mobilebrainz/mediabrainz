package app.mediabrainz.api.model;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * https://musicbrainz.org/doc/Place
 */

public class Place extends BaseLookupEntity {

    public static class PlaceSearch extends BaseSearch {
        @Json(name = "places")
        private List<Place> places;

        public List<Place> getPlaces() {
            return places;
        }

        public void setPlaces(List<Place> places) {
            this.places = places;
        }
    }

    public static class PlaceBrowse {

        @Json(name = "place-count")
        protected int count;

        @Json(name = "place-offset")
        protected int offset;

        @Json(name = "places")
        private List<Place> places;

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

        public List<Place> getPlaces() {
            return places;
        }

        public void setPlaces(List<Place> places) {
            this.places = places;
        }
    }

    public enum PlaceType {
        STUDIO("Studio"),
        VENUE("Venue"),
        STADIUM("Stadium"),
        INDOOR_ARENA("Indoor arena"),
        RELIGIOUS_BUILDING("Religious building"),
        OTHER("Other");

        private final String type;
        PlaceType(String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type;
        }
    }

    //Place.PlaceType
    @Json(name = "type")
    private String type = "";

    @Json(name = "score")
    private int score;

    @Json(name = "name")
    private String name = "";

    @Json(name = "address")
    private String address = "";

    @Json(name = "coordinates")
    private Coordinate coordinate;

    @Json(name = "area")
    private Area area;

    @Json(name = "life-span")
    private LifeSpan lifeSpan;

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

    public Place() {
    }

    public static class Coordinate {

        @Json(name = "latitude")
        private double latitude;

        @Json(name = "longitude")
        private double longitude;

        public Coordinate() {
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
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

    public List<Alias> getAliases() {
        return aliases;
    }

    public void setAliases(List<Alias> aliases) {
        this.aliases = aliases;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public List<Tag> getUserTags() {
        return userTags;
    }

    public void setUserTags(List<Tag> userTags) {
        this.userTags = userTags;
    }
}
