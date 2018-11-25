package app.mediabrainz.api.site;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;


public interface UserProfileServiceInterface {

    class EditorNotFoundException extends Throwable {

        public EditorNotFoundException() {
        }

        public EditorNotFoundException(String message) {
            super(message);
        }
    }

    Flowable<Result<UserProfile>> getUserProfile(String username);

}
