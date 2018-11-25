package app.mediabrainz.api.browse;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.Work;

import static app.mediabrainz.api.browse.EntityType.ARTIST_ENTITY;
import static app.mediabrainz.api.browse.EntityType.COLLECTION_ENTITY;
import static app.mediabrainz.api.lookup.IncType.*;


public class WorkBrowseService extends
        BaseBrowseService<Work.WorkBrowse, WorkBrowseService.WorkIncType, WorkBrowseService.WorkBrowseEntityType> {

    public WorkBrowseService(WorkBrowseEntityType entityType, String mbid) {
        super(entityType, mbid);
    }

    @Override
    public Flowable<Result<Work.WorkBrowse>> browse() {
        return getJsonRetrofitService().browseWork(getParams());
    }

    public enum WorkBrowseEntityType implements BaseBrowseService.BrowseEntityTypeInterface {
        ARTIST(ARTIST_ENTITY),
        COLLECTION(COLLECTION_ENTITY);

        private final String type;
        WorkBrowseEntityType(String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type;
        }
    }

    public enum WorkIncType implements BrowseServiceInterface.IncTypeInterface {
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
