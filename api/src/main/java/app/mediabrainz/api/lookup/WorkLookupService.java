package app.mediabrainz.api.lookup;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.Work;

import static app.mediabrainz.api.lookup.IncType.*;


public class WorkLookupService extends BaseLookupService<Work, WorkLookupService.WorkIncType> {

    public WorkLookupService(String mbid) {
        super(mbid);
    }

    @Override
    public Flowable<Result<Work>> lookup() {
        return getJsonRetrofitService().lookupWork(getMbid(), getParams());
    }

    public enum WorkIncType implements LookupServiceInterface.IncTypeInterface {
        ALIASES(ALIASES_INC),
        ANNOTATION(ANNOTATION_INC),
        TAGS(TAGS_INC),
        RATINGS(RATINGS_INC),
        USER_TAGS(USER_TAGS_INC),         //require authorization
        USER_RATINGS(USER_RATINGS_INC);   //require authorization

        private final String inc;
        WorkIncType(String inc) {
            this.inc = inc;
        }
        @Override
        public String toString() {
            return inc;
        }
    }

}
