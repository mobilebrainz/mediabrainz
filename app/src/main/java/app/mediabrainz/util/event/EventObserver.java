package app.mediabrainz.util.event;


import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class EventObserver<T> implements Observer<Event<T>> {

    private final Observer<T> observer;

    public EventObserver(@NonNull Observer<T> observer) {
        this.observer = observer;
    }

    @Override
    public void onChanged(@Nullable Event<T> event) {
        if (event != null) {
            T content = event.getContentIfNotHandled();
            if (content != null) {
                observer.onChanged(content);
            }
        }
    }
}
