package app.mediabrainz.api.model.relations;

import com.squareup.moshi.Json;

import java.util.List;
import java.util.Map;

import app.mediabrainz.api.model.Area;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.Event;
import app.mediabrainz.api.model.Instrument;
import app.mediabrainz.api.model.Label;
import app.mediabrainz.api.model.Place;
import app.mediabrainz.api.model.Recording;
import app.mediabrainz.api.model.Release;
import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.api.model.Series;
import app.mediabrainz.api.model.Url;
import app.mediabrainz.api.model.Work;


public class Relation {

    /**
     * https://musicbrainz.org/relationship-attributes
     */
    @Json(name = "attributes")
    private List<String> attributes;

    @Json(name = "attribute-credits")
    private Map<String, String> attributeCredits;

    /**
     * attribute-values: Map<String, String> - значения аттрибутов, имена которых заданы в attributes:
     * attributes["time"],  attribute-values{time: "23:45"} (inc=event-rels)
     */
    @Json(name = "attribute-values")
    private Map<String, String> attributeValues;

    @Json(name = "begin")
    private String begin;

    @Json(name = "direction")
    private String direction;

    @Json(name = "end")
    private String end;

    @Json(name = "ended")
    private Boolean ended;

    @Json(name = "source-credit")
    private String sourceCredit;

    @Json(name = "target-type")
    private String targetType;

    @Json(name = "type")
    private String type;

    @Json(name = "type-id")
    private String typeId;

    @Json(name = "area")
    private Area area;

    @Json(name = "artist")
    private Artist artist;

    @Json(name = "event")
    private Event event;

    @Json(name = "instrument")
    private Instrument instrument;

    @Json(name = "label")
    private Label label;

    @Json(name = "place")
    private Place place;

    @Json(name = "recording")
    private Recording recording;

    @Json(name = "release")
    private Release release;

    @Json(name = "release_group")
    private ReleaseGroup releaseGroup;

    @Json(name = "url")
    private Url url;

    @Json(name = "work")
    private Work work;

    @Json(name = "series")
    private Series series;

    public Relation() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Recording getRecording() {
        return recording;
    }

    public void setRecording(Recording recording) {
        this.recording = recording;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Boolean getEnded() {
        return ended;
    }

    public void setEnded(Boolean ended) {
        this.ended = ended;
    }

    public String getSourceCredit() {
        return sourceCredit;
    }

    public void setSourceCredit(String sourceCredit) {
        this.sourceCredit = sourceCredit;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public Release getRelease() {
        return release;
    }

    public void setRelease(Release release) {
        this.release = release;
    }

    public ReleaseGroup getReleaseGroup() {
        return releaseGroup;
    }

    public void setReleaseGroup(ReleaseGroup releaseGroup) {
        this.releaseGroup = releaseGroup;
    }

    public Url getUrl() {
        return url;
    }

    public void setUrl(Url url) {
        this.url = url;
    }

    public Work getWork() {
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
    }

    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series) {
        this.series = series;
    }

    public Map<String, String> getAttributeCredits() {
        return attributeCredits;
    }

    public void setAttributeCredits(Map<String, String> attributeCredits) {
        this.attributeCredits = attributeCredits;
    }

    public Map<String, String> getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValues(Map<String, String> attributeValues) {
        this.attributeValues = attributeValues;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }
}
