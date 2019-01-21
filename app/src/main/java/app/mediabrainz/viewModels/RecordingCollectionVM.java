package app.mediabrainz.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import app.mediabrainz.api.model.Recording;
import app.mediabrainz.data.NetworkState;
import app.mediabrainz.data.RecordingCollectionDataSource;

import static app.mediabrainz.data.RecordingCollectionDataSource.BROWSE_LIMIT;


public class RecordingCollectionVM extends BaseCollectionVM {

    public LiveData<PagedList<Recording>> recordingCollections;
    private MutableLiveData<RecordingCollectionDataSource> recordingCollectionDataSource;

    public void load(String collectionId) {
        RecordingCollectionDataSource.Factory factory = new RecordingCollectionDataSource.Factory(compositeDisposable, collectionId);
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(BROWSE_LIMIT)
                //.setInitialLoadSizeHint(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        recordingCollections = new LivePagedListBuilder<>(factory, config).build();
        recordingCollectionDataSource = factory.getRecordingCollectionDataSourceLiveData();
    }

    public void retry() {
        recordingCollectionDataSource.getValue().retry();
    }

    public void refresh() {
        recordingCollectionDataSource.getValue().invalidate();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(recordingCollectionDataSource, RecordingCollectionDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(recordingCollectionDataSource, RecordingCollectionDataSource::getInitialLoad);
    }

}
