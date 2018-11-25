package app.mediabrainz.api.search;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.Media;
import app.mediabrainz.api.model.Release;
import app.mediabrainz.api.model.Release.ReleaseSearch;

import static app.mediabrainz.api.search.ReleaseSearchService.ReleaseSearchField.FORMAT;
import static app.mediabrainz.api.search.ReleaseSearchService.ReleaseSearchField.STATUS;

/**
 * unconditional search: Release search terms with no fields search the RELEASE field only.
 *  new ReleaseSearchService().search("Stair")
 *  new ReleaseSearchService().search("Stair", 2, 10)
 */

public class ReleaseSearchService extends
        BaseSearchService<ReleaseSearch, ReleaseSearchService.ReleaseSearchField> {

    @Override
    public Flowable<Result<ReleaseSearch>> search() {
        return getJsonRetrofitService().searchRelease(getParams());
    }

    public ReleaseSearchService addStatus(Release.Status status) {
        add(STATUS, status.toString());
        return this;
    }

    public ReleaseSearchService addFormat(Media.Format format) {
        add(FORMAT, format.toString());
        return this;
    }

    @Override
    public ReleaseSearchService add(LuceneBuilder.Operator operator) {
        super.add(operator);
        return this;
    }

    public enum ReleaseSearchField implements SearchServiceInterface.SearchFieldInterface {
        /**
         * artist id
         */
        ARID("arid"),

        /**
         * complete artist name(s) as it appears on the release
         */
        ARTIST("artist"),

        /**
         * an artist on the release, each artist added as a separate field
         */
        ARTIST_NAME("artistname"),

        /**
         * the Amazon ASIN for this release
         */
        ASIN("asin"),

        /**
         * The barcode of this release
         */
        BARCODE("barcode"),

        /**
         * The catalog number for this release, can have multiples when major using an imprint
         */
        CATNO("catno"),

        /**
         * Disambiguation comment
         */
        COMMENT("comment"),

        /**
         * The two letter country code for the release country
         */
        COUNTRY("country"),

        /**
         * name credit on the release, each artist added as a separate field
         */
        CREDIT_NAME("creditname"),

        /**
         * The release date (format: YYYY-MM-DD)
         */
        DATE("date"),

        /**
         * total number of cd ids over all mediums for the release
         */
        DISCIDS("discids"),

        /**
         * number of cd ids for the release on a medium in the release
         */
        DISCIDS_MEDIUM("discidsmedium"),

        /**
         * release format
         */
        FORMAT("format"),

        /**
         * The label id for this release, a release can have multiples when major using an imprint
         */
        LAID("laid"),

        /**
         * The name of the label for this release, can have multiples when major using an imprint
         */
        LABEL("label"),

        /**
         * The language for this release. Use the three character ISO 639 codes to search for a specific language. (e.g. lang:eng)
         */
        LANG("lang"),

        /**
         * number of mediums in the release
         */
        MEDIUMS("mediums"),

        /**
         * primary type of the release group (album, single, ep, other)
         */
        PRIMARY_TYPE("primarytype"),

        /**
         * The release contains recordings with these puids
         */
        PUID("puid"),

        /**
         * The quality of the release (low, normal, high)
         */
        QUALITY("quality"),

        /**
         * release id
         */
        REID("reid"),

        /**
         * release name
         */
        RELEASE("release"),

        /**
         * name of the release with any accent characters retained
         */
        RELEASE_ACCENT("releaseaccent"),

        /**
         * release group id
         */
        RGID("rgid"),

        /**
         * The 4 character script code (e.g. latn) used for this release
         */
        SCRIPT("script"),

        /**
         * secondary type of the release group (audiobook, compilation, interview, live, remix, soundtrack, spokenword)
         */
        SECONDARY_TYPE("secondarytype"),

        /**
         * release status (e.g official)
         */
        STATUS("status"),

        /**
         * a tag that appears on the release
         */
        TAG("tag"),

        /**
         * total number of tracks over all mediums on the release
         */
        TRACKS("tracks"),

        /**
         * number of tracks on a medium in the release
         */
        TRACKS_MEDIUM("tracksmedium"),

        /**
         * type of the release group, old type mapping for when we did not have separate primary and secondary types
         */
        TYPE("type");

        private final String searchField;

        ReleaseSearchField(String searchField) {
            this.searchField = searchField;
        }

        @Override
        public String toString() {
            return searchField;
        }
    }

}
