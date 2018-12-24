package app.mediabrainz.api.model;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * https://musicbrainz.org/doc/Release
 */

public class Release extends BaseLookupEntity {

    public enum Status {
        /**
         * Any release officially sanctioned by the artist and/or their record company.
         * Most releases will fit into this category.
         */
        OFFICIAL("official"),

        /**
         * A give-away release or a release intended to promote an upcoming official release
         * (e.g. pre-release versions, releases included with a magazine, versions supplied to radio DJs for air-play).
         */
        PROMOTIONAL("promotional"),

        /**
         * An unofficial/underground release that was not sanctioned by the artist and/or the record company.
         * This includes unofficial live recordings and pirated releases.
         */
        BOOTLEG("bootleg"),

        /**
         * An alternate version of a release where the titles have been changed.
         * These don't correspond to any real release and should be linked to the original release
         * using the transl(iter)ation relationship.

         */
        PSEUDO_RELEASE("pseudo-release");

        private final String type;
        Status (String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type;
        }
    }

    // TODO: Check all types. (is "normal" type?)
    public enum DataQuality {
        /**
         * All available data has been added, if possible including cover art with liner info that proves it.
         */
        HIGH_QUALITY("High quality"),

        /**
         * This is the default setting - technically "unknown" if the quality has never been modified, "normal" if it has.
         */
        DEFAULT_QUALITY("Default quality"),

        /**
         * The release needs serious fixes, or its existence is hard to prove (but it's not clearly fake).
         */
        LOW_QUALITY("Low quality");

        private final String type;
        DataQuality (String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type;
        }
    }

    public static class ReleaseSearch extends BaseSearch {
        @Json(name = "releases")
        private List<Release> releases;

        public List<Release> getReleases() {
            return releases;
        }

        public void setReleases(List<Release> releases) {
            this.releases = releases;
        }
    }

    public static class ReleaseBrowse {

        @Json(name = "release-count")
        protected int count;

        @Json(name = "release-offset")
        protected int offset;

        @Json(name = "releases")
        private List<Release> releases;

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

        public List<Release> getReleases() {
            return releases;
        }

        public void setReleases(List<Release> releases) {
            this.releases = releases;
        }
    }


    public static class TextRepresentation {
        @Json(name = "language")
        private String language = "";

        @Json(name = "script")
        private String script = "";

        public TextRepresentation() {
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getScript() {
            return script;
        }

        public void setScript(String script) {
            this.script = script;
        }
    }

    @Json(name = "cover-art-archive")
    private CoverArt coverArt;

    @Json(name = "score")
    private int score;

    @Json(name = "count")
    private int count;

    @Json(name = "title")
    private String title = "";

    /**
     * Release.Status
     */
    @Json(name = "status")
    private String status = "";

    @Json(name = "status-id")
    private String statusId;

    @Json(name = "packaging")
    private String packaging = "";

    @Json(name = "packaging-id")
    private String packagingId;

    //"yyyy-mm-dd"
    @Json(name = "date")
    private String date = "";

    @Json(name = "country")
    private String country = "";

    @Json(name = "barcode")
    private String barcode = "";

    @Json(name = "disambiguation")
    private String disambiguation = "";

    @Json(name = "asin")
    private String asin = "";

    @Json(name = "track-count")
    private int trackCount;

    @Json(name = "text-representation")
    private TextRepresentation textRepresentation;

    @Json(name = "release-events")
    private List<ReleaseEvent> releaseEvents;

    /**
     * Release.DataQuality
     */
    @Json(name = "quality")
    private String quality;

    //inc=aliases
    @Json(name = "aliases")
    private List<Alias> aliases;

    //inc=annotation
    @Json(name = "annotation")
    private String annotation;

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

    //inc=artists
    //inc=artist-credits
    @Json(name = "artist-credit")
    private List<Artist.ArtistCredit> artistCredits;

    //inc=collections
    @Json(name = "collections")
    private List<Collection> collections;

    //inc=labels
    @Json(name = "label-info")
    private List<Label.LabelInfo> labelInfo;

    //inc=release-groups
    @Json(name = "release-group")
    private ReleaseGroup releaseGroup;

    //inc=media
    //inc=recordings equal inc=media with track infos
    @Json(name = "media")
    private List<Media> media;

    public Release() {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getTrackCount() {
        return trackCount;
    }

    public void setTrackCount(int trackCount) {
        this.trackCount = trackCount;
    }

    public TextRepresentation getTextRepresentation() {
        return textRepresentation;
    }

    public void setTextRepresentation(TextRepresentation textRepresentation) {
        this.textRepresentation = textRepresentation;
    }

    public List<Artist.ArtistCredit> getArtistCredits() {
        return artistCredits;
    }

    public void setArtistCredits(List<Artist.ArtistCredit> artistCredits) {
        this.artistCredits = artistCredits;
    }

    public ReleaseGroup getReleaseGroup() {
        return releaseGroup;
    }

    public void setReleaseGroup(ReleaseGroup releaseGroup) {
        this.releaseGroup = releaseGroup;
    }

    public List<Label.LabelInfo> getLabelInfo() {
        return labelInfo;
    }

    public void setLabelInfo(List<Label.LabelInfo> labelInfo) {
        this.labelInfo = labelInfo;
    }

    public List<ReleaseEvent> getReleaseEvents() {
        return releaseEvents;
    }

    public void setReleaseEvents(List<ReleaseEvent> releaseEvents) {
        this.releaseEvents = releaseEvents;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public List<Media> getMedia() {
        return media;
    }

    public void setMedia(List<Media> media) {
        this.media = media;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getPackagingId() {
        return packagingId;
    }

    public void setPackagingId(String packagingId) {
        this.packagingId = packagingId;
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

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
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

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public CoverArt getCoverArt() {
        return coverArt;
    }

    public void setCoverArt(CoverArt coverArt) {
        this.coverArt = coverArt;
    }

    public List<Collection> getCollections() {
        return collections;
    }

    public void setCollections(List<Collection> collections) {
        this.collections = collections;
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
}
