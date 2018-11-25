package app.mediabrainz.api.site;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;


public interface RatingServiceInterface {

    enum RatingType {

        ARTIST("artist"),
        RELEASE_GROUP("release_group"),
        RECORDING("recording");

        private final String type;

        RatingType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    Flowable<Result<Rating.Page>> getRatings(RatingType ratingType, String username, int page);

    Flowable<Result<Rating.Page>> getRatings(RatingType ratingType, String username, int page, boolean findCount);

    Flowable<Result<Integer>> getCountPage(RatingType ratingType, String username);

}
