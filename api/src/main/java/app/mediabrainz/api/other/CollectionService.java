package app.mediabrainz.api.other;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.Config;
import app.mediabrainz.api.core.ApiUtils;
import app.mediabrainz.api.core.BaseWebService;
import app.mediabrainz.api.model.xml.Metadata;


public class CollectionService extends BaseWebService implements CollectionServiceInterface {

    public static final String CLIENT_QUERY = "client";
    public static final String ACCESS_TOKEN_QUERY = "access_token";

    private Map<String, String> map = new HashMap<>();

    public CollectionService(String client) {
        map.put(CLIENT_QUERY, client);

        if (Config.accessToken != null) {
            digestAuth = false;
            map.put(ACCESS_TOKEN_QUERY, Config.accessToken);
        } else {
            digestAuth = true;
        }
    }

    @Override
    public Flowable<Result<Metadata>> putCollection(String collId, CollectionServiceInterface.CollectionType collType, String... ids) {
        return getXmlRetrofitService().putCollection(
                collId,
                collType.getType(),
                ApiUtils.getStringFromArray(ids, ";"),
                map);
    }

    @Override
    public Flowable<Result<Metadata>> deleteCollection(String collId, CollectionServiceInterface.CollectionType collType, String... ids) {
        return getXmlRetrofitService().deleteCollection(
                collId,
                collType.getType(),
                ApiUtils.getStringFromArray(ids, ";"),
                map);
    }

    public static CollectionType getCollectionType(String collectionEntityType) {
        for (CollectionType collectionType : CollectionType.values()) {
            if (collectionType.getEntityType().equalsIgnoreCase(collectionEntityType)) {
                return collectionType;
            }
        }
        return null;
    }

}
