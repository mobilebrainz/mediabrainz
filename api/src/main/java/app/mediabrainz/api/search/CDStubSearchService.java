package app.mediabrainz.api.search;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.CDStub.CDStubSearch;

/**
 * unconditional search: Query terms without a field specifier will search the ARTIST and TITLE fields.
 *   new CDStubSearchService().search("Mac")
 *   new CDStubSearchService().search("France", 5, 10)
 */

public class CDStubSearchService extends
        BaseSearchService<CDStubSearch, CDStubSearchService.CDStubSearchField> {

    @Override
    public Flowable<Result<CDStubSearch>> search() {
        return getJsonRetrofitService().searchCDStub(getParams());
    }

    public enum CDStubSearchField implements SearchServiceInterface.SearchFieldInterface {
        /**
         * the artist name set on the CD stub
         */
        ARTIST("artist"),

        /**
         * the barcode set on the CD stub
         */
        BARCODE("barcode"),

        /**
         * the comment set on the CD stub
         */
        COMMENT("comment"),

        /**
         * the CD stub's Disc ID
         */
        DISCID("discid"),

        /**
         * the release title set on the CD stub
         */
        TITLE("title"),

        /**
         * the CD stub's number of tracks
         */
        TRACKS("tracks");

        private final String searchField;

        CDStubSearchField(String searchField) {
            this.searchField = searchField;
        }

        @Override
        public String toString() {
            return searchField;
        }
    }

}
