package app.mediabrainz.api.lookup;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.Place;

import static app.mediabrainz.api.lookup.IncType.*;


public class PlaceLookupService extends BaseLookupService<Place, PlaceLookupService.PlaceIncType> {

    public PlaceLookupService(String mbid) {
        super(mbid);
    }

    @Override
    public Flowable<Result<Place>> lookup() {
        return getJsonRetrofitService().lookupPlace(getMbid(), getParams());
    }

    public enum PlaceIncType implements LookupServiceInterface.IncTypeInterface {
        ALIASES(ALIASES_INC),
        ANNOTATION(ANNOTATION_INC),
        TAGS(TAGS_INC),
        USER_TAGS(USER_TAGS_INC);         //require authorization

        private final String inc;
        PlaceIncType(String inc) {
            this.inc = inc;
        }
        @Override
        public String toString() {
            return inc;
        }
    }

}
