package app.mediabrainz.viewModels;


import app.mediabrainz.api.model.Collection;
import app.mediabrainz.util.event.EventLiveData;

import static app.mediabrainz.MediaBrainzApp.api;


public class CollectionsTabVM extends BaseViewModel {

    public final EventLiveData<Resource<Collection>> deleteEvent = new EventLiveData<>();

    public void deleteCollection(Collection collection) {
        deleteEvent.setData(Resource.loading());
        compositeDisposable.add(api.deleteCollection(collection,
                responseBody -> deleteEvent.postEvent(Resource.success(collection)),
                throwable -> deleteEvent.postEvent(Resource.error(throwable))));
    }

}
