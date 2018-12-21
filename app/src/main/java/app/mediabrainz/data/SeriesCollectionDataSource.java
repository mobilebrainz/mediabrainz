package app.mediabrainz.data;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

import app.mediabrainz.api.model.Series;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

import static app.mediabrainz.MediaBrainzApp.api;


public class SeriesCollectionDataSource extends PageKeyedDataSource<Integer, Series> {

    public static final int BROWSE_LIMIT = 100;

    private CompositeDisposable compositeDisposable;
    private String id;
    private MutableLiveData<NetworkState> networkState = new MutableLiveData<>();
    private MutableLiveData<NetworkState> initialLoad = new MutableLiveData<>();
    private Completable retryCompletable;

    public SeriesCollectionDataSource(CompositeDisposable compositeDisposable, String collectionId) {
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
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Series> callback) {
        networkState.postValue(NetworkState.LOADING);
        initialLoad.postValue(NetworkState.LOADING);

        compositeDisposable.add(api.getSeriesFromCollection(
                id,
                seriesBrowse -> {
                    setRetry(null);
                    initialLoad.postValue(NetworkState.LOADED);

                    List<Series> series = seriesBrowse.getSeries();
                    sort(series);

                    callback.onResult(series, null, seriesBrowse.getCount() > BROWSE_LIMIT ? BROWSE_LIMIT : null);
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
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Series> callback) {
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Series> callback) {
        networkState.postValue(NetworkState.LOADING);

        compositeDisposable.add(api.getSeriesFromCollection(
                id,
                seriesBrowse -> {
                    setRetry(null);
                    initialLoad.postValue(NetworkState.LOADED);

                    List<Series> series = seriesBrowse.getSeries();
                    sort(series);

                    int nextOffset = seriesBrowse.getOffset() + BROWSE_LIMIT;
                    callback.onResult(series, seriesBrowse.getCount() > nextOffset ? nextOffset : null);

                    networkState.postValue(NetworkState.LOADED);
                },
                throwable -> {
                    setRetry(() -> loadAfter(params, callback));
                    networkState.postValue(NetworkState.error(throwable.getMessage()));
                },
                BROWSE_LIMIT, params.key));
    }

    private void sort(List<Series> series) {
        Collections.sort(series, (a1, a2) -> (a1.getName()).compareTo(a2.getName()));
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

    public static class Factory extends PageKeyedDataSource.Factory<Integer, Series> {

        private CompositeDisposable compositeDisposable;
        private String id;
        private MutableLiveData<SeriesCollectionDataSource> seriesCollectionDataSourceMutableLiveData = new MutableLiveData<>();

        public Factory(CompositeDisposable compositeDisposable, String collectionId) {
            this.compositeDisposable = compositeDisposable;
            this.id = collectionId;
        }

        @Override
        public PageKeyedDataSource<Integer, Series> create() {
            SeriesCollectionDataSource areaCollectionDataSource = new SeriesCollectionDataSource(compositeDisposable, id);
            seriesCollectionDataSourceMutableLiveData.postValue(areaCollectionDataSource);
            return areaCollectionDataSource;
        }

        @NonNull
        public MutableLiveData<SeriesCollectionDataSource> getSeriesCollectionDataSourceMutableLiveData() {
            return seriesCollectionDataSourceMutableLiveData;
        }
    }
}
