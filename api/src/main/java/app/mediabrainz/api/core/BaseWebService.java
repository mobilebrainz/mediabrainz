package app.mediabrainz.api.core;

import static app.mediabrainz.api.Config.WEB_SERVICE;


public abstract class BaseWebService {

    private static final WebServiceInterface<RetrofitService> webService =
            new WebService(RetrofitService.class, WEB_SERVICE);

    protected boolean digestAuth = false;

    public RetrofitService getJsonRetrofitService() {
        return (!digestAuth) ? webService.getJsonRetrofitService()
                : webService.getDigestAuthJsonRetrofitService();
    }

    public RetrofitService getXmlRetrofitService() {
        return (!digestAuth) ? webService.getXmlRetrofitService()
                : webService.getDigestAuthXmlRetrofitService();
    }

    public boolean isDigestAuth() {
        return digestAuth;
    }

    public void setDigestAuth(boolean digestAuth) {
        this.digestAuth = digestAuth;
    }

}
