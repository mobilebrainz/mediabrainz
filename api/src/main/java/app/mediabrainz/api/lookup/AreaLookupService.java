package app.mediabrainz.api.lookup;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.Area;

import static app.mediabrainz.api.lookup.IncType.*;


public class AreaLookupService extends BaseLookupService<Area, AreaLookupService.AreaIncType> {

    public AreaLookupService(String mbid) {
        super(mbid);
    }

    @Override
    public Flowable<Result<Area>> lookup() {
        return getJsonRetrofitService().lookupArea(getMbid(), getParams());
    }

    public enum AreaIncType implements LookupServiceInterface.IncTypeInterface {
        ALIASES(ALIASES_INC),
        ANNOTATION(ANNOTATION_INC),
        TAGS(TAGS_INC),
        USER_TAGS(USER_TAGS_INC);         //require authorization

        private final String inc;

        AreaIncType(String inc) {
            this.inc = inc;
        }

        @Override
        public String toString() {
            return inc;
        }
    }

}
