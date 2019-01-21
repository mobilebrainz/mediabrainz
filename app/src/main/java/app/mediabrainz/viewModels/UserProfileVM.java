package app.mediabrainz.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import app.mediabrainz.api.site.UserProfile;

import static app.mediabrainz.MediaBrainzApp.api;


public class UserProfileVM extends BaseViewModel {

    private final String username;
    public final MutableLiveData<Resource<UserProfile>> userProfileResource = new MutableLiveData<>();

    private UserProfileVM(@NonNull String username) {
        this.username = username;
    }

    public void lazyLoad() {
        Resource<UserProfile> resource = userProfileResource.getValue();
        if (resource == null || resource.getData() == null || resource.getStatus() != Status.SUCCESS) {
            load();
        }
    }

    public void load() {
        userProfileResource.setValue(Resource.loading());
        compositeDisposable.add(api.getUserProfile(
                username,
                userProfile -> userProfileResource.postValue(Resource.success(userProfile)),
                throwable -> userProfileResource.postValue(Resource.error(throwable))));
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        private final String username;

        public Factory(@NonNull String username) {
            super();
            this.username = username;
        }

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new UserProfileVM(username);
        }
    }

}
