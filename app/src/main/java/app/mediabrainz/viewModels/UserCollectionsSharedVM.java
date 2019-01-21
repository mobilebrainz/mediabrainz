package app.mediabrainz.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import app.mediabrainz.api.model.Collection;

import static app.mediabrainz.MediaBrainzApp.api;


public class UserCollectionsSharedVM extends BaseViewModel {

    private final String username;
    public final MutableLiveData<Resource<Collection.CollectionBrowse>> сollectionsLiveData = new MutableLiveData<>();

    private UserCollectionsSharedVM(@NonNull String username) {
        this.username = username;
    }

    public void lazyLoadUserCollections() {
        Resource<Collection.CollectionBrowse> resource = сollectionsLiveData.getValue();
        if (resource == null || resource.getData() == null || resource.getStatus() != Status.SUCCESS) {
            loadUserCollections();
        }
    }

    public void loadUserCollections() {
        сollectionsLiveData.setValue(Resource.loading());
        compositeDisposable.add(api.getCollections(
                username,
                collectionBrowse -> сollectionsLiveData.postValue(Resource.success(collectionBrowse)),
                throwable -> сollectionsLiveData.postValue(Resource.error(throwable)),
                100, 0));
    }

    public void invalidateUserCollections() {
        сollectionsLiveData.setValue(Resource.invalidate());
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        private final String username;

        public Factory(String username) {
            super();
            this.username = username;
        }

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new UserCollectionsSharedVM(username);
        }
    }

}
