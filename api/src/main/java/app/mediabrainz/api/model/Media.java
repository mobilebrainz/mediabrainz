package app.mediabrainz.api.model;

import com.squareup.moshi.Json;

import java.util.List;


public class Media {

    // TODO: add formats
    public enum Format {
        CD("CD"),
        DIGITAL_MEDIA("Digital Media"),
        VINYL("Vinyl");

        private final String type;

        Format(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    public static class Track {

        @Json(name = "id")
        private String id;

        @Json(name = "title")
        private String title;

        @Json(name = "recording")
        private Recording recording;

        @Json(name = "position")
        private int position;

        @Json(name = "length")
        private Long length;

        @Json(name = "number")
        private String number;

        public Track() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Recording getRecording() {
            return recording;
        }

        public void setRecording(Recording recording) {
            this.recording = recording;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public Long getLength() {
            return length;
        }

        public void setLength(Long length) {
            this.length = length;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }
    }

    @Json(name = "title")
    private String title;

    /**
     * Media.Format
     */
    @Json(name = "format")
    private String format;

    @Json(name = "position")
    private Integer position;

    @Json(name = "format-id")
    private String formatId;

    @Json(name = "disc-count")
    private int discCount;

    @Json(name = "track-count")
    private int trackCount;

    @Json(name = "track-offset")
    private int trackOffset;

    @Json(name = "tracks")
    private List<Track> tracks;

    //inc=discs
    @Json(name = "discs")
    private List<Disc> discs;

    public Media() {
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getDiscCount() {
        return discCount;
    }

    public void setDiscCount(int discCount) {
        this.discCount = discCount;
    }

    public int getTrackCount() {
        return trackCount;
    }

    public void setTrackCount(int trackCount) {
        this.trackCount = trackCount;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFormatId() {
        return formatId;
    }

    public void setFormatId(String formatId) {
        this.formatId = formatId;
    }

    public int getTrackOffset() {
        return trackOffset;
    }

    public void setTrackOffset(int trackOffset) {
        this.trackOffset = trackOffset;
    }

    public List<Disc> getDiscs() {
        return discs;
    }

    public void setDiscs(List<Disc> discs) {
        this.discs = discs;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
