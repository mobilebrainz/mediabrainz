package app.mediabrainz.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import app.mediabrainz.api.model.Place;
import app.mediabrainz.data.NetworkState;
import app.mediabrainz.data.PlaceCollectionDataSource;
import io.reactivex.disposables.CompositeDisposable;

import static app.mediabrainz.data.PlaceCollectionDataSource.BROWSE_LIMIT;


public class PlaceCollectionViewModel extends ViewModel {

    public LiveData<PagedList<Place>> placeCollectionLiveData;
    private CompositeDisposable compositeDisposable;
    private MutableLiveData<PlaceCollectionDataSource> placeCollectionDataSourceMutableLiveData;

    public PlaceCollectionViewModel() {
        compositeDisposable = new CompositeDisposable();
    }

    public void load(String collectionId) {

        PlaceCollectionDataSource.Factory factory = new PlaceCollectionDataSource.Factory(compositeDisposable, collectionId);
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(BROWSE_LIMIT)
                //.setInitialLoadSizeHint(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        placeCollectionLiveData = new LivePagedListBuilder<>(factory, config).build();
        placeCollectionDataSourceMutableLiveData = factory.getPlaceCollectionDataSourceMutableLiveData();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    public void retry() {
        placeCollectionDataSourceMutableLiveData.getValue().retry();
    }

    public void refresh() {
        placeCollectionDataSourceMutableLiveData.getValue().invalidate();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(placeCollectionDataSourceMutableLiveData, PlaceCollectionDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(placeCollectionDataSourceMutableLiveData, PlaceCollectionDataSource::getInitialLoad);
    }

}
