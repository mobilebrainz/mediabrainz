package app.mediabrainz.api.browse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.Config;
import app.mediabrainz.api.core.BaseWebService;
import app.mediabrainz.api.core.ApiUtils;

import static app.mediabrainz.api.Config.AUTHORIZATED_INCS;
import static app.mediabrainz.api.Config.FORMAT_JSON;
import static app.mediabrainz.api.browse.BrowseServiceInterface.BrowseParamType.ACCESS_TOKEN;
import static app.mediabrainz.api.browse.BrowseServiceInterface.BrowseParamType.FORMAT;
import static app.mediabrainz.api.browse.BrowseServiceInterface.BrowseParamType.INC;
import static app.mediabrainz.api.browse.BrowseServiceInterface.IncTypeInterface;
import static app.mediabrainz.api.lookup.LookupServiceInterface.RelsType;


public abstract class BaseBrowseService
        <R, P1 extends Enum<P1> & IncTypeInterface, P2 extends Enum<P2> & BaseBrowseService.BrowseEntityTypeInterface>
        extends BaseWebService
        implements BrowseServiceInterface<R, P1> {

    public interface BrowseEntityTypeInterface {
    }

    private final P2 entityType;
    private final String mbid;
    private final List<IncTypeInterface> incs = new ArrayList<>();
    private final Map<BrowseParamType, String> params = new HashMap<>();

    public BaseBrowseService(P2 entityType, String mbid) {
        super();
        this.entityType = entityType;
        this.mbid = mbid;
        params.put(FORMAT, FORMAT_JSON);
        if (Config.accessToken != null) {
            params.put(ACCESS_TOKEN, Config.accessToken);
        }
    }

    protected BrowseServiceInterface<R, P1> addParam(BrowseParamType param, String value) {
        params.put(param, value);
        return this;
    }

    @Override
    public BrowseServiceInterface<R, P1> addIncs(P1... incTypes) {
        incs.addAll(Arrays.asList(incTypes));
        if (Config.accessToken == null) {
            for (P1 incType : incTypes) {
                for (String authType : AUTHORIZATED_INCS) {
                    if (incType.toString().equals(authType)) {
                        digestAuth = true;
                    }
                }
            }
        }
        return this;
    }

    @Override
    public BrowseServiceInterface<R, P1> addRels(RelsType... relTypes) {
        incs.addAll(Arrays.asList(relTypes));
        return this;
    }

    @Override
    public Flowable<Result<R>> browse(int limit, int offset) {
        params.put(BrowseParamType.LIMIT, Integer.toString(limit));
        params.put(BrowseParamType.OFFSET, Integer.toString(offset));
        return browse();
    }

    protected Map<String, String> getParams() {
        Map<String, String> map = new HashMap<>();
        map.put(entityType.toString(), mbid);
        Iterator<BrowseParamType> iter = params.keySet().iterator();
        while(iter.hasNext()) {
            BrowseParamType key = iter.next();
            map.put(key.toString(), params.get(key));
        }
        String inc = ApiUtils.getStringFromList(incs, "+");
        incs.clear();
        if (!inc.equals("")) {
            map.put(INC.toString(), inc);
        }
        return map;
    }

}
