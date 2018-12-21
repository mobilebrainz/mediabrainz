package app.mediabrainz.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import app.mediabrainz.api.model.Event;
import app.mediabrainz.data.EventCollectionDataSource;
import app.mediabrainz.data.NetworkState;
import io.reactivex.disposables.CompositeDisposable;

import static app.mediabrainz.data.EventCollectionDataSource.BROWSE_LIMIT;


public class EventCollectionViewModel extends ViewModel {

    public LiveData<PagedList<Event>> eventCollectionLiveData;
    private CompositeDisposable compositeDisposable;
    private MutableLiveData<EventCollectionDataSource> eventCollectionDataSourceMutableLiveData;

    public EventCollectionViewModel() {
        compositeDisposable = new CompositeDisposable();
    }

    public void load(String collectionId) {

        EventCollectionDataSource.Factory factory = new EventCollectionDataSource.Factory(compositeDisposable, collectionId);
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(BROWSE_LIMIT)
                //.setInitialLoadSizeHint(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        eventCollectionLiveData = new LivePagedListBuilder<>(factory, config).build();
        eventCollectionDataSourceMutableLiveData = factory.getEventCollectionDataSourceLiveData();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    public void retry() {
        eventCollectionDataSourceMutableLiveData.getValue().retry();
    }

    public void refresh() {
        eventCollectionDataSourceMutableLiveData.getValue().invalidate();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(eventCollectionDataSourceMutableLiveData, EventCollectionDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(eventCollectionDataSourceMutableLiveData, EventCollectionDataSource::getInitialLoad);
    }

}
