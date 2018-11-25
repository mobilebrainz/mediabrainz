package app.mediabrainz.api.lookup;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.Instrument;

import static app.mediabrainz.api.lookup.IncType.*;


public class InstrumentLookupService extends BaseLookupService<Instrument, InstrumentLookupService.InstrumentIncType> {

    public InstrumentLookupService(String mbid) {
        super(mbid);
    }

    @Override
    public Flowable<Result<Instrument>> lookup() {
        return getJsonRetrofitService().lookupInstrument(getMbid(), getParams());
    }

    public enum InstrumentIncType implements LookupServiceInterface.IncTypeInterface {
        ALIASES(ALIASES_INC),
        ANNOTATION(ANNOTATION_INC),
        TAGS(TAGS_INC),
        USER_TAGS(USER_TAGS_INC);         //require authorization

        private final String inc;
        InstrumentIncType(String inc) {
            this.inc = inc;
        }
        @Override
        public String toString() {
            return inc;
        }
    }

}
