package app.mediabrainz.util.event;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;


@SuppressWarnings("WeakerAccess")
public class EventLiveData<T> extends MutableLiveData<Event<T>> {

    public void postEvent(T value) {
        super.postValue(new Event<>(value));
    }

    public void postData(T value) {
        super.postValue(new Event<>(value, false));
    }

    public void setEvent(T value) {
        super.setValue(new Event<>(value));
    }

    public void setData(T value) {
        super.setValue(new Event<>(value, false));
    }

    @MainThread
    public void observeEvent(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
        super.observe(owner, new EventObserver<>(observer));
    }

}