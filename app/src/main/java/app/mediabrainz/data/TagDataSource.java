package app.mediabrainz.data;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import app.mediabrainz.api.site.TagEntity;
import app.mediabrainz.api.site.TagServiceInterface;

import static app.mediabrainz.MediaBrainzApp.api;


public class TagDataSource extends PageKeyedDataSource<Integer, TagEntity> {

    private CompositeDisposable compositeDisposable;
    private TagServiceInterface.TagType tagType;
    private String tag;
    private MutableLiveData<NetworkState> networkState = new MutableLiveData<>();
    private MutableLiveData<NetworkState> initialLoad = new MutableLiveData<>();
    private Completable retryCompletable;

    public TagDataSource(CompositeDisposable compositeDisposable, TagServiceInterface.TagType tagType, String tag) {
        this.tagType = tagType;
        this.tag = tag;
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
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, TagEntity> callback) {
        networkState.postValue(NetworkState.LOADING);
        initialLoad.postValue(NetworkState.LOADING);

        compositeDisposable.add(api.getTagEntities(tagType, tag, 1,
                page -> {
                    setRetry(null);
                    initialLoad.postValue(NetworkState.LOADED);
                    callback.onResult(
                            page.getTagEntities(),
                            null,
                            page.getCount() > 1 ? 2 : null);

                    networkState.postValue(NetworkState.LOADED);
                },
                throwable -> {
                    setRetry(() -> loadInitial(params, callback));
                    NetworkState error = NetworkState.error(throwable.getMessage());
                    networkState.postValue(error);
                    initialLoad.postValue(error);
                }));
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, TagEntity> callback) {
        // ignored, since we only ever append to our initial getWikidata
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, TagEntity> callback) {
        networkState.postValue(NetworkState.LOADING);

        compositeDisposable.add(api.getTagEntities(tagType, tag, params.key,
                page -> {
                    setRetry(null);
                    networkState.postValue(NetworkState.LOADED);

                    callback.onResult(
                            page.getTagEntities(),
                            page.getCount() > page.getCurrent() ? page.getCurrent() + 1 : null);
                },
                throwable -> {
                    setRetry(() -> loadAfter(params, callback));
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

    public static class Factory extends PageKeyedDataSource.Factory<Integer, TagEntity> {

        private CompositeDisposable compositeDisposable;
        private TagServiceInterface.TagType tagType;
        private String tag;
        private MutableLiveData<TagDataSource> tagDataSourceLiveData = new MutableLiveData<>();

        public Factory(CompositeDisposable compositeDisposable, TagServiceInterface.TagType tagType, String tag) {
            this.compositeDisposable = compositeDisposable;
            this.tagType = tagType;
            this.tag = tag;
        }

        @Override
        public PageKeyedDataSource<Integer, TagEntity> create() {
            TagDataSource tagDataSource = new TagDataSource(compositeDisposable, tagType, tag);
            tagDataSourceLiveData.postValue(tagDataSource);
            return tagDataSource;
        }

        @NonNull
        public MutableLiveData<TagDataSource> getTagDataSourceLiveData() {
            return tagDataSourceLiveData;
        }

    }
}
