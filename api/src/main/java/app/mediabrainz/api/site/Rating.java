package app.mediabrainz.api.site;

import java.util.ArrayList;
import java.util.List;


public class Rating {

    public static class Page {

        private int current = 1;
        private int count = 1;
        private List<Rating> ratings = new ArrayList<>();

        public Page() {
        }

        public List<Rating> getRatings() {
            return ratings;
        }

        public int getCurrent() {
            return current;
        }

        public void setCurrent(int current) {
            this.current = current;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    private String user;
    private float rate;
    private String mbid;
    private String name;
    private String artistMbid;
    private String artistName;
    private String artistComment;

    public Rating(String user, String mbid, String name, float rate) {
        this.user = user;
        this.mbid = mbid;
        this.name = name;
        this.rate = rate;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMbid() {
        return mbid;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtistMbid() {
        return artistMbid;
    }

    public void setArtistMbid(String artistMbid) {
        this.artistMbid = artistMbid;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getArtistComment() {
        return artistComment;
    }

    public void setArtistComment(String artistComment) {
        this.artistComment = artistComment;
    }
}
