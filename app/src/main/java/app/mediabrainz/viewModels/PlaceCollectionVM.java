package app.mediabrainz.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import app.mediabrainz.api.model.Place;
import app.mediabrainz.data.NetworkState;
import app.mediabrainz.data.PlaceCollectionDataSource;

import static app.mediabrainz.data.PlaceCollectionDataSource.BROWSE_LIMIT;


public class PlaceCollectionVM extends BaseCollectionVM {

    public LiveData<PagedList<Place>> placeCollections;
    private MutableLiveData<PlaceCollectionDataSource> placeCollectionDataSource;

    public void load(String collectionId) {
        PlaceCollectionDataSource.Factory factory = new PlaceCollectionDataSource.Factory(compositeDisposable, collectionId);
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(BROWSE_LIMIT)
                //.setInitialLoadSizeHint(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        placeCollections = new LivePagedListBuilder<>(factory, config).build();
        placeCollectionDataSource = factory.getPlaceCollectionDataSourceMutableLiveData();
    }

    public void retry() {
        placeCollectionDataSource.getValue().retry();
    }

    public void refresh() {
        placeCollectionDataSource.getValue().invalidate();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(placeCollectionDataSource, PlaceCollectionDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(placeCollectionDataSource, PlaceCollectionDataSource::getInitialLoad);
    }

}
