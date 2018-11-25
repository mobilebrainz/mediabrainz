package app.mediabrainz.api.model;

import com.squareup.moshi.Json;

import java.util.List;


public class Instrument extends BaseLookupEntity {

    public static class InstrumentSearch extends BaseSearch {
        @Json(name = "instruments")
        private List<Instrument> instruments;

        public List<Instrument> getInstruments() {
            return instruments;
        }

        public void setInstruments(List<Instrument> instruments) {
            this.instruments = instruments;
        }
    }

    public enum InstrumentType {
        WIND_INSTRUMENT("Wind instrument"),
        STRING_INSTRUMENT("String instrument"),
        PERCUSSION_INSTRUMENT("Percussion instrument"),
        ELECTRONIC_INSTRUMENT("Electronic instrument"),
        OTHER_INSTRUMENT("Other instrument"),
        UNCLASSIFIED_INSTRUMENT("Unclassified instrument");

        private final String type;
        InstrumentType(String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type;
        }
    }

    @Json(name = "name")
    private String name;

    @Json(name = "score")
    private int score;

    // Instrument.InstrumentType
    @Json(name = "type")
    private String type;

    @Json(name = "type-id")
    private String typeId;

    @Json(name = "description")
    private String description;

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

    public Instrument() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisambiguation() {
        return disambiguation;
    }

    public void setDisambiguation(String disambiguation) {
        this.disambiguation = disambiguation;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
