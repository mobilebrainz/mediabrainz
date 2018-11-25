package app.mediabrainz.api.search;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.BaseSearch;
import app.mediabrainz.api.core.BaseWebService;

import static app.mediabrainz.api.Config.FORMAT_JSON;
import static app.mediabrainz.api.search.SearchServiceInterface.SearchParamType.FORMAT;
import static app.mediabrainz.api.search.SearchServiceInterface.SearchParamType.LIMIT;
import static app.mediabrainz.api.search.SearchServiceInterface.SearchParamType.OFFSET;
import static app.mediabrainz.api.search.SearchServiceInterface.SearchParamType.QUERY;


public abstract class BaseSearchService
        <R extends BaseSearch, P extends Enum<P> & SearchServiceInterface.SearchFieldInterface>
        extends BaseWebService
        implements SearchServiceInterface<R, P> {

    private final LuceneBuilder expression = new LuceneBuilder();
    private final Map<SearchParamType, String> params = new HashMap<>();
    {
        params.put(QUERY, "empty");
        params.put(FORMAT, FORMAT_JSON);
    }

    @Override
    public Flowable<Result<R>> search(String query) {
        params.put(QUERY, query);
        return search();
    }

    @Override
    public Flowable<Result<R>> search(String query, int limit, int offset) {
        params.put(QUERY, query);
        params.put(LIMIT, Integer.toString(limit));
        params.put(OFFSET, Integer.toString(offset));
        return search();
    }

    @Override
    public Flowable<Result<R>> search(int limit, int offset) {
        return search(expression.build(), limit, offset);
    }

    @Override
    public SearchServiceInterface<R, P> add(String query) {
        if (query != null && !query.equals("")) {
            expression.add(query);
        }
        return this;
    }

    @Override
    public SearchServiceInterface<R, P> add(LuceneBuilder.Operator operator) {
        expression.add(operator);
        return this;
    }

    @Override
    public SearchServiceInterface<R, P> add(P searchField, String value) {
        if (value != null && !value.equals("")) {
            expression.add(searchField, value);
        }
        return this;
    }

    protected Map<String, String> getParams() {
        Map<String, String> map = new HashMap<>();
        Iterator<SearchParamType> iter = params.keySet().iterator();
        while(iter.hasNext()) {
            SearchParamType key = iter.next();
            map.put(key.toString(), params.get(key));
        }
        String query = expression.build();
        if (!query.equals("")) {
            map.put(QUERY.toString(), query);
        }
        return map;
    }

}
