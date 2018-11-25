package app.mediabrainz.api.model;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * https://musicbrainz.org/doc/CD_Stub
 */

public class CDStub {

    public static class CDStubSearch extends BaseSearch {
        @Json(name = "cdstubs")
        private List<CDStub> cdStubs;

        public List<CDStub> getCdStubs() {
            return cdStubs;
        }

        public void setCdStubs(List<CDStub> cdStubs) {
            this.cdStubs = cdStubs;
        }
    }


    @Json(name = "id")
    private String id;

    @Json(name = "title")
    private String title = "";

    @Json(name = "artist")
    private String artist = "";

    @Json(name = "score")
    private int score;

    @Json(name = "count")
    private int count;

    @Json(name = "barcode")
    private String barcode = "";

    @Json(name = "comment")
    private String comment = "";

    public CDStub() {
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

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
