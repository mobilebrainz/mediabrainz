package app.mediabrainz.api.model;

import com.squareup.moshi.Json;

import java.util.List;


public class Event extends BaseLookupEntity {

    public static class EventSearch extends BaseSearch {
        @Json(name = "events")
        private List<Event> events;

        public List<Event> getEvents() {
            return events;
        }

        public void setEvents(List<Event> events) {
            this.events = events;
        }
    }

    public static class EventBrowse {

        @Json(name = "event-count")
        protected int count;

        @Json(name = "event-offset")
        protected int offset;

        @Json(name = "events")
        private List<Event> events;

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

        public List<Event> getEvents() {
            return events;
        }

        public void setEvents(List<Event> events) {
            this.events = events;
        }
    }

    public enum EventType {
        CONCERT("Concert"),
        FESTIVAL("Festival"),
        LAUNCH_EVENT("Launch event"),
        // TODO: check Convention/Expo
        CONVENTION("Convention"),
        EXPO("Expo"),
        // TODO: check Masterclass/Clinic
        MASTERCLASS("Masterclass"),
        CLINIC("Clinic");

        private final String type;
        EventType(String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type;
        }
    }

    @Json(name = "name")
    private String name;

    @Json(name = "cancelled")
    private Boolean cancelled;

    @Json(name = "time")
    private String time;

    //Event.EventType
    @Json(name = "type")
    private String type;

    @Json(name = "type-id")
    private String typeId;

    @Json(name = "disambiguation")
    private String disambiguation;

    @Json(name = "setlist")
    private String setlist;

    @Json(name = "life-span")
    private LifeSpan lifeSpan;

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

    public Event() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getCancelled() {
        return cancelled;
    }

    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getDisambiguation() {
        return disambiguation;
    }

    public void setDisambiguation(String disambiguation) {
        this.disambiguation = disambiguation;
    }

    public String getSetlist() {
        return setlist;
    }

    public void setSetlist(String setlist) {
        this.setlist = setlist;
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
