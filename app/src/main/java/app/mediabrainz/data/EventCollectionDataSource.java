package app.mediabrainz.data;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

import app.mediabrainz.api.model.Event;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

import static app.mediabrainz.MediaBrainzApp.api;


public class EventCollectionDataSource extends PageKeyedDataSource<Integer, Event> {

    public static final int BROWSE_LIMIT = 100;

    private CompositeDisposable compositeDisposable;
    private String id;
    private MutableLiveData<NetworkState> networkState = new MutableLiveData<>();
    private MutableLiveData<NetworkState> initialLoad = new MutableLiveData<>();
    private Completable retryCompletable;

    public EventCollectionDataSource(CompositeDisposable compositeDisposable, String collectionId) {
        this.id = collectionId;
        this.compositeDisposable = compositeDisposable;
    }

    public void retry() {
        if (retryCompletable != null) {
            compositeDisposable.add(retryCompletable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            () -> {
                            },
                            throwable -> {
                                //Timber.e(throwable.getMessage());
                            }));
        }
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Event> callback) {
        networkState.postValue(NetworkState.LOADING);
        initialLoad.postValue(NetworkState.LOADING);

        compositeDisposable.add(api.getEventsFromCollection(
                id,
                eventBrowse -> {
                    setRetry(null);
                    initialLoad.postValue(NetworkState.LOADED);

                    List<Event> events = eventBrowse.getEvents();
                    sort(events);

                    callback.onResult(events, null, eventBrowse.getCount() > BROWSE_LIMIT ? BROWSE_LIMIT : null);
                    networkState.postValue(NetworkState.LOADED);
                },
                throwable -> {
                    setRetry(() -> loadInitial(params, callback));
                    NetworkState error = NetworkState.error(throwable.getMessage());
                    networkState.postValue(error);
                    initialLoad.postValue(error);
                },
                BROWSE_LIMIT, 0));
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Event> callback) {
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Event> callback) {
        networkState.postValue(NetworkState.LOADING);

        compositeDisposable.add(api.getEventsFromCollection(
                id,
                eventBrowse -> {
                    setRetry(null);
                    initialLoad.postValue(NetworkState.LOADED);

                    List<Event> events = eventBrowse.getEvents();
                    sort(events);

                    int nextOffset = eventBrowse.getOffset() + BROWSE_LIMIT;
                    callback.onResult(events, eventBrowse.getCount() > nextOffset ? nextOffset : null);

                    networkState.postValue(NetworkState.LOADED);
                },
                throwable -> {
                    setRetry(() -> loadAfter(params, callback));
                    networkState.postValue(NetworkState.error(throwable.getMessage()));
                },
                BROWSE_LIMIT, params.key));
    }

    private void sort(List<Event> events) {
        Collections.sort(events, (a1, a2) -> (a1.getName()).compareTo(a2.getName()));
    }

    @NonNull
    public MutableLiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    @NonNull
    public MutableLiveData<NetworkState> getInitialLoad() {
        return initialLoad;
    }

    private void setRetry(final Action action) {
        this.retryCompletable = action == null ? null : Completable.fromAction(action);
    }

    public static class Factory extends PageKeyedDataSource.Factory<Integer, Event> {

        private CompositeDisposable compositeDisposable;
        private String id;
        private MutableLiveData<EventCollectionDataSource> eventCollectionDataSourceLiveData = new MutableLiveData<>();

        public Factory(CompositeDisposable compositeDisposable, String collectionId) {
            this.compositeDisposable = compositeDisposable;
            this.id = collectionId;
        }

        @Override
        public PageKeyedDataSource<Integer, Event> create() {
            EventCollectionDataSource eventCollectionDataSource = new EventCollectionDataSource(compositeDisposable, id);
            eventCollectionDataSourceLiveData.postValue(eventCollectionDataSource);
            return eventCollectionDataSource;
        }

        @NonNull
        public MutableLiveData<EventCollectionDataSource> getEventCollectionDataSourceLiveData() {
            return eventCollectionDataSourceLiveData;
        }
    }
}
