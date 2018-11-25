package app.mediabrainz.api.browse;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.Url;

import static app.mediabrainz.api.browse.EntityType.RESOURCE_ENTITY;


public class UrlBrowseService extends
        BaseBrowseService<Url, BrowseServiceInterface.EmptyIncType, UrlBrowseService.UrlBrowseEntityType> {

    public UrlBrowseService(UrlBrowseEntityType entityType, String mbid) {
        super(entityType, mbid);
    }

    @Override
    public Flowable<Result<Url>> browse() {
        return getJsonRetrofitService().browseUrl(getParams());
    }

    public enum UrlBrowseEntityType implements BaseBrowseService.BrowseEntityTypeInterface {
        RESOURCE(RESOURCE_ENTITY);

        private final String type;
        UrlBrowseEntityType(String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type;
        }
    }

}
