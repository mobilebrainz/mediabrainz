package app.mediabrainz.apihandler;

import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import retrofit2.adapter.rxjava2.Result;

import app.mediabrainz.functions.Consumer;
import app.mediabrainz.functions.ErrorHandler;


public class ApiHandler {

    private static final long DELAY_503 = 1000;

    private static  <T> Flowable<Result<T>> observeOn(Flowable<Result<T>> publisher) {
        return publisher.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Disposable subscribe(@NonNull Flowable<Result<T>> publisher, Consumer<T> consumer, ErrorHandler errorHandler) {
        publisher = observeOn(publisher);
        return publisher.subscribe(
                result -> handle(result, consumer, errorHandler),
                throwable -> {
                    if (errorHandler != null) errorHandler.handle(throwable);
                });
    }

    private static <T> void handle(Result<T> result, Consumer<T> consumer, ErrorHandler errorHandler) {
        Response<T> response = result.response();
        if (!result.isError()) {
            if (response.code() == 200) {
                if (consumer != null) {
                    consumer.accept(response.body());
                }
            } else if (errorHandler != null) {
                errorHandler.handle(new HttpException(response));
            }
        } else if (errorHandler != null) {
            errorHandler.handle(result.error());
        }
    }

    public static <T> Disposable subscribe503(@NonNull Flowable<Result<T>> publisher, Consumer<T> consumer, ErrorHandler errorHandler) {
        publisher = observeOn(publisher);
        Flowable<Result<T>> finalPublisher = publisher;
        return publisher.subscribe(
                result -> handle503(finalPublisher, result, consumer, errorHandler),
                throwable -> {
                    if (errorHandler != null) errorHandler.handle(throwable);
                });
    }

    private static <T> void handle503(
            Flowable<Result<T>> publisher,
            Result<T> result,
            Consumer<T> consumer,
            ErrorHandler errorHandler) {

        Response<T> response = result.response();
        if (response != null && response.code() == 503) {
            publisher.delay(DELAY_503, TimeUnit.MILLISECONDS).subscribe(
                    r -> handle503(publisher, result, consumer, errorHandler),
                    throwable -> {
                        if (errorHandler != null) errorHandler.handle(throwable);
                    });
        } else {
            handle(result, consumer, errorHandler);
        }
    }

}
