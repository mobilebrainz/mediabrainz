package app.mediabrainz.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import app.mediabrainz.api.model.Series;
import app.mediabrainz.data.NetworkState;
import app.mediabrainz.data.SeriesCollectionDataSource;

import static app.mediabrainz.data.SeriesCollectionDataSource.BROWSE_LIMIT;


public class SeriesCollectionVM extends BaseCollectionVM {

    public LiveData<PagedList<Series>> seriesCollections;
    private MutableLiveData<SeriesCollectionDataSource> seriesCollectionDataSource;

    public void load(String collectionId) {
        SeriesCollectionDataSource.Factory factory = new SeriesCollectionDataSource.Factory(compositeDisposable, collectionId);
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(BROWSE_LIMIT)
                //.setInitialLoadSizeHint(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        seriesCollections = new LivePagedListBuilder<>(factory, config).build();
        seriesCollectionDataSource = factory.getSeriesCollectionDataSourceMutableLiveData();
    }

    public void retry() {
        seriesCollectionDataSource.getValue().retry();
    }

    public void refresh() {
        seriesCollectionDataSource.getValue().invalidate();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(seriesCollectionDataSource, SeriesCollectionDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(seriesCollectionDataSource, SeriesCollectionDataSource::getInitialLoad);
    }

}
