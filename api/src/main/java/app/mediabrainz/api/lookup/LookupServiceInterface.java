package app.mediabrainz.api.lookup;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.BaseLookupEntity;
import app.mediabrainz.api.browse.BrowseServiceInterface;


public interface LookupServiceInterface
        <R extends BaseLookupEntity, P extends Enum<P> & LookupServiceInterface.IncTypeInterface> {

    LookupServiceInterface<R, P> addIncs(P... incTypes);
    LookupServiceInterface<R, P> addRels(RelsType... relTypes);
    Flowable<Result<R>> lookup();

    interface IncTypeInterface {
    }

    enum LookupParamType {
        ACCESS_TOKEN("access_token"),
        FORMAT("fmt"),
        INC("inc"),

        /**
         * primary or secondary type for release-group
         * only for inc=releases or inc=release-groups
         * https://musicbrainz.org/ws/2/artist/79491354-3d83-40e3-9d8e-7592d58d790a?fmt=json&inc=release-groups&type=album
         */
        TYPE("type"),

        /**
         * only for inc=releases
         */
        STATUS("status"),

        /**
         * only for discid : /ws/2/discid/I5l9cCSFccLKFEKS.7wqSZAorPU-?toc=1+12+267257+150+22767+41887+58317+72102+91375+104652+115380+132165+143932+159870+174597
         */
        TOC("toc"),

        /**
         * only for discid : /ws/2/discid/I5l9cCSFccLKFEKS.7wqSZAorPU-?media-format=all
         */
        MEDIA_FORMAT("media-format");

        private final String param;
        LookupParamType(String param) {
            this.param = param;
        }
        @Override
        public String toString() {
            return param;
        }
    }

    enum RelsType implements LookupServiceInterface.IncTypeInterface, BrowseServiceInterface.IncTypeInterface {
        AREA_RELS("area-rels"),
        ARTIST_RELS("artist-rels"),
        EVENT_RELS("event-rels"),
        INSTRUMENT_RELS("instrument-rels"),
        LABEL_RELS("label-rels"),
        PLACE_RELS("place-rels"),
        RECORDING_RELS("recording-rels"),
        RELEASE_RELS("release-rels"),
        RELEASE_GROUP_RELS("release-group-rels"),
        SERIES_RELS("series-rels"),
        URL_RELS("url-rels"),
        WORK_RELS("work-rels");

        private final String param;
        RelsType(String param) {
            this.param = param;
        }
        @Override
        public String toString() {
            return param;
        }
    }

    enum EmptyIncType implements IncTypeInterface {
    }

}
