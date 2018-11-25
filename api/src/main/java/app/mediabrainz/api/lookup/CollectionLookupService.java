package app.mediabrainz.api.lookup;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.Collection;


public class CollectionLookupService extends BaseLookupService<Collection, LookupServiceInterface.EmptyIncType> {

    public CollectionLookupService(String mbid) {
        super(mbid);
    }

    @Override
    public Flowable<Result<Collection>> lookup() {
        return getJsonRetrofitService().lookupCollection(getMbid(), getParams());
    }

}
