package app.mediabrainz.api.other;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.xml.Metadata;
import app.mediabrainz.api.model.xml.RecordingXML;
import app.mediabrainz.api.model.xml.ReleaseXML;
import app.mediabrainz.api.model.xml.UserTagXML;


public interface PostWebServiceInterface {

    Flowable<Result<Metadata>> postMetadata(PathType pathType, Metadata metadata);

    Flowable<Result<Metadata>> postArtistRating(String artistId, int userRating);
    Flowable<Result<Metadata>> postRecordingRating(String recordingId, int userRating);
    Flowable<Result<Metadata>> postReleaseGroupRating(String releaseGroupId, int userRating);

    Flowable<Result<Metadata>> postArtistTags(String artistId, UserTagXML... tags);
    Flowable<Result<Metadata>> postRecordingTags(String recordingId, UserTagXML... tags);
    Flowable<Result<Metadata>> postReleaseGroupTags(String releaseGroupId, UserTagXML... tags);

    Flowable<Result<Metadata>> postTagToArtists(UserTagXML tag, String... artistIds);
    Flowable<Result<Metadata>> postTagToReleaseGroups(UserTagXML tag, String... releaseGroupIds);
    Flowable<Result<Metadata>> postTagToRecordings(UserTagXML tag, String... recordingIds);

    //TODO: require tests postBarcodes() and postIsrcs()
    Flowable<Result<Metadata>> postBarcodes(ReleaseXML... releases);
    Flowable<Result<Metadata>> postBarcode(String releaseId, String barcode);
    Flowable<Result<Metadata>> postIsrcs(RecordingXML... recordings);
    Flowable<Result<Metadata>> postIsrcs(String recordingId, String... isrcs);

    enum PathType {

        RATING("rating"),
        TAG("tag"),
        RELEASE("release"),
        RECORDING("recording");

        private final String param;
        PathType(String param) {
            this.param = param;
        }
        @Override
        public String toString() {
            return param;
        }
    }

}
