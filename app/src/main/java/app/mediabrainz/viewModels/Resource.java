package app.mediabrainz.viewModels;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class Resource<T> {

    @NonNull
    private final Status status;
    @Nullable
    private final T data;
    @Nullable
    private final Throwable throwable;

    private Resource(@NonNull Status status, @Nullable T data, @Nullable Throwable throwable) {
        this.status = status;
        this.data = data;
        this.throwable = throwable;
    }

    @NonNull
    public Status getStatus() {
        return status;
    }

    @Nullable
    public T getData() {
        return data;
    }

    @Nullable
    public Throwable getThrowable() {
        return throwable;
    }

    public static <T> Resource<T> success(@NonNull T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(Throwable throwable) {
        return new Resource<>(Status.ERROR, null, throwable);
    }

    public static <T> Resource<T> loading() {
        return new Resource<>(Status.LOADING, null, null);
    }

    public static <T> Resource<T> invalidate() {
        return new Resource<>(Status.INVALID, null, null);
    }

}
