package app.mediabrainz.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import app.mediabrainz.api.model.Work;
import app.mediabrainz.data.NetworkState;
import app.mediabrainz.data.WorkCollectionDataSource;

import static app.mediabrainz.data.WorkCollectionDataSource.BROWSE_LIMIT;


public class WorkCollectionVM extends BaseCollectionVM {

    public LiveData<PagedList<Work>> workCollections;
    private MutableLiveData<WorkCollectionDataSource> workCollectionDataSource;

    public void load(String collectionId) {
        WorkCollectionDataSource.Factory factory = new WorkCollectionDataSource.Factory(compositeDisposable, collectionId);
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(BROWSE_LIMIT)
                //.setInitialLoadSizeHint(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        workCollections = new LivePagedListBuilder<>(factory, config).build();
        workCollectionDataSource = factory.getWorkCollectionDataSourceLiveData();
    }

    public void retry() {
        workCollectionDataSource.getValue().retry();
    }

    public void refresh() {
        workCollectionDataSource.getValue().invalidate();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(workCollectionDataSource, WorkCollectionDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(workCollectionDataSource, WorkCollectionDataSource::getInitialLoad);
    }

}
