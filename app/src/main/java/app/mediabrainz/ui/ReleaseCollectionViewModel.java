package app.mediabrainz.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import app.mediabrainz.api.model.Release;
import app.mediabrainz.data.NetworkState;
import app.mediabrainz.data.ReleaseCollectionDataSource;
import io.reactivex.disposables.CompositeDisposable;

import static app.mediabrainz.data.ReleaseCollectionDataSource.BROWSE_LIMIT;


public class ReleaseCollectionViewModel extends ViewModel {

    public LiveData<PagedList<Release>> releaseCollectionLiveData;
    private CompositeDisposable compositeDisposable;
    private MutableLiveData<ReleaseCollectionDataSource> releaseCollectionDataSourceMutableLiveData;

    public ReleaseCollectionViewModel() {
        compositeDisposable = new CompositeDisposable();
    }

    public void load(String collectionId) {

        ReleaseCollectionDataSource.Factory factory = new ReleaseCollectionDataSource.Factory(compositeDisposable, collectionId);
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(BROWSE_LIMIT)
                //.setInitialLoadSizeHint(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        releaseCollectionLiveData = new LivePagedListBuilder<>(factory, config).build();
        releaseCollectionDataSourceMutableLiveData = factory.getReleaseCollectionDataSourceLiveData();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    public void retry() {
        releaseCollectionDataSourceMutableLiveData.getValue().retry();
    }

    public void refresh() {
        releaseCollectionDataSourceMutableLiveData.getValue().invalidate();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(releaseCollectionDataSourceMutableLiveData, ReleaseCollectionDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(releaseCollectionDataSourceMutableLiveData, ReleaseCollectionDataSource::getInitialLoad);
    }

}
