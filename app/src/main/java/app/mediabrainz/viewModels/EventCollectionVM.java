package app.mediabrainz.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import app.mediabrainz.api.model.Event;
import app.mediabrainz.data.EventCollectionDataSource;
import app.mediabrainz.data.NetworkState;

import static app.mediabrainz.data.EventCollectionDataSource.BROWSE_LIMIT;


public class EventCollectionVM extends BaseCollectionVM {

    public LiveData<PagedList<Event>> eventCollections;
    private MutableLiveData<EventCollectionDataSource> eventCollectionDataSource;

    public void load(String collectionId) {
        EventCollectionDataSource.Factory factory = new EventCollectionDataSource.Factory(compositeDisposable, collectionId);
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(BROWSE_LIMIT)
                //.setInitialLoadSizeHint(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        eventCollections = new LivePagedListBuilder<>(factory, config).build();
        eventCollectionDataSource = factory.getEventCollectionDataSourceLiveData();
    }

    public void retry() {
        eventCollectionDataSource.getValue().retry();
    }

    public void refresh() {
        eventCollectionDataSource.getValue().invalidate();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(eventCollectionDataSource, EventCollectionDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(eventCollectionDataSource, EventCollectionDataSource::getInitialLoad);
    }

}
