package app.mediabrainz.api.search;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.Series;
import app.mediabrainz.api.model.Series.SeriesSearch;

import static app.mediabrainz.api.search.SeriesSearchService.SeriesSearchField.TYPE;

/**
 * unconditional search: Series search terms with no fields specified search the name and alias fields.
 *  new SeriesSearchService().search("rock")
 *  new SeriesSearchService().search("rock", 5, 10)
 */
public class SeriesSearchService extends
        BaseSearchService<SeriesSearch, SeriesSearchService.SeriesSearchField> {

    @Override
    public Flowable<Result<SeriesSearch>> search() {
        return getJsonRetrofitService().searchSeries(getParams());
    }

    public SeriesSearchService addType(Series.SeriesType type) {
        add(TYPE, type.toString());
        return this;
    }

    @Override
    public SeriesSearchService add(LuceneBuilder.Operator operator) {
        super.add(operator);
        return this;
    }

    public enum SeriesSearchField implements SearchServiceInterface.SearchFieldInterface {
        /**
         * search field: name
         */
        SERIES("series"),
        ALIAS("alias"),
        TAG("tag"),
        TYPE("type");

        private final String searchField;

        SeriesSearchField(String searchField) {
            this.searchField = searchField;
        }

        @Override
        public String toString() {
            return searchField;
        }
    }

}
