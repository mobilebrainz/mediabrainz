package app.mediabrainz.api.browse;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.lookup.LookupServiceInterface;


public interface BrowseServiceInterface
        <R, P extends Enum<P> & BrowseServiceInterface.IncTypeInterface> {

    Flowable<Result<R>> browse();
    Flowable<Result<R>> browse(int limit, int offset);

    BrowseServiceInterface<R, P> addIncs(P... incTypes);
    BrowseServiceInterface<R, P> addRels(LookupServiceInterface.RelsType... relTypes);

    interface IncTypeInterface {
    }

    enum EmptyIncType implements IncTypeInterface {
    }

    enum BrowseParamType {
        ACCESS_TOKEN("access_token"),
        FORMAT("fmt"),
        LIMIT("limit"),
        OFFSET("offset"),

        //TODO: remove?
        INC("inc"),

        /**
         * only for inc=releases or inc=release-groups
         * https://musicbrainz.org/ws/2/artist/79491354-3d83-40e3-9d8e-7592d58d790a?fmt=json&inc=release-groups&type=album
         */
        TYPE("type"),

        /**
         * only for inc=releases
         */
        STATUS("status");

        private final String param;
        BrowseParamType(String param) {
            this.param = param;
        }
        @Override
        public String toString() {
            return param;
        }
    }

}
