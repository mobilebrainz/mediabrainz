package app.mediabrainz.api.lookup;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.ISWC;


public class ISWCLookupService extends BaseLookupService<ISWC, WorkLookupService.WorkIncType> {

    public ISWCLookupService(String mbid) {
        super(mbid);
    }

    @Override
    public Flowable<Result<ISWC>> lookup() {
        return getJsonRetrofitService().lookupISWC(getMbid(), getParams());
    }

}
