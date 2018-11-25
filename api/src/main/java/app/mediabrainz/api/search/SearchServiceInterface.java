package app.mediabrainz.api.search;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.BaseSearch;


public interface SearchServiceInterface
        <R extends BaseSearch, P extends Enum<P> & SearchServiceInterface.SearchFieldInterface> {

    Flowable<Result<R>> search();
    Flowable<Result<R>> search(String query);
    Flowable<Result<R>> search(int limit, int offset);
    Flowable<Result<R>> search(String query, int limit, int offset);

    SearchServiceInterface<R, P> add(String query);
    SearchServiceInterface<R, P> add(P searchField, String value);
    SearchServiceInterface<R, P> add(LuceneBuilder.Operator operator);

    interface SearchFieldInterface {
    }

    enum SearchParamType {
        FORMAT("fmt"),
        QUERY("query"),
        LIMIT("limit"),
        OFFSET("offset");

        private final String param;
        SearchParamType(String param) {
            this.param = param;
        }
        @Override
        public String toString() {
            return param;
        }
    }

}
