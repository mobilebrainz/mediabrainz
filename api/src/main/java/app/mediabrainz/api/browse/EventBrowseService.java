package app.mediabrainz.api.browse;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.Event;

import static app.mediabrainz.api.lookup.IncType.*;
import static app.mediabrainz.api.browse.EntityType.*;


public class EventBrowseService extends
        BaseBrowseService<Event.EventBrowse, EventBrowseService.EventIncType, EventBrowseService.EventBrowseEntityType> {

    public EventBrowseService(EventBrowseEntityType entityType, String mbid) {
        super(entityType, mbid);
    }

    @Override
    public Flowable<Result<Event.EventBrowse>> browse() {
        return getJsonRetrofitService().browseEvent(getParams());
    }

    public enum EventBrowseEntityType implements BaseBrowseService.BrowseEntityTypeInterface {
        AREA(AREA_ENTITY),
        ARTIST(ARTIST_ENTITY),
        COLLECTION(COLLECTION_ENTITY),
        PLACE(PLACE_ENTITY);

        private final String type;
        EventBrowseEntityType(String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type;
        }
    }

    public enum EventIncType implements BrowseServiceInterface.IncTypeInterface {
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
