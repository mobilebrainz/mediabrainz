package app.mediabrainz.api.search;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.Label;
import app.mediabrainz.api.model.Label.LabelSearch;

import static app.mediabrainz.api.search.LabelSearchService.LabelSearchField.TYPE;

/**
 * unconditional search: Label search terms with no fields specified search the LABEL, SORT_NAME and ALIAS fields.
 *  new LabelSearchService().search("Records")
 *  new LabelSearchService().search("Records", 2, 10)
 */

public class LabelSearchService extends
        BaseSearchService<LabelSearch, LabelSearchService.LabelSearchField> {

    @Override
    public Flowable<Result<LabelSearch>> search() {
        return getJsonRetrofitService().searchLabel(getParams());
    }

    public LabelSearchService addType(Label.LabelType type) {
        add(TYPE, type.toString());
        return this;
    }

    @Override
    public LabelSearchService add(LuceneBuilder.Operator operator) {
        super.add(operator);
        return this;
    }

    public enum LabelSearchField implements SearchServiceInterface.SearchFieldInterface {
        /**
         * the aliases/misspellings for this label
         */
        ALIAS("alias"),

        /**
         * label area
         */
        AREA("area"),

        /**
         * label founding date
         */
        BEGIN("begin"),

        /**
         * label code (only the figures part, i.e. without "LC")
         */
        CODE("code"),

        /**
         * label comment to differentiate similar labels
         */
        COMMENT("comment"),

        /**
         * The two letter country code of the label country
         */
        COUNTRY("country"),

        /**
         * label dissolution date
         */
        END("end"),

        /**
         * true if know ended even if do not know end date
         */
        ENDED("ended"),

        /**
         * ipi
         */
        IPI("ipi"),

        /**
         * label name
         */
        LABEL("label"),

        /**
         * name of the label with any accent characters retained
         */
        LABELACCENT("labelaccent"),

        /**
         * MBID of the label
         */
        LAID("laid"),

        /**
         * label sortname
         */
        SORT_NAME("sortname"),

        /**
         * label type
         */
        TYPE("type"),

        /**
         * folksonomy tag
         */
        TAG("tag");

        private final String searchField;

        LabelSearchField(String searchField) {
            this.searchField = searchField;
        }

        @Override
        public String toString() {
            return searchField;
        }
    }

}
