package app.mediabrainz.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import io.reactivex.disposables.CompositeDisposable;
import app.mediabrainz.api.site.Rating;
import app.mediabrainz.api.site.RatingServiceInterface;
import app.mediabrainz.data.NetworkState;
import app.mediabrainz.data.RatingsDataSource;


public class RatingsViewModel extends ViewModel {

    private static final int PAGE_SIZE = 100;

    public LiveData<PagedList<Rating>> ratingsLiveData;
    private CompositeDisposable compositeDisposable;
    private MutableLiveData<RatingsDataSource> ratingsDataSourceMutableLiveData;

    public RatingsViewModel() {
        compositeDisposable = new CompositeDisposable();
    }

    public void load(RatingServiceInterface.RatingType ratingType, String username) {
        RatingsDataSource.Factory factory = new RatingsDataSource.Factory(compositeDisposable, ratingType, username);
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                //.setInitialLoadSizeHint(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        ratingsLiveData = new LivePagedListBuilder<>(factory, config).build();
        ratingsDataSourceMutableLiveData = factory.getRatingsDataSourceLiveData();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    public void retry() {
        ratingsDataSourceMutableLiveData.getValue().retry();
    }

    public void refresh() {
        ratingsDataSourceMutableLiveData.getValue().invalidate();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(ratingsDataSourceMutableLiveData, RatingsDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(ratingsDataSourceMutableLiveData, RatingsDataSource::getInitialLoad);
    }

}
