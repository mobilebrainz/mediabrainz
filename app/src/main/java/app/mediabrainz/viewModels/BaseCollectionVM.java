package app.mediabrainz.viewModels;


import app.mediabrainz.api.model.BaseLookupEntity;
import app.mediabrainz.api.model.Collection;
import app.mediabrainz.api.model.xml.Metadata;
import app.mediabrainz.util.event.EventLiveData;

import static app.mediabrainz.MediaBrainzApp.api;


public abstract class BaseCollectionVM extends BaseViewModel {

    public final EventLiveData<Resource<Metadata>> deleteEntityEvent = new EventLiveData<>();

    public void deleteEntityFromCollection(Collection collection, BaseLookupEntity entity) {
        deleteEntityEvent.setData(Resource.loading());
        compositeDisposable.add(api.deleteEntityFromCollection(
                collection, entity,
                metadata -> deleteEntityEvent.postEvent(Resource.success(metadata)),
                throwable -> deleteEntityEvent.postEvent(Resource.error(throwable))));
    }

}
