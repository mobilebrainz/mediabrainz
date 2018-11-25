package app.mediabrainz.api.browse;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.Series;

import static app.mediabrainz.api.browse.EntityType.COLLECTION_ENTITY;
import static app.mediabrainz.api.lookup.IncType.*;


public class SeriesBrowseService extends
        BaseBrowseService<Series.SeriesBrowse, SeriesBrowseService.SeriesIncType, SeriesBrowseService.SeriesBrowseEntityType> {

    public SeriesBrowseService(SeriesBrowseEntityType entityType, String mbid) {
        super(entityType, mbid);
    }

    @Override
    public Flowable<Result<Series.SeriesBrowse>> browse() {
        return getJsonRetrofitService().browseSeries(getParams());
    }

    public enum SeriesBrowseEntityType implements BaseBrowseService.BrowseEntityTypeInterface {
        COLLECTION(COLLECTION_ENTITY);

        private final String type;
        SeriesBrowseEntityType(String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type;
        }
    }

    public enum SeriesIncType implements BrowseServiceInterface.IncTypeInterface {
        ALIASES(ALIASES_INC),
        ANNOTATION(ANNOTATION_INC),
        TAGS(TAGS_INC),
        USER_TAGS(USER_TAGS_INC);         //require authorization

        private final String inc;
        SeriesIncType(String inc) {
            this.inc = inc;
        }
        @Override
        public String toString() {
            return inc;
        }
    }

}
