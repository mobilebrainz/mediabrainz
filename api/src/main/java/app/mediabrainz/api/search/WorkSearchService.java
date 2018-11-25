package app.mediabrainz.api.search;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.Work.WorkSearch;

/**
 * unconditional search: Work search terms with no fields specified search the WORK and ALIAS fields.
 *  new WorkSearchService().search("Frozen")
 *  new WorkSearchService().search("Frozen", 1, 10)
 */

public class WorkSearchService extends
        BaseSearchService<WorkSearch, WorkSearchService.WorkSearchField> {

    @Override
    public Flowable<Result<WorkSearch>> search() {
        return getJsonRetrofitService().searchWork(getParams());
    }

    public enum WorkSearchField implements SearchServiceInterface.SearchFieldInterface {
        /**
         * the aliases/misspellings for this work
         */
        ALIAS("alias"),

        /**
         * artist id
         */
        ARID("arid"),

        /**
         * artist name, an artist in the context of a work is an artist-work relation such as composer or lyricist
         */
        ARTIST("artist"),

        /**
         * disambiguation comment
         */
        COMMENT("comment"),

        /**
         * ISWC of work
         */
        ISWC("iswc"),

        /**
         * Lyrics language of work
         */
        LANG("lang"),

        /**
         * folksonomy tag
         */
        TAG("tag"),

        /**
         * work type
         */
        TYPE("type"),

        /**
         * work id
         */
        WID("wid"),

        /**
         * name of work
         */
        WORK("work"),

        /**
         * name of the work with any accent characters retained
         */
        WORK_ACCENT("workaccent");

        private final String searchField;

        WorkSearchField(String searchField) {
            this.searchField = searchField;
        }

        @Override
        public String toString() {
            return searchField;
        }
    }

}
