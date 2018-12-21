package app.mediabrainz.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import app.mediabrainz.api.model.Series;
import app.mediabrainz.data.NetworkState;
import app.mediabrainz.data.SeriesCollectionDataSource;
import io.reactivex.disposables.CompositeDisposable;

import static app.mediabrainz.data.SeriesCollectionDataSource.BROWSE_LIMIT;


public class SeriesCollectionViewModel extends ViewModel {

    public LiveData<PagedList<Series>> seriesCollectionLiveData;
    private CompositeDisposable compositeDisposable;
    private MutableLiveData<SeriesCollectionDataSource> seriesCollectionDataSourceMutableLiveData;

    public SeriesCollectionViewModel() {
        compositeDisposable = new CompositeDisposable();
    }

    public void load(String collectionId) {

        SeriesCollectionDataSource.Factory factory = new SeriesCollectionDataSource.Factory(compositeDisposable, collectionId);
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(BROWSE_LIMIT)
                //.setInitialLoadSizeHint(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        seriesCollectionLiveData = new LivePagedListBuilder<>(factory, config).build();
        seriesCollectionDataSourceMutableLiveData = factory.getSeriesCollectionDataSourceMutableLiveData();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    public void retry() {
        seriesCollectionDataSourceMutableLiveData.getValue().retry();
    }

    public void refresh() {
        seriesCollectionDataSourceMutableLiveData.getValue().invalidate();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(seriesCollectionDataSourceMutableLiveData, SeriesCollectionDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(seriesCollectionDataSourceMutableLiveData, SeriesCollectionDataSource::getInitialLoad);
    }

}
