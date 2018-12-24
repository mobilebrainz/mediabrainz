package app.mediabrainz.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import app.mediabrainz.api.model.Area;
import app.mediabrainz.data.AreaCollectionDataSource;
import app.mediabrainz.data.NetworkState;
import io.reactivex.disposables.CompositeDisposable;

import static app.mediabrainz.data.AreaCollectionDataSource.BROWSE_LIMIT;


public class AreaCollectionViewModel extends ViewModel {

    public LiveData<PagedList<Area>> areaCollectionLiveData;
    private CompositeDisposable compositeDisposable;
    private MutableLiveData<AreaCollectionDataSource> areaCollectionDataSourceMutableLiveData;

    public AreaCollectionViewModel() {
        compositeDisposable = new CompositeDisposable();
    }

    public void load(String collectionId) {

        AreaCollectionDataSource.Factory factory = new AreaCollectionDataSource.Factory(compositeDisposable, collectionId);
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(BROWSE_LIMIT)
                //.setInitialLoadSizeHint(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        areaCollectionLiveData = new LivePagedListBuilder<>(factory, config).build();
        areaCollectionDataSourceMutableLiveData = factory.getAreaCollectionDataSourceLiveData();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    public void retry() {
        areaCollectionDataSourceMutableLiveData.getValue().retry();
    }

    public void refresh() {
        areaCollectionDataSourceMutableLiveData.getValue().invalidate();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(areaCollectionDataSourceMutableLiveData, AreaCollectionDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(areaCollectionDataSourceMutableLiveData, AreaCollectionDataSource::getInitialLoad);
    }

}
