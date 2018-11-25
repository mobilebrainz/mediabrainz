package app.mediabrainz.api.browse;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.Release;
import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.api.core.ApiUtils;

import static app.mediabrainz.api.lookup.IncType.*;
import static app.mediabrainz.api.browse.EntityType.*;


public class ReleaseBrowseService extends
        BaseBrowseService<Release.ReleaseBrowse, ReleaseBrowseService.ReleaseIncType, ReleaseBrowseService.ReleaseBrowseEntityType> {

    public ReleaseBrowseService(ReleaseBrowseEntityType entityType, String mbid) {
        super(entityType, mbid);
    }

    @Override
    public Flowable<Result<Release.ReleaseBrowse>> browse() {
        return getJsonRetrofitService().browseRelease(getParams());
    }

    public ReleaseBrowseService addReleaseGroupTypes(ReleaseGroup.AlbumType... types) {
        addParam(BrowseParamType.TYPE, ApiUtils.getStringFromArray(types, "|").toLowerCase());
        return this;
    }

    public ReleaseBrowseService addStatus(Release.Status status) {
        addParam(BrowseParamType.STATUS, status.toString());
        return this;
    }

    public enum ReleaseBrowseEntityType implements BaseBrowseService.BrowseEntityTypeInterface {
        AREA(AREA_ENTITY),
        ARTIST(ARTIST_ENTITY),
        COLLECTION(COLLECTION_ENTITY),
        LABEL(LABEL_ENTITY),
        RECORDING(RECORDING_ENTITY),
        RELEASE_GROUP(RELEASE_GROUP_ENTITY),
        TRACK(TRACK_ENTITY),
        TRACK_ARTIST(TRACK_ARTIST_ENTITY);

        private final String type;
        ReleaseBrowseEntityType(String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type;
        }
    }

    public enum ReleaseIncType implements BrowseServiceInterface.IncTypeInterface {
        ALIASES(ALIASES_INC),
        ANNOTATION(ANNOTATION_INC),

        ARTIST_CREDITS(ARTIST_CREDITS_INC),
        LABELS(LABELS_INC),
        RECORDINGS(RECORDINGS_INC),
        RELEASE_GROUPS(RELEASE_GROUPS_INC),
        DISCIDS(DISCIDS_INC),
        MEDIA(MEDIA_INC);

        private final String inc;
        ReleaseIncType(String inc) {
            this.inc = inc;
        }
        @Override
        public String toString() {
            return inc;
        }
    }

}
