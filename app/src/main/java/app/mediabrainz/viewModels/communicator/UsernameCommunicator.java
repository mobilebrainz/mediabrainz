package app.mediabrainz.viewModels.communicator;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;


public class UsernameCommunicator extends ViewModel {

    public final MutableLiveData<String> username = new MutableLiveData<>();

}
