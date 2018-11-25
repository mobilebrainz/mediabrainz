package app.mediabrainz.api.lookup;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.Label;
import app.mediabrainz.api.model.Release;
import app.mediabrainz.api.model.ReleaseGroup;

import static app.mediabrainz.api.lookup.IncType.*;


public class LabelLookupService extends BaseLookupService<Label, LabelLookupService.LabelIncType> {

    public LabelLookupService(String mbid) {
        super(mbid);
    }

    @Override
    public Flowable<Result<Label>> lookup() {
        return getJsonRetrofitService().lookupLabel(getMbid(), getParams());
    }

    public LabelLookupService addReleaseGroupType(ReleaseGroup.AlbumType type) {
        addParam(LookupParamType.TYPE, type.toString().toLowerCase());
        return this;
    }

    public LabelLookupService addReleaseStatus(Release.Status status) {
        addParam(LookupParamType.STATUS, status.toString());
        return this;
    }

    public enum LabelIncType implements LookupServiceInterface.IncTypeInterface {
        RELEASES(RELEASES_INC),

        ALIASES(ALIASES_INC),
        ANNOTATION(ANNOTATION_INC),
        TAGS(TAGS_INC),
        RATINGS(RATINGS_INC),
        USER_TAGS(USER_TAGS_INC),         //require authorization
        USER_RATINGS(USER_RATINGS_INC);   //require authorization

        private final String inc;
        LabelIncType(String inc) {
            this.inc = inc;
        }
        @Override
        public String toString() {
            return inc;
        }
    }

}
