package app.mediabrainz.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import app.mediabrainz.data.room.entity.User;
import app.mediabrainz.data.room.repository.UserRepository;
import app.mediabrainz.functions.Action;
import app.mediabrainz.util.event.EventLiveData;


public class UsersVM extends ViewModel {

    private final UserRepository userRepository = new UserRepository();
    public final MutableLiveData<Resource<List<User>>> usersResource = new MutableLiveData<>();
    public final EventLiveData<Resource<User>> deleteEvent = new EventLiveData<>();
    // todo: EventLiveData?
    public final MutableLiveData<Resource<User>> userEvent = new MutableLiveData<>();
    public final EventLiveData<Resource<String>> insertEvent = new EventLiveData<>();

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

    public void delete(User user) {
        userRepository.delete(() -> deleteEvent.postEvent(Resource.success(user)), user);
    }

    public void find(String username) {
        userRepository.findUser(username, user -> userEvent.postValue(Resource.success(user)));
    }

    public void insert(String username) {
        userRepository.insert(() -> insertEvent.postEvent(Resource.success(username)),
                new User(username));
    }

}
