package app.mediabrainz.api.search;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.Instrument.InstrumentSearch;

/**
 * unconditional search:  Instrument terms with no fields specified search the name, description and other fields.
 *   new InstrumentSearchService().search("bass")
 *   new InstrumentSearchService().search("bass", 2, 10)
 */

public class InstrumentSearchService extends
        BaseSearchService<InstrumentSearch, InstrumentSearchService.InstrumentSearchField> {

    @Override
    public Flowable<Result<InstrumentSearch>> search() {
        return getJsonRetrofitService().searchInstrument(getParams());
    }

    @Override
    public InstrumentSearchService add(LuceneBuilder.Operator operator) {
        super.add(operator);
        return this;
    }

    public enum InstrumentSearchField implements SearchServiceInterface.SearchFieldInterface {

        /**
         * search only field name
         */
        INSTRUMENT("instrument");

        private final String searchField;

        InstrumentSearchField(String searchField) {
            this.searchField = searchField;
        }

        @Override
        public String toString() {
            return searchField;
        }
    }

}
