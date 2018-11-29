package app.mediabrainz.api.externalResources.lyrics.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root(name="LyricsResult", strict=false)
public class LyricsApi {

    public static final String LYRICS_NOT_FOUND = "Not found";
    public static final String LYRICS_INSTRUMENTAL = "Instrumental";

    @Element(name="artist")
    private String artist;

    @Element(name="song")
    private String song;

    @Element(name="lyrics")
    private String lyrics;

    @Element(name="url")
    private String url;

    public LyricsApi() {
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
