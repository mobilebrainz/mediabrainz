package app.mediabrainz.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import app.mediabrainz.data.room.entity.User;
import app.mediabrainz.data.room.repository.UserRepository;


public class UsersVM extends ViewModel {

    private final UserRepository userRepository = new UserRepository();
    public final MutableLiveData<Resource<List<User>>> usersResource = new MutableLiveData<>();

    public void lazyLoad() {
        Resource<List<User>> resource = usersResource.getValue();
        if (resource == null || resource.getData() == null || resource.getStatus() != Status.SUCCESS) {
            load();
        }
    }

    public void load() {
        usersResource.setValue(Resource.loading());
        userRepository.getUsers(users -> usersResource.postValue(Resource.success(users)));
    }

}
