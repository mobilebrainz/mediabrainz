package app.mediabrainz.api.search;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.api.model.ReleaseGroup.ReleaseGroupSearch;

import static app.mediabrainz.api.search.ReleaseGroupSearchService.ReleaseGroupSearchField.PRIMARY_TYPE;
import static app.mediabrainz.api.search.ReleaseGroupSearchService.ReleaseGroupSearchField.SECONDARY_TYPE;

/**
 * unconditional search: Release group search terms with no fields search the RELEASE_GROUP field only.
 *  new ReleaseGroupSearchService().search("black")
 *  new ReleaseGroupSearchService().search("black", 1, 10)
 */

public class ReleaseGroupSearchService extends
        BaseSearchService<ReleaseGroupSearch, ReleaseGroupSearchService.ReleaseGroupSearchField> {

    @Override
    public Flowable<Result<ReleaseGroupSearch>> search() {
        return getJsonRetrofitService().searchReleaseGroup(getParams());
    }

    public ReleaseGroupSearchService addPrimaryType(ReleaseGroup.PrimaryType type) {
        add(PRIMARY_TYPE, type.toString());
        return this;
    }

    public ReleaseGroupSearchService addSecondaryType(ReleaseGroup.SecondaryType type) {
        add(SECONDARY_TYPE, type.toString());
        return this;
    }

    @Override
    public ReleaseGroupSearchService add(LuceneBuilder.Operator operator) {
        super.add(operator);
        return this;
    }

    public enum ReleaseGroupSearchField implements ReleaseSearchService.SearchFieldInterface {
        /**
         * MBID of the release group’s artist
         */
        ARID("arid"),

        /**
         * release group artist as it appears on the cover (Artist Credit)
         */
        ARTIST("artist"),

        /**
         * “real name” of any artist that is included in the release group’s artist credit
         */
        ARTIST_NAME("artistname"),

        /**
         * release group comment to differentiate similar release groups
         */
        COMMENT("comment"),

        /**
         * name of any artist in multi-artist credits, as it appears on the cover.
         */
        CREDIT_NAME("creditname"),

        /**
         * primary type of the release group (album, single, ep, other)
         */
        PRIMARY_TYPE("primarytype"),

        /**
         * MBID of the release group
         */
        RGID("rgid"),

        /**
         * name of the release group
         */
        RELEASE_GROUP("releasegroup"),

        /**
         * name of the releasegroup with any accent characters retained
         */
        RELEASE_GROUP_ACCENT("releasegroupaccent"),

        /**
         * number of releases in this release group
         */
        RELEASES("releases"),

        /**
         * name of a release that appears in the release group
         */
        RELEASE("release"),

        /**
         * MBID of a release that appears in the release group
         */
        REID("reid"),

        /**
         * secondary type of the release group (audiobook, compilation, interview, live, remix soundtrack, spokenword)
         */
        SECONDARY_TYPE("secondarytype"),

        /**
         * status of a release that appears within the release group
         */
        STATUS("status"),

        /**
         * a tag that appears on the release group
         */
        TAG("tag"),

        /**
         * type of the release group, old type mapping for when we did not have separate primary and secondary types
         */
        TYPE("type");

        private final String searchField;

        ReleaseGroupSearchField(String searchField) {
            this.searchField = searchField;
        }

        @Override
        public String toString() {
            return searchField;
        }
    }

}
