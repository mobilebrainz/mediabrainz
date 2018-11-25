package app.mediabrainz.api.browse;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.Label;

import static app.mediabrainz.api.lookup.IncType.*;
import static app.mediabrainz.api.browse.EntityType.*;


public class LabelBrowseService extends
        BaseBrowseService<Label.LabelBrowse, LabelBrowseService.LabelIncType, LabelBrowseService.LabelBrowseEntityType> {

    public LabelBrowseService(LabelBrowseEntityType entityType, String mbid) {
        super(entityType, mbid);
    }

    @Override
    public Flowable<Result<Label.LabelBrowse>> browse() {
        return getJsonRetrofitService().browseLabel(getParams());
    }

    public enum LabelBrowseEntityType implements BaseBrowseService.BrowseEntityTypeInterface {
        AREA(AREA_ENTITY),
        COLLECTION(COLLECTION_ENTITY),
        RELEASE(RELEASE_ENTITY);

        private final String type;
        LabelBrowseEntityType(String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type;
        }
    }

    public enum LabelIncType implements BrowseServiceInterface.IncTypeInterface {
        ALIASES(ALIASES_INC),
        ANNOTATION(ANNOTATION_INC),
        TAGS(TAGS_INC),
        RATINGS(RATINGS_INC),
        USER_TAGS(USER_TAGS_INC),         //require authorization
        USER_RATINGS(USER_RATINGS_INC);   //require authorization

        private final String inc;
        LabelIncType(String inc) {
            this.inc = inc;
        }
        @Override
        public String toString() {
            return inc;
        }
    }

}
