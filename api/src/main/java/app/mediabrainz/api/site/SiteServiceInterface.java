package app.mediabrainz.api.site;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava2.Result;


public interface SiteServiceInterface {

    interface OnLogin {
        Flowable<Result<ResponseBody>> onLogin();
    }

    Flowable<Result<ResponseBody>> login(OnLogin onLogin);

    Flowable<Result<ResponseBody>> deleteCollection(String collectionId);

    Flowable<Result<ResponseBody>> editCollection(String collectionId, String name, int typeFromSpinner, String description, int publ);

    Flowable<Result<ResponseBody>> createCollection(String name, int typeFromSpinner, String description, int publ);

    Flowable<Result<ResponseBody>> sendEmail(String username, String subject, String message, boolean revealEmail);
}
