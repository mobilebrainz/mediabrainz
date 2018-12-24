package app.mediabrainz.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import app.mediabrainz.api.model.Work;
import app.mediabrainz.data.NetworkState;
import app.mediabrainz.data.WorkCollectionDataSource;
import io.reactivex.disposables.CompositeDisposable;

import static app.mediabrainz.data.WorkCollectionDataSource.BROWSE_LIMIT;


public class WorkCollectionViewModel extends ViewModel {

    public LiveData<PagedList<Work>> workCollectionLiveData;
    private CompositeDisposable compositeDisposable;
    private MutableLiveData<WorkCollectionDataSource> workCollectionDataSourceMutableLiveData;

    public WorkCollectionViewModel() {
        compositeDisposable = new CompositeDisposable();
    }

    public void load(String collectionId) {

        WorkCollectionDataSource.Factory factory = new WorkCollectionDataSource.Factory(compositeDisposable, collectionId);
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(BROWSE_LIMIT)
                //.setInitialLoadSizeHint(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        workCollectionLiveData = new LivePagedListBuilder<>(factory, config).build();
        workCollectionDataSourceMutableLiveData = factory.getWorkCollectionDataSourceLiveData();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    public void retry() {
        workCollectionDataSourceMutableLiveData.getValue().retry();
    }

    public void refresh() {
        workCollectionDataSourceMutableLiveData.getValue().invalidate();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(workCollectionDataSourceMutableLiveData, WorkCollectionDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(workCollectionDataSourceMutableLiveData, WorkCollectionDataSource::getInitialLoad);
    }

}
