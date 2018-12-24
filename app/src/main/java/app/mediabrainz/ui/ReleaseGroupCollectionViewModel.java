package app.mediabrainz.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.data.NetworkState;
import app.mediabrainz.data.ReleaseGroupCollectionDataSource;
import io.reactivex.disposables.CompositeDisposable;

import static app.mediabrainz.data.ReleaseGroupCollectionDataSource.BROWSE_LIMIT;


public class ReleaseGroupCollectionViewModel extends ViewModel {

    public LiveData<PagedList<ReleaseGroup>> rgCollectionLiveData;
    private CompositeDisposable compositeDisposable;
    private MutableLiveData<ReleaseGroupCollectionDataSource> rgCollectionDataSourceMutableLiveData;

    public ReleaseGroupCollectionViewModel() {
        compositeDisposable = new CompositeDisposable();
    }

    public void load(String collectionId) {

        ReleaseGroupCollectionDataSource.Factory factory = new ReleaseGroupCollectionDataSource.Factory(compositeDisposable, collectionId);
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(BROWSE_LIMIT)
                //.setInitialLoadSizeHint(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        rgCollectionLiveData = new LivePagedListBuilder<>(factory, config).build();
        rgCollectionDataSourceMutableLiveData = factory.getRgCollectionDataSourceLiveData();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    public void retry() {
        rgCollectionDataSourceMutableLiveData.getValue().retry();
    }

    public void refresh() {
        rgCollectionDataSourceMutableLiveData.getValue().invalidate();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(rgCollectionDataSourceMutableLiveData, ReleaseGroupCollectionDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(rgCollectionDataSourceMutableLiveData, ReleaseGroupCollectionDataSource::getInitialLoad);
    }

}
