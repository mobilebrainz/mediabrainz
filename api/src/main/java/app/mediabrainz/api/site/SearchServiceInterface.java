package app.mediabrainz.api.site;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;


public interface SearchServiceInterface {

    enum SearchType {

        EDITOR("editor"),
        TAG("tag");

        private final String type;

        SearchType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    Flowable<Result<List<String>>> searchTag(String query, int page, int limit);

    Flowable<Result<List<String>>> searchUser(String query, int page, int limit);

    Flowable<Result<List<String>>> search(SearchType searchType, String query, int page, int limit);

}
