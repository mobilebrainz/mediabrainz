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

    public final MutableLiveData<Resource<Map<Tag.TagType, List<Tag>>>> userTagsResource = new MutableLiveData<>();

    public void lazyLoad(String username) {
        Resource<Map<Tag.TagType, List<Tag>>> resource = userTagsResource.getValue();
        if (resource == null || resource.getData() == null || resource.getStatus() != Status.SUCCESS) {
            load(username);
        }
    }

    public void load(String username) {
        userTagsResource.setValue(Resource.loading());
        compositeDisposable.add(api.getTags(
                username,
                tagMap -> userTagsResource.postValue(Resource.success(tagMap)),
                throwable -> userTagsResource.postValue(Resource.error(throwable))));
    }

}
