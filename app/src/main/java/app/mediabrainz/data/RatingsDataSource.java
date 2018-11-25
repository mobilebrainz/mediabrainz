package app.mediabrainz.data;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import app.mediabrainz.api.site.Rating;
import app.mediabrainz.api.site.RatingServiceInterface;

import static app.mediabrainz.MediaBrainzApp.api;


public class RatingsDataSource extends PageKeyedDataSource<Integer, Rating> {

    private CompositeDisposable compositeDisposable;
    private RatingServiceInterface.RatingType ratingType;
    private String username;
    private MutableLiveData<NetworkState> networkState = new MutableLiveData<>();
    private MutableLiveData<NetworkState> initialLoad = new MutableLiveData<>();
    /**
     * Keep Completable reference for the retry event
     */
    private Completable retryCompletable;

    public RatingsDataSource(CompositeDisposable compositeDisposable, RatingServiceInterface.RatingType ratingType, String username) {
        this.ratingType = ratingType;
        this.username = username;
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
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Rating> callback) {
        // update network states.
        // we also provide an initial getWikidata state to the listeners so that the UI can know when the first page is loaded.
        networkState.postValue(NetworkState.LOADING);
        initialLoad.postValue(NetworkState.LOADING);

        compositeDisposable.add(api.getRatings(ratingType, username, 1,
                page -> {
                    // clear retry since last request succeeded
                    setRetry(null);
                    initialLoad.postValue(NetworkState.LOADED);
                    callback.onResult(
                            page.getRatings(),
                            null,
                            page.getCount() > 1 ? 2 : null);

                    networkState.postValue(NetworkState.LOADED);
                },
                throwable -> {
                    // keep a Completable for future retry
                    setRetry(() -> loadInitial(params, callback));
                    NetworkState error = NetworkState.error(throwable.getMessage());
                    // publish the error
                    networkState.postValue(error);
                    initialLoad.postValue(error);
                }));
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Rating> callback) {
        // ignored, since we only ever append to our initial getWikidata
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Rating> callback) {
        // set network value to loading.
        networkState.postValue(NetworkState.LOADING);

        compositeDisposable.add(api.getRatings(ratingType, username, params.key,
                page -> {
                    // clear retry since last request succeeded
                    setRetry(null);
                    networkState.postValue(NetworkState.LOADED);

                    callback.onResult(
                            page.getRatings(),
                            page.getCount() > page.getCurrent() ? page.getCurrent() + 1 : null);
                },
                throwable -> {
                    // keep a Completable for future retry
                    setRetry(() -> loadAfter(params, callback));
                    // publish the error
                    networkState.postValue(NetworkState.error(throwable.getMessage()));
                }));
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

    public static class Factory extends PageKeyedDataSource.Factory<Integer, Rating> {

        private CompositeDisposable compositeDisposable;
        private RatingServiceInterface.RatingType ratingType;
        private String username;
        private MutableLiveData<RatingsDataSource> ratingsDataSourceLiveData = new MutableLiveData<>();

        public Factory(CompositeDisposable compositeDisposable, RatingServiceInterface.RatingType ratingType, String username) {
            this.compositeDisposable = compositeDisposable;
            this.ratingType = ratingType;
            this.username = username;
        }

        @Override
        public PageKeyedDataSource<Integer, Rating> create() {
            RatingsDataSource ratingsDataSource = new RatingsDataSource(compositeDisposable, ratingType, username);
            ratingsDataSourceLiveData.postValue(ratingsDataSource);
            return ratingsDataSource;
        }

        @NonNull
        public MutableLiveData<RatingsDataSource> getRatingsDataSourceLiveData() {
            return ratingsDataSourceLiveData;
        }

    }
}
