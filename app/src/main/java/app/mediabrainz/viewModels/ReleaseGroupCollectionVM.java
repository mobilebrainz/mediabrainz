package app.mediabrainz.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.data.NetworkState;
import app.mediabrainz.data.ReleaseGroupCollectionDataSource;

import static app.mediabrainz.data.ReleaseGroupCollectionDataSource.BROWSE_LIMIT;


public class ReleaseGroupCollectionVM extends BaseCollectionVM {

    public LiveData<PagedList<ReleaseGroup>> rgCollections;
    private MutableLiveData<ReleaseGroupCollectionDataSource> rgCollectionDataSource;

    public void load(String collectionId) {

        ReleaseGroupCollectionDataSource.Factory factory = new ReleaseGroupCollectionDataSource.Factory(compositeDisposable, collectionId);
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(BROWSE_LIMIT)
                //.setInitialLoadSizeHint(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        rgCollections = new LivePagedListBuilder<>(factory, config).build();
        rgCollectionDataSource = factory.getRgCollectionDataSourceLiveData();
    }

    public void retry() {
        rgCollectionDataSource.getValue().retry();
    }

    public void refresh() {
        rgCollectionDataSource.getValue().invalidate();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(rgCollectionDataSource, ReleaseGroupCollectionDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(rgCollectionDataSource, ReleaseGroupCollectionDataSource::getInitialLoad);
    }

}
