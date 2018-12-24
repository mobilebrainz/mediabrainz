package app.mediabrainz.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import app.mediabrainz.api.model.Artist;
import app.mediabrainz.data.ArtistCollectionDataSource;
import app.mediabrainz.data.NetworkState;
import io.reactivex.disposables.CompositeDisposable;

import static app.mediabrainz.data.ArtistCollectionDataSource.BROWSE_LIMIT;


public class ArtistCollectionViewModel extends ViewModel {

    public LiveData<PagedList<Artist>> artistCollectionLiveData;
    private CompositeDisposable compositeDisposable;
    private MutableLiveData<ArtistCollectionDataSource> artistCollectionDataSourceMutableLiveData;

    public ArtistCollectionViewModel() {
        compositeDisposable = new CompositeDisposable();
    }

    public void load(String collectionId) {

        ArtistCollectionDataSource.Factory factory = new ArtistCollectionDataSource.Factory(compositeDisposable, collectionId);
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(BROWSE_LIMIT)
                //.setInitialLoadSizeHint(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        artistCollectionLiveData = new LivePagedListBuilder<>(factory, config).build();
        artistCollectionDataSourceMutableLiveData = factory.getArtistCollectionDataSourceLiveData();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    public void retry() {
        artistCollectionDataSourceMutableLiveData.getValue().retry();
    }

    public void refresh() {
        artistCollectionDataSourceMutableLiveData.getValue().invalidate();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(artistCollectionDataSourceMutableLiveData, ArtistCollectionDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(artistCollectionDataSourceMutableLiveData, ArtistCollectionDataSource::getInitialLoad);
    }

}
