package app.mediabrainz.api.model;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * https://musicbrainz.org/doc/Artist
 */

public class Artist extends BaseLookupEntity {

    /**
     * The type is used to state whether an artist is a person, a group, or something else.
     * Note that not every ensemble related to classical music is an orchestra or choir.
     * The Borodin Quartet and The Hilliard Ensemble, for example, are simply groups.
     */
    public enum ArtistType {
        /**
         * indicates an individual person.
         */
        PERSON("Person"),

        /**
         * indicates a group of people that may or may not have a distinctive name.
         */
        GROUP("Group"),

        /**
         * indicates an orchestra (a large instrumental ensemble).
         */
        ORCHESTRA("Orchestra"),

        /**
         * indicates a choir/chorus (a large vocal ensemble).
         */
        CHOIR("Choir"),

        /**
         * indicates an individual fictional character.
         */
        CHARACTER("Character"),

        /**
         * Anything which does not fit into the above categories.
         */
        OTHER("Other");

        private final String type;
        ArtistType(String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type;
        }
    }

    public static class ArtistSearch extends BaseSearch {
        @Json(name = "artists")
        private List<Artist> artists;

        public List<Artist> getArtists() {
            return artists;
        }

        public void setArtists(List<Artist> artists) {
            this.artists = artists;
        }
    }

    public static class ArtistBrowse {

        @Json(name = "artist-count")
        protected int count;

        @Json(name = "artist-offset")
        protected int offset;

        @Json(name = "artists")
        private List<Artist> artists;

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

        public List<Artist> getArtists() {
            return artists;
        }

        public void setArtists(List<Artist> artists) {
            this.artists = artists;
        }
    }

    public static class ArtistCredit {

        @Json(name = "joinphrase")
        private String joinphrase;

        @Json(name = "name")
        private String name;

        @Json(name = "artist")
        private Artist artist;

        public ArtistCredit() {
        }

        public String getJoinphrase() {
            return joinphrase;
        }

        public void setJoinphrase(String joinphrase) {
            this.joinphrase = joinphrase;
        }

        public Artist getArtist() {
            return artist;
        }

        public void setArtist(Artist artist) {
            this.artist = artist;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Json(name = "name")
    private String name;

    @Json(name = "sort-name")
    private String sortName = "";

    @Json(name = "disambiguation")
    private String disambiguation = "";

    //Artist.AlbumType
    @Json(name = "type")
    private String type = "";

    @Json(name = "type-id")
    private String typeId = "";

    @Json(name = "country")
    private String country = "";

    @Json(name = "score")
    private int score;

    @Json(name = "gender")
    private String gender = "";

    @Json(name = "area")
    private Area area;

    @Json(name = "begin_area")
    private Area beginArea;

    @Json(name = "end_area")
    private Area endArea;

    @Json(name = "life-span")
    private LifeSpan lifeSpan;

    @Json(name = "isnis")
    private List<String> isnis;

    @Json(name = "ipis")
    private List<String> ipis;

    //inc=works
    @Json(name = "works")
    private List<Work> works;

    //inc=recordings
    @Json(name = "recordings")
    private List<Recording> recordings;

    //inc=releases
    @Json(name = "releases")
    private List<Release> releases;

    //inc=release-groups
    @Json(name = "release-groups")
    private List<ReleaseGroup> releaseGroups;

    //inc=aliases
    @Json(name = "aliases")
    private List<Alias> aliases;

    //inc=annotation
    @Json(name = "annotation")
    private String annotation;

    //inc=ratings
    @Json(name = "rating")
    private Rating rating;

    //inc=user-ratings
    @Json(name = "user-rating")
    private Rating userRating;

    //inc=tags
    @Json(name = "tags")
    private List<Tag> tags;

    //inc=user-tags
    @Json(name = "user-tags")
    private List<Tag> userTags;

    //inc=genres
    @Json(name = "genres")
    private List<Tag> genres;

    //inc=user-genres
    @Json(name = "user-genres")
    private List<Tag> userGenres;

    public Artist() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisambiguation() {
        return disambiguation;
    }

    public void setDisambiguation(String disambiguation) {
        this.disambiguation = disambiguation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public Area getBeginArea() {
        return beginArea;
    }

    public void setBeginArea(Area beginArea) {
        this.beginArea = beginArea;
    }

    public Area getEndArea() {
        return endArea;
    }

    public void setEndArea(Area endArea) {
        this.endArea = endArea;
    }

    public LifeSpan getLifeSpan() {
        return lifeSpan;
    }

    public void setLifeSpan(LifeSpan lifeSpan) {
        this.lifeSpan = lifeSpan;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<String> getIsnis() {
        return isnis;
    }

    public void setIsnis(List<String> isnis) {
        this.isnis = isnis;
    }

    public List<String> getIpis() {
        return ipis;
    }

    public void setIpis(List<String> ipis) {
        this.ipis = ipis;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public List<Work> getWorks() {
        return works;
    }

    public void setWorks(List<Work> works) {
        this.works = works;
    }

    public List<Recording> getRecordings() {
        return recordings;
    }

    public void setRecordings(List<Recording> recordings) {
        this.recordings = recordings;
    }

    public List<Release> getReleases() {
        return releases;
    }

    public void setReleases(List<Release> releases) {
        this.releases = releases;
    }

    public List<ReleaseGroup> getReleaseGroups() {
        return releaseGroups;
    }

    public void setReleaseGroups(List<ReleaseGroup> releaseGroups) {
        this.releaseGroups = releaseGroups;
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

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public Rating getUserRating() {
        return userRating;
    }

    public void setUserRating(Rating userRating) {
        this.userRating = userRating;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Tag> getGenres() {
        return genres;
    }

    public void setGenres(List<Tag> genres) {
        this.genres = genres;
    }

    public List<Tag> getUserGenres() {
        return userGenres;
    }

    public void setUserGenres(List<Tag> userGenres) {
        this.userGenres = userGenres;
    }

    public List<Tag> getUserTags() {
        return userTags;
    }

    public void setUserTags(List<Tag> userTags) {
        this.userTags = userTags;
    }
}
