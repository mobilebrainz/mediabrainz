package app.mediabrainz.api.lookup;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.ISRC;
import app.mediabrainz.api.model.Release;
import app.mediabrainz.api.model.ReleaseGroup;


public class ISRCLookupService extends BaseLookupService<ISRC, RecordingLookupService.RecordingIncType> {

    public ISRCLookupService(String mbid) {
        super(mbid);
    }

    @Override
    public Flowable<Result<ISRC>> lookup() {
        return getJsonRetrofitService().lookupISRC(getMbid(), getParams());
    }

    public ISRCLookupService addReleaseGroupType(ReleaseGroup.AlbumType type) {
        addParam(LookupParamType.TYPE, type.toString().toLowerCase());
        return this;
    }

    public ISRCLookupService addReleaseStatus(Release.Status status) {
        addParam(LookupParamType.STATUS, status.toString());
        return this;
    }

}
