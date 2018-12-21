package app.mediabrainz.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import app.mediabrainz.api.model.Recording;
import app.mediabrainz.data.NetworkState;
import app.mediabrainz.data.RecordingCollectionDataSource;
import io.reactivex.disposables.CompositeDisposable;

import static app.mediabrainz.data.RecordingCollectionDataSource.BROWSE_LIMIT;


public class RecordingCollectionViewModel extends ViewModel {

    public LiveData<PagedList<Recording>> recordingCollectionLiveData;
    private CompositeDisposable compositeDisposable;
    private MutableLiveData<RecordingCollectionDataSource> recordingCollectionDataSourceMutableLiveData;

    public RecordingCollectionViewModel() {
        compositeDisposable = new CompositeDisposable();
    }

    public void load(String collectionId) {

        RecordingCollectionDataSource.Factory factory = new RecordingCollectionDataSource.Factory(compositeDisposable, collectionId);
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(BROWSE_LIMIT)
                //.setInitialLoadSizeHint(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        recordingCollectionLiveData = new LivePagedListBuilder<>(factory, config).build();
        recordingCollectionDataSourceMutableLiveData = factory.getRecordingCollectionDataSourceLiveData();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    public void retry() {
        recordingCollectionDataSourceMutableLiveData.getValue().retry();
    }

    public void refresh() {
        recordingCollectionDataSourceMutableLiveData.getValue().invalidate();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(recordingCollectionDataSourceMutableLiveData, RecordingCollectionDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(recordingCollectionDataSourceMutableLiveData, RecordingCollectionDataSource::getInitialLoad);
    }

}
