package app.mediabrainz.api.model;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * https://musicbrainz.org/doc/Release_Group
 */

public class ReleaseGroup extends BaseLookupEntity {

    public static class ReleaseGroupSearch extends BaseSearch {
        @Json(name = "release-groups")
        private List<ReleaseGroup> releaseGroups;

        public List<ReleaseGroup> getReleaseGroups() {
            return releaseGroups;
        }

        public void setReleaseGroups(List<ReleaseGroup> releaseGroups) {
            this.releaseGroups = releaseGroups;
        }
    }

    public static class ReleaseGroupBrowse {

        @Json(name = "release-group-count")
        protected int count;

        @Json(name = "release-group-offset")
        protected int offset;

        @Json(name = "release-groups")
        private List<ReleaseGroup> releaseGroups;

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

        public List<ReleaseGroup> getReleaseGroups() {
            return releaseGroups;
        }

        public void setReleaseGroups(List<ReleaseGroup> releaseGroups) {
            this.releaseGroups = releaseGroups;
        }
    }

    public interface AlbumType {
    }

    /**
     * https://musicbrainz.org/doc/Release_Group/Type
     */
    public enum PrimaryType implements AlbumType {
        ALBUM("album"),
        SINGLE("single"),
        EP("ep"),
        BROADCAST("broadcast"),
        OTHER("other"),

        EMPTY(""),
        ANY("(*)"),
        NOTHING("(-*)");

        private final String type;
        PrimaryType (String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type.toLowerCase();
        }
    }

    /**
     * https://musicbrainz.org/doc/Release_Group/Type
     */
    public enum SecondaryType implements AlbumType {
        COMPILATION("compilation"),
        SOUNDTRACK("soundtrack"),
        SPOKENWORD("spokenword"),
        INTERVIEW("interview"),
        AUDIOBOOK("audiobook"),
        LIVE("live"),
        REMIX("remix"),
        DJ_MIX("dj-mix"),
        MIXTAPE("mixtape"),
        STREET("street"),

        EMPTY(""),
        ANY("(*)"),
        NOTHING("(-*)");

        private final String type;
        SecondaryType (String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type.toLowerCase();
        }
    }

    @Json(name = "count")
    private int count;

    @Json(name = "score")
    private int score;

    @Json(name = "title")
    private String title;

    /**
     * ReleaseGroupXML.PrimaryType
     */
    @Json(name = "primary-type")
    private String primaryType;

    @Json(name = "primary-type-id")
    private String primaryTypeId;

    /**
     * ReleaseGroupXML.SecondaryType
     */
    @Json(name = "secondary-types")
    private List<String> secondaryTypes;

    @Json(name = "secondary-type-ids")
    private List<String> secondaryTypeIds;

    @Json(name = "disambiguation")
    private String disambiguation;

    //"yyyy-mm-dd"
    @Json(name = "first-release-date")
    private String firstReleaseDate;

    //inc=releases
    @Json(name = "releases")
    private List<Release> releases;

    //inc=artists
    @Json(name = "artist-credit")
    private List<Artist.ArtistCredit> artistCredits;

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

    public ReleaseGroup() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrimaryType() {
        return primaryType;
    }

    public void setPrimaryType(String primaryType) {
        this.primaryType = primaryType;
    }

    public List<String> getSecondaryTypes() {
        return secondaryTypes;
    }

    public void setSecondaryTypes(List<String> secondaryTypes) {
        this.secondaryTypes = secondaryTypes;
    }

    public List<Release> getReleases() {
        return releases;
    }

    public void setReleases(List<Release> releases) {
        this.releases = releases;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Artist.ArtistCredit> getArtistCredits() {
        return artistCredits;
    }

    public void setArtistCredits(List<Artist.ArtistCredit> artistCredits) {
        this.artistCredits = artistCredits;
    }

    public String getPrimaryTypeId() {
        return primaryTypeId;
    }

    public void setPrimaryTypeId(String primaryTypeId) {
        this.primaryTypeId = primaryTypeId;
    }

    public List<String> getSecondaryTypeIds() {
        return secondaryTypeIds;
    }

    public void setSecondaryTypeIds(List<String> secondaryTypeIds) {
        this.secondaryTypeIds = secondaryTypeIds;
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

    public List<Tag> getUserTags() {
        return userTags;
    }

    public void setUserTags(List<Tag> userTags) {
        this.userTags = userTags;
    }

    public String getDisambiguation() {
        return disambiguation;
    }

    public void setDisambiguation(String disambiguation) {
        this.disambiguation = disambiguation;
    }

    public String getFirstReleaseDate() {
        return firstReleaseDate;
    }

    public void setFirstReleaseDate(String firstReleaseDate) {
        this.firstReleaseDate = firstReleaseDate;
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

    public Integer getYear() {
        if (firstReleaseDate == null || firstReleaseDate.equals("") ) {
            return 0;
        }
        return Integer.valueOf(firstReleaseDate.substring(0, 4));
    }
}
