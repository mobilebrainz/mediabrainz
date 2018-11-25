package app.mediabrainz.api.coverart;

import com.squareup.moshi.Json;

import java.util.List;


public class CoverArtImage {

    public enum CoverArtType {

        FRONT("Front"),
        BACK("Back"),
        BOOKLET("Booklet"),
        MEDIUM("Medium"),
        TRAY("Tray"),
        OBI("Obi"),
        SPINE("Spine"),
        TRACK("Track"),
        LINER("Liner"),
        STICKER("Sticker"),
        POSTER("Poster"),
        WATERMARK("Watermark"),
        OTHER("Other");

        private final String type;
        CoverArtType(String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type;
        }
    }

    public static class Thumbnails {

        public static final int SMALL_SIZE = 250;
        public static final int LARGE_SIZE = 500;

        @Json(name = "small")
        private String small;

        @Json(name = "large")
        private String large;

        public String getSmall() {
            return small;
        }

        public void setSmall(String small) {
            this.small = small;
        }

        public String getLarge() {
            return large;
        }

        public void setLarge(String large) {
            this.large = large;
        }
    }

    @Json(name = "id")
    private String id;

    @Json(name = "image")
    private String image;

    @Json(name = "edit")
    private Long edit;

    @Json(name = "types")
    private List<String> types;

    @Json(name = "front")
    private Boolean front;

    @Json(name = "back")
    private Boolean back;

    @Json(name = "approved")
    private Boolean approved;

    @Json(name = "comment")
    private String comment;

    @Json(name = "thumbnails")
    private Thumbnails thumbnails;

    public CoverArtImage() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getEdit() {
        return edit;
    }

    public void setEdit(Long edit) {
        this.edit = edit;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public Boolean getFront() {
        return front;
    }

    public void setFront(Boolean front) {
        this.front = front;
    }

    public Boolean getBack() {
        return back;
    }

    public void setBack(Boolean back) {
        this.back = back;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Thumbnails getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(Thumbnails thumbnails) {
        this.thumbnails = thumbnails;
    }
}
