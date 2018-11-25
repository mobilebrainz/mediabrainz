package app.mediabrainz.api.other;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.xml.Metadata;


public interface MetadataBrowseServiceInterface {

    Flowable<Result<Metadata>> browseRating();
    Flowable<Result<Metadata>> browseTag();

}
