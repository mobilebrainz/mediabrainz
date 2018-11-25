package app.mediabrainz.api.browse;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.Place;

import static app.mediabrainz.api.browse.EntityType.AREA_ENTITY;
import static app.mediabrainz.api.browse.EntityType.COLLECTION_ENTITY;
import static app.mediabrainz.api.lookup.IncType.*;


public class PlaceBrowseService extends
        BaseBrowseService<Place.PlaceBrowse, PlaceBrowseService.PlaceIncType, PlaceBrowseService.PlaceBrowseEntityType> {

    public PlaceBrowseService(PlaceBrowseEntityType entityType, String mbid) {
        super(entityType, mbid);
    }

    @Override
    public Flowable<Result<Place.PlaceBrowse>> browse() {
        return getJsonRetrofitService().browsePlace(getParams());
    }

    public enum PlaceBrowseEntityType implements BaseBrowseService.BrowseEntityTypeInterface {
        AREA(AREA_ENTITY),
        COLLECTION(COLLECTION_ENTITY);

        private final String type;
        PlaceBrowseEntityType(String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type;
        }
    }

    public enum PlaceIncType implements BrowseServiceInterface.IncTypeInterface {
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
