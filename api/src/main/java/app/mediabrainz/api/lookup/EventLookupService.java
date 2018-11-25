package app.mediabrainz.api.lookup;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.Event;

import static app.mediabrainz.api.lookup.IncType.*;


public class EventLookupService extends BaseLookupService<Event, EventLookupService.EventIncType> {

    public EventLookupService(String mbid) {
        super(mbid);
    }

    @Override
    public Flowable<Result<Event>> lookup() {
        return getJsonRetrofitService().lookupEvent(getMbid(), getParams());
    }

    public enum EventIncType implements LookupServiceInterface.IncTypeInterface {
        ALIASES(ALIASES_INC),
        ANNOTATION(ANNOTATION_INC),
        TAGS(TAGS_INC),
        RATINGS(RATINGS_INC),
        USER_TAGS(USER_TAGS_INC),         //require authorization
        USER_RATINGS(USER_RATINGS_INC);   //require authorization

        private final String inc;
        EventIncType(String inc) {
            this.inc = inc;
        }
        @Override
        public String toString() {
            return inc;
        }
    }

}
