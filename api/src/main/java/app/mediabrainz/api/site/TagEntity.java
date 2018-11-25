package app.mediabrainz.api.site;

import java.util.ArrayList;
import java.util.List;


public class TagEntity {

    public static class Page {

        private int current = 1;
        private int count = 1;
        private List<TagEntity> tagEntities = new ArrayList<>();

        public Page() {
        }

        public List<TagEntity> getTagEntities() {
            return tagEntities;
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

    private int voteCount;
    private String mbid;
    private String name;

    private String artistMbid;
    private String artistName;
    private String artistComment;

    public TagEntity() {
    }

    public TagEntity(String mbid, String name) {
        this.mbid = mbid;
        this.name = name;
    }

    public TagEntity(String mbid, String name, int voteCount) {
        this.voteCount = voteCount;
        this.mbid = mbid;
        this.name = name;
    }

    public String getMbid() {
        return mbid;
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

    public String getArtistComment() {
        return artistComment;
    }

    public void setArtistComment(String artistComment) {
        this.artistComment = artistComment;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }
}
