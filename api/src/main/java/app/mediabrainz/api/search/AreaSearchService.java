package app.mediabrainz.api.search;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.Area;
import app.mediabrainz.api.model.Area.AreaSearch;

import static app.mediabrainz.api.search.AreaSearchService.AreaSearchField.TYPE;

/**
 * unconditional search: Area search terms with no fields specified search the AREA and SORTNAME fields.
 *   new AreaSearchService().search("France")
 *   new AreaSearchService().search("France", 5, 10)
 */

public class AreaSearchService extends
        BaseSearchService<AreaSearch, AreaSearchService.AreaSearchField> {

    @Override
    public Flowable<Result<AreaSearch>> search() {
        return getJsonRetrofitService().searchArea(getParams());
    }

    public AreaSearchService addType(Area.Type type) {
        add(TYPE, type.toString());
        return this;
    }

    @Override
    public AreaSearchService add(LuceneBuilder.Operator operator) {
        super.add(operator);
        return this;
    }

    public enum AreaSearchField implements SearchServiceInterface.SearchFieldInterface {
        /**
         * the area's MBID
         */
        AID("aid"),

        /**
         * an alias attached to the area
         */
        ALIAS("alias"),

        /**
         * the area's name
         */
        AREA("area"),

        /**
         * the area's begin date
         */
        BEGIN("begin"),

        /**
         * the area's disambiguation comment
         */
        COMMENT("comment"),

        /**
         * the area's end date
         */
        END("end"),

        /**
         * a flag indicating whether or not the area has ended
         */
        ENDED("ended"),

        /**
         * an ISO 3166-1/2/3 code attached to the area
         */
        ISO("iso"),

        /**
         * an ISO 3166-1 code attached to the area
         */
        ISO1("iso1"),

        /**
         * an ISO 3166-2 code attached to the area
         */
        ISO2("iso2"),

        /**
         * an ISO 3166-3 code attached to the area
         */
        ISO3("iso3"),

        /**
         * the area's sort name
         */
        SORTNAME("sortname"),

        /**
         * the area's type
         */
        TYPE("type");

        private final String searchField;

        AreaSearchField(String searchField) {
            this.searchField = searchField;
        }

        @Override
        public String toString() {
            return searchField;
        }
    }

}
