package app.mediabrainz.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import app.mediabrainz.api.model.Release;
import app.mediabrainz.data.NetworkState;
import app.mediabrainz.data.ReleaseCollectionDataSource;

import static app.mediabrainz.data.ReleaseCollectionDataSource.BROWSE_LIMIT;


public class ReleaseCollectionVM extends BaseCollectionVM {

    public LiveData<PagedList<Release>> releaseCollections;
    private MutableLiveData<ReleaseCollectionDataSource> releaseCollectionDataSource;

    public void load(String collectionId) {
        ReleaseCollectionDataSource.Factory factory = new ReleaseCollectionDataSource.Factory(compositeDisposable, collectionId);
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(BROWSE_LIMIT)
                //.setInitialLoadSizeHint(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        releaseCollections = new LivePagedListBuilder<>(factory, config).build();
        releaseCollectionDataSource = factory.getReleaseCollectionDataSourceLiveData();
    }

    public void retry() {
        releaseCollectionDataSource.getValue().retry();
    }

    public void refresh() {
        releaseCollectionDataSource.getValue().invalidate();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(releaseCollectionDataSource, ReleaseCollectionDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(releaseCollectionDataSource, ReleaseCollectionDataSource::getInitialLoad);
    }

}
