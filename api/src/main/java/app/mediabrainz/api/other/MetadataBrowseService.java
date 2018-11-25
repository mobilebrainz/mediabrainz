package app.mediabrainz.api.other;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.Config;
import app.mediabrainz.api.model.xml.Metadata;
import app.mediabrainz.api.core.BaseWebService;


public class MetadataBrowseService extends BaseWebService implements MetadataBrowseServiceInterface {

    public static final String RATING_PATH = "rating";
    public static final String TAG_PATH = "tag";
    public static final String ID_QUERY = "id";
    public static final String ENTITY_QUERY = "entity";
    public static final String ACCESS_TOKEN_QUERY = "access_token";

    private Map<String, String> map = new HashMap<>();

    public MetadataBrowseService(EntityType entityType, String id) {
        map.put(ENTITY_QUERY , entityType.toString());
        map.put(ID_QUERY, id);

        if (Config.accessToken != null) {
            digestAuth = false;
            map.put(ACCESS_TOKEN_QUERY, Config.accessToken);
        } else {
            digestAuth = true;
        }
    }

    @Override
    public Flowable<Result<Metadata>> browseRating() {
        return getXmlRetrofitService().browseMetadata(RATING_PATH, map);
    }

    @Override
    public Flowable<Result<Metadata>> browseTag() {
        return getXmlRetrofitService().browseMetadata(TAG_PATH, map);
    }

    public enum EntityType {
        ARTIST("artist"),
        EVENT("event"),
        RELEASE_GROUP("release-group"),
        RECORDING("recording"),
        WORK("work");

        private final String inc;
        EntityType(String inc) {
            this.inc = inc;
        }
        @Override
        public String toString() {
            return inc;
        }
    }

}
