package app.mediabrainz.functions;


public interface Consumer<T> {

    void accept(T t);
}
