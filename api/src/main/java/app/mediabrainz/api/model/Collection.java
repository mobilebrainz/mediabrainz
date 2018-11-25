package app.mediabrainz.api.model;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * lookup example: https://musicbrainz.org/ws/2/collection/0a5c389a-fd5b-4901-83e7-171419318172?fmt=json
 */

public class Collection  extends BaseLookupEntity {

    public static class CollectionBrowse {

        @Json(name = "collection-count")
        protected int count;

        @Json(name = "collection-offset")
        protected int offset;

        @Json(name = "collections")
        private List<Collection> collections;

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

        public List<Collection> getCollections() {
            return collections;
        }

        public void setCollections(List<Collection> collections) {
            this.collections = collections;
        }
    }
    // entity-types
    public static final String AREA_ENTITY_TYPE = "area";
    public static final String ARTIST_ENTITY_TYPE = "artist";
    public static final String EVENT_ENTITY_TYPE = "event";
    public static final String INSTRUMENT_ENTITY_TYPE = "instrument";
    public static final String LABEL_ENTITY_TYPE = "label";
    public static final String PLACE_ENTITY_TYPE = "place";
    public static final String RECORDING_ENTITY_TYPE = "recording";
    public static final String RELEASE_ENTITY_TYPE = "release";
    public static final String RELEASE_GROUP_ENTITY_TYPE = "release_group";
    public static final String SERIES_ENTITY_TYPE = "series";
    public static final String WORK_ENTITY_TYPE = "work";

    // types
    public static final String AREA_TYPE = "Area";
    public static final String ARTIST_TYPE = "Artist";
    public static final String EVENT_TYPE = "Event";
    public static final String ATTENDING_EVENT_TYPE = "Attending";
    public static final String MAYBE_ATTENDING_EVENT_TYPE = "Maybe attending";
    public static final String INSTRUMENT_TYPE = "Instrument";
    public static final String LABEL_TYPE = "Label";
    public static final String PLACE_TYPE = "Place";
    public static final String RECORDING_TYPE = "Recording";
    public static final String RELEASE_TYPE = "Release";
    public static final String OWNED_RELEASE_TYPE = "Owned music";
    public static final String WISHLIST_RELEASE_TYPE = "Wishlist";
    public static final String RELEASE_GROUP_TYPE = "Release group";
    public static final String SERIES_TYPE = "Series";
    public static final String WORK_TYPE = "Work";

    @Json(name = "name")
    private String name;

    @Json(name = "editor")
    private String editor;

    @Json(name = "type")
    private String type;

    @Json(name = "type-id")
    private String typeId;

    @Json(name = "entity-type")
    private String entityType;

    // counts:
    @Json(name = "area-count")
    private int areaCount;

    @Json(name = "artist-count")
    private int artistCount;

    @Json(name = "event-count")
    private int eventCount;

    @Json(name = "instrument-count")
    private int instrumentCount;

    @Json(name = "label-count")
    private int labelCount;

    @Json(name = "place-count")
    private int placeCount;

    @Json(name = "recording-count")
    private int recordingCount;

    @Json(name = "release-count")
    private int releaseCount;

    @Json(name = "release-group-count")
    private int releaseGroupCount;

    @Json(name = "series-count")
    private int seriesCount;

    @Json(name = "work-count")
    private int workCount;

    private int count;

    public Collection() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
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

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public int getAreaCount() {
        return areaCount;
    }

    public void setAreaCount(int areaCount) {
        this.areaCount = areaCount;
    }

    public int getArtistCount() {
        return artistCount;
    }

    public void setArtistCount(int artistCount) {
        this.artistCount = artistCount;
    }

    public int getEventCount() {
        return eventCount;
    }

    public void setEventCount(int eventCount) {
        this.eventCount = eventCount;
    }

    public int getInstrumentCount() {
        return instrumentCount;
    }

    public void setInstrumentCount(int instrumentCount) {
        this.instrumentCount = instrumentCount;
    }

    public int getLabelCount() {
        return labelCount;
    }

    public void setLabelCount(int labelCount) {
        this.labelCount = labelCount;
    }

    public int getPlaceCount() {
        return placeCount;
    }

    public void setPlaceCount(int placeCount) {
        this.placeCount = placeCount;
    }

    public int getRecordingCount() {
        return recordingCount;
    }

    public void setRecordingCount(int recordingCount) {
        this.recordingCount = recordingCount;
    }

    public int getReleaseCount() {
        return releaseCount;
    }

    public void setReleaseCount(int releaseCount) {
        this.releaseCount = releaseCount;
    }

    public int getReleaseGroupCount() {
        return releaseGroupCount;
    }

    public void setReleaseGroupCount(int releaseGroupCount) {
        this.releaseGroupCount = releaseGroupCount;
    }

    public int getSeriesCount() {
        return seriesCount;
    }

    public void setSeriesCount(int seriesCount) {
        this.seriesCount = seriesCount;
    }

    public int getWorkCount() {
        return workCount;
    }

    public void setWorkCount(int workCount) {
        this.workCount = workCount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
