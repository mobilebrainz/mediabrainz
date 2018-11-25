package app.mediabrainz.api.lookup;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.Disc;
import app.mediabrainz.api.model.ReleaseGroup;

/**
 *
    new DiscidLookupService("I5l9cCSFccLKFEKS.7wqSZAorPU-")
         .addToc("1+12+267257+150+22767+41887+58317+72102+91375+104652+115380+132165+143932+159870+174597")
        //.addRels(URL_RELS, ARTIST_RELS)
        .lookup();

    Passing "-" (or any invalid placeholder) as the discid will cause it to be ignored if a valid toc is present:
    new DiscidLookupService("-")
         .addToc("1+12+267257+150+22767+41887+58317+72102+91375+104652+115380+132165+143932+159870+174597")
        //By default, fuzzy toc searches only return mediums whose format is set to "CD."
        //If you want to search all mediums regardless of format, add 'media-format=all'
         .addMediaFormat("all")
         .lookup();
 */

public class DiscidLookupService extends BaseLookupService<Disc, ReleaseLookupService.ReleaseIncType> {

    public DiscidLookupService(String mbid) {
        super(mbid);
    }

    @Override
    public Flowable<Result<Disc>> lookup() {
        return getJsonRetrofitService().lookupDisc(getMbid(), getParams());
    }

    public DiscidLookupService addReleaseGroupType(ReleaseGroup.AlbumType type) {
        addParam(LookupParamType.TYPE, type.toString().toLowerCase());
        return this;
    }

    public DiscidLookupService addToc(String toc) {
        addParam(LookupParamType.TOC, toc);
        return this;
    }

    public DiscidLookupService addMediaFormat(String format) {
        addParam(LookupParamType.MEDIA_FORMAT, format);
        return this;
    }

}
