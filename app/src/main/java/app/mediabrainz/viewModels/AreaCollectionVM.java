package app.mediabrainz.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import app.mediabrainz.api.model.Area;
import app.mediabrainz.data.AreaCollectionDataSource;
import app.mediabrainz.data.NetworkState;

import static app.mediabrainz.data.AreaCollectionDataSource.BROWSE_LIMIT;


public class AreaCollectionVM extends BaseCollectionVM {

    public LiveData<PagedList<Area>> areaCollections;
    private MutableLiveData<AreaCollectionDataSource> areaCollectionDataSource;

    public void load(String collectionId) {
        AreaCollectionDataSource.Factory factory = new AreaCollectionDataSource.Factory(compositeDisposable, collectionId);
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(BROWSE_LIMIT)
                //.setInitialLoadSizeHint(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        areaCollections = new LivePagedListBuilder<>(factory, config).build();
        areaCollectionDataSource = factory.getAreaCollectionDataSourceLiveData();
    }

    public void retry() {
        areaCollectionDataSource.getValue().retry();
    }

    public void refresh() {
        areaCollectionDataSource.getValue().invalidate();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(areaCollectionDataSource, AreaCollectionDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(areaCollectionDataSource, AreaCollectionDataSource::getInitialLoad);
    }

}
