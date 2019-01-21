package app.mediabrainz.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import app.mediabrainz.api.model.Label;
import app.mediabrainz.data.LabelCollectionDataSource;
import app.mediabrainz.data.NetworkState;

import static app.mediabrainz.data.LabelCollectionDataSource.BROWSE_LIMIT;


public class LabelCollectionVM extends BaseCollectionVM {

    public LiveData<PagedList<Label>> labelCollections;
    private MutableLiveData<LabelCollectionDataSource> labelCollectionDataSource;

    public void load(String collectionId) {
        LabelCollectionDataSource.Factory factory = new LabelCollectionDataSource.Factory(compositeDisposable, collectionId);
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(BROWSE_LIMIT)
                //.setInitialLoadSizeHint(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        labelCollections = new LivePagedListBuilder<>(factory, config).build();
        labelCollectionDataSource = factory.getLabelCollectionDataSourceLiveData();
    }

    public void retry() {
        labelCollectionDataSource.getValue().retry();
    }

    public void refresh() {
        labelCollectionDataSource.getValue().invalidate();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(labelCollectionDataSource, LabelCollectionDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(labelCollectionDataSource, LabelCollectionDataSource::getInitialLoad);
    }

}
