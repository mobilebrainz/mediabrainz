package app.mediabrainz.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import app.mediabrainz.api.site.TagEntity;
import app.mediabrainz.api.site.TagServiceInterface;
import app.mediabrainz.data.NetworkState;
import app.mediabrainz.data.TagDataSource;


public class TagVM extends BaseViewModel {

    private static final int PAGE_SIZE = 100;

    public LiveData<PagedList<TagEntity>> tagLiveData;
    private MutableLiveData<TagDataSource> tagDataSourceMutableLiveData;

    public void load(TagServiceInterface.TagType tagType, String tag) {
        TagDataSource.Factory factory = new TagDataSource.Factory(compositeDisposable, tagType, tag);
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                //.setInitialLoadSizeHint(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        tagLiveData = new LivePagedListBuilder<>(factory, config).build();
        tagDataSourceMutableLiveData = factory.getTagDataSourceLiveData();
    }

    public void retry() {
        tagDataSourceMutableLiveData.getValue().retry();
    }

    public void refresh() {
        tagDataSourceMutableLiveData.getValue().invalidate();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(tagDataSourceMutableLiveData, TagDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(tagDataSourceMutableLiveData, TagDataSource::getInitialLoad);
    }

}
