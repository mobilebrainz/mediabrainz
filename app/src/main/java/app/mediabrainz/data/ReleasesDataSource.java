package app.mediabrainz.data;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import app.mediabrainz.api.browse.ReleaseBrowseService;
import app.mediabrainz.api.model.Release;
import app.mediabrainz.util.MbUtils;

import static app.mediabrainz.MediaBrainzApp.api;


public class ReleasesDataSource extends PageKeyedDataSource<Integer, Release> {

    public static final int RELEASE_BROWSE_LIMIT = 100;

    private CompositeDisposable compositeDisposable;
    private ReleaseBrowseService.ReleaseBrowseEntityType type;
    private String mbid;
    private MutableLiveData<NetworkState> networkState = new MutableLiveData<>();
    private MutableLiveData<NetworkState> initialLoad = new MutableLiveData<>();
    /**
     * Keep Completable reference for the retry event
     */
    private Completable retryCompletable;

    public ReleasesDataSource(CompositeDisposable compositeDisposable, String mbid, ReleaseBrowseService.ReleaseBrowseEntityType type) {
        this.mbid = mbid;
        this.compositeDisposable = compositeDisposable;
        this.type = type;
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
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Release> callback) {
        // update network states.
        // we also provide an initial getWikidata state to the listeners so that the UI can know when the first page is loaded.
        networkState.postValue(NetworkState.LOADING);
        initialLoad.postValue(NetworkState.LOADING);

        compositeDisposable.add(api.getReleasesByType(
                type,
                mbid,
                releaseBrowse -> {
                    // clear retry since last request succeeded
                    setRetry(null);
                    initialLoad.postValue(NetworkState.LOADED);

                    List<Release> releases = releaseBrowse.getReleases();
                    sort(releases);

                    callback.onResult(releases, null,
                            // find offset
                            releaseBrowse.getCount() > RELEASE_BROWSE_LIMIT ? RELEASE_BROWSE_LIMIT : null);

                    networkState.postValue(NetworkState.LOADED);
                },
                throwable -> {
                    // keep a Completable for future retry
                    setRetry(() -> loadInitial(params, callback));
                    NetworkState error = NetworkState.error(throwable.getMessage());
                    // publish the error
                    networkState.postValue(error);
                    initialLoad.postValue(error);
                },
                RELEASE_BROWSE_LIMIT, 0));
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Release> callback) {
        // ignored, since we only ever append to our initial getWikidata
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Release> callback) {
        // set network value to loading.
        networkState.postValue(NetworkState.LOADING);

        compositeDisposable.add(api.getReleasesByType(
                type,
                mbid,
                releaseBrowse -> {
                    // clear retry since last request succeeded
                    setRetry(null);
                    initialLoad.postValue(NetworkState.LOADED);

                    List<Release> releases = releaseBrowse.getReleases();
                    sort(releases);

                    int nextOffset = releaseBrowse.getOffset() + RELEASE_BROWSE_LIMIT;

                    callback.onResult(releases, releaseBrowse.getCount() > nextOffset ? nextOffset : null);

                    networkState.postValue(NetworkState.LOADED);
                },
                throwable -> {
                    // keep a Completable for future retry
                    setRetry(() -> loadAfter(params, callback));
                    // publish the error
                    networkState.postValue(NetworkState.error(throwable.getMessage()));
                },
                RELEASE_BROWSE_LIMIT, params.key));
    }

    private void sort(List<Release> releases) {
        Comparator<Release> sortDate = (r1, r2) -> MbUtils.getNumberDate(r1.getDate()) - MbUtils.getNumberDate(r2.getDate());
        Collections.sort(releases, sortDate);
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

    public static class Factory extends PageKeyedDataSource.Factory<Integer, Release> {

        private CompositeDisposable compositeDisposable;
        private String mbid;
        private ReleaseBrowseService.ReleaseBrowseEntityType type;
        private MutableLiveData<ReleasesDataSource> releasesDataSourceLiveData = new MutableLiveData<>();

        public Factory(CompositeDisposable compositeDisposable, String mbid, ReleaseBrowseService.ReleaseBrowseEntityType type) {
            this.compositeDisposable = compositeDisposable;
            this.mbid = mbid;
            this.type = type;
        }

        @Override
        public PageKeyedDataSource<Integer, Release> create() {
            ReleasesDataSource releasesDataSource = new ReleasesDataSource(compositeDisposable, mbid, type);
            releasesDataSourceLiveData.postValue(releasesDataSource);
            return releasesDataSource;
        }

        @NonNull
        public MutableLiveData<ReleasesDataSource> getReleasesDataSourceLiveData() {
            return releasesDataSourceLiveData;
        }

    }
}
