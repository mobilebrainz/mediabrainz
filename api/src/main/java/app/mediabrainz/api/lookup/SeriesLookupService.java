package app.mediabrainz.api.lookup;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.Series;

import static app.mediabrainz.api.lookup.IncType.*;


public class SeriesLookupService extends BaseLookupService<Series, SeriesLookupService.SeriesIncType> {

    public SeriesLookupService(String mbid) {
        super(mbid);
    }

    @Override
    public Flowable<Result<Series>> lookup() {
        return getJsonRetrofitService().lookupSeries(getMbid(), getParams());
    }

    public enum SeriesIncType implements LookupServiceInterface.IncTypeInterface {
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
