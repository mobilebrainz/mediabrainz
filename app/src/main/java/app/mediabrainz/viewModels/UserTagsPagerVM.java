package app.mediabrainz.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;

import app.mediabrainz.api.model.Tag;

import static app.mediabrainz.MediaBrainzApp.api;


public class UserTagsPagerVM extends BaseViewModel {

    private final String username;
    public final MutableLiveData<Resource<Map<Tag.TagType, List<Tag>>>> userTagsResource = new MutableLiveData<>();

    private UserTagsPagerVM(String username) {
        this.username = username;
    }

    public void lazyLoad() {
        Resource<Map<Tag.TagType, List<Tag>>> resource = userTagsResource.getValue();
        if (resource == null || resource.getData() == null || resource.getStatus() != Status.SUCCESS) {
            load();
        }
    }

    public void load() {
        userTagsResource.setValue(Resource.loading());
        compositeDisposable.add(api.getTags(
                username,
                tagMap -> userTagsResource.postValue(Resource.success(tagMap)),
                throwable -> userTagsResource.postValue(Resource.error(throwable))));
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
            return (T) new UserTagsPagerVM(username);
        }
    }

}
