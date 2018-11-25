package app.mediabrainz.api.model;

import com.squareup.moshi.Json;

/**
 * The begin and end dates indicate when an artist started and finished its existence.
 * Its exact meaning depends on the type of artist:
     For a person -
        Begin date represents date of birth, and end date represents date of death.
     For a group (or orchestra/choir) -
        Begin date represents the date when the group first formed: if a group dissolved and then reunited, the date is still that of when they first formed. End date represents the date when the group last dissolved: if a group dissolved and then reunited, the date is that of when they last dissolved (if they are together, it should be blank!). For listing other inactivity periods, just use the annotation and the "member of" relationships.
     For a character -
        Begin date represents the date (in real life) when the character concept was created. The End date should not be set, since new media featuring a character can be created at any time. In particular, the Begin and End date fields should not be used to hold the fictional birth or death dates of a character. (This information can be put in the annotation.)
     For others -
        There are no clear indications about how to use dates for artists of the type Other at the moment.
 */

public class LifeSpan {

    @Json(name = "ended")
    private Boolean ended = false;

    @Json(name = "begin")
    private String begin = "";

    @Json(name = "end")
    private String end = "";

    @SuppressWarnings("unused") // Moshi uses this!
    public LifeSpan() {
    }

    public Boolean getEnded() {
        return ended;
    }

    public void setEnded(Boolean ended) {
        this.ended = ended;
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
}
