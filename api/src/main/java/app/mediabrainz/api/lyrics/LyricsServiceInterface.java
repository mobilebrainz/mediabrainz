package app.mediabrainz.api.lyrics;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.lyrics.model.LyricsResult;
import app.mediabrainz.api.lyrics.model.LyricsApi;


public interface LyricsServiceInterface {

    Flowable<Result<LyricsResult>> getLyricsWikia(@NonNull String artist, @NonNull String song);

    Flowable<Result<LyricsApi>> getLyricsWikiaApi(@NonNull String artist, @NonNull String song);

}
