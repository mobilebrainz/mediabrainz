package app.mediabrainz.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import app.mediabrainz.api.model.Artist;
import app.mediabrainz.data.ArtistCollectionDataSource;
import app.mediabrainz.data.NetworkState;

import static app.mediabrainz.data.ArtistCollectionDataSource.BROWSE_LIMIT;


public class ArtistCollectionVM extends BaseCollectionVM {

    public LiveData<PagedList<Artist>> artistCollections;
    private MutableLiveData<ArtistCollectionDataSource> artistCollectionDataSource;

    public void load(String collectionId) {
        ArtistCollectionDataSource.Factory factory = new ArtistCollectionDataSource.Factory(compositeDisposable, collectionId);
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(BROWSE_LIMIT)
                //.setInitialLoadSizeHint(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        artistCollections = new LivePagedListBuilder<>(factory, config).build();
        artistCollectionDataSource = factory.getArtistCollectionDataSourceLiveData();
    }

    public void retry() {
        artistCollectionDataSource.getValue().retry();
    }

    public void refresh() {
        artistCollectionDataSource.getValue().invalidate();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(artistCollectionDataSource, ArtistCollectionDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(artistCollectionDataSource, ArtistCollectionDataSource::getInitialLoad);
    }

}
