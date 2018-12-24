package app.mediabrainz.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import app.mediabrainz.api.model.Label;
import app.mediabrainz.data.LabelCollectionDataSource;
import app.mediabrainz.data.NetworkState;
import io.reactivex.disposables.CompositeDisposable;

import static app.mediabrainz.data.LabelCollectionDataSource.BROWSE_LIMIT;


public class LabelCollectionViewModel extends ViewModel {

    public LiveData<PagedList<Label>> labelCollectionLiveData;
    private CompositeDisposable compositeDisposable;
    private MutableLiveData<LabelCollectionDataSource> labelCollectionDataSourceMutableLiveData;

    public LabelCollectionViewModel() {
        compositeDisposable = new CompositeDisposable();
    }

    public void load(String collectionId) {

        LabelCollectionDataSource.Factory factory = new LabelCollectionDataSource.Factory(compositeDisposable, collectionId);
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(BROWSE_LIMIT)
                //.setInitialLoadSizeHint(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        labelCollectionLiveData = new LivePagedListBuilder<>(factory, config).build();
        labelCollectionDataSourceMutableLiveData = factory.getLabelCollectionDataSourceLiveData();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    public void retry() {
        labelCollectionDataSourceMutableLiveData.getValue().retry();
    }

    public void refresh() {
        labelCollectionDataSourceMutableLiveData.getValue().invalidate();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(labelCollectionDataSourceMutableLiveData, LabelCollectionDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(labelCollectionDataSourceMutableLiveData, LabelCollectionDataSource::getInitialLoad);
    }

}
