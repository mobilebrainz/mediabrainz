package app.mediabrainz.viewModels;


import java.util.List;

import app.mediabrainz.api.model.Collection;
import app.mediabrainz.api.site.SiteService;
import app.mediabrainz.util.event.EventLiveData;

import static app.mediabrainz.MediaBrainzApp.api;


public class CollectionCreateEditVM extends BaseViewModel {

    public final EventLiveData<Resource<String>> createEvent = new EventLiveData<>();
    public final EventLiveData<Resource<Boolean>> existEvent = new EventLiveData<>();
    public final EventLiveData<Resource<Collection>> editEvent = new EventLiveData<>();

    public void createCollection(String name, int type, String description, int isPublic) {
        createEvent.setData(Resource.loading());
        compositeDisposable.add(api.createCollection(name, type, description, isPublic,
                responseBody -> createEvent.postEvent(Resource.success(name)),
                throwable -> createEvent.postEvent(Resource.error(throwable))));
    }

    public void existCollection(String name, int type) {
        existCollection(name, SiteService.getCollectionTypeFromSpinner(type - 1));
    }

    public void existCollection(String name, String type) {
        existEvent.setData(Resource.loading());
        compositeDisposable.add(api.getCollections(
                collectionBrowse -> {
                    boolean existName = false;
                    if (collectionBrowse.getCount() > 0) {
                        List<Collection> collections = collectionBrowse.getCollections();
                        for (Collection collection : collections) {
                            if (collection.getName().equalsIgnoreCase(name) && collection.getType().equalsIgnoreCase(type)) {
                                existName = true;
                                break;
                            }
                        }
                    }
                    existEvent.postEvent(Resource.success(existName));
                },
                throwable -> existEvent.postEvent(Resource.error(throwable)),
                100, 0));
    }

    public void editCollection(Collection collection, String name, int type, String description, int isPublic) {
        editEvent.setData(Resource.loading());
        compositeDisposable.add(api.editCollection(collection, name, type, description, isPublic,
                responseBody -> editEvent.postEvent(Resource.success(collection)),
                throwable -> editEvent.postEvent(Resource.error(throwable))));
    }

}
