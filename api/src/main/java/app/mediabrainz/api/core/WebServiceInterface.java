package app.mediabrainz.api.core;


public interface WebServiceInterface<T> {

    T getRetrofitService();

    T getJsonRetrofitService();

    T getDigestAuthJsonRetrofitService();

    T getXmlRetrofitService();

    T getDigestAuthXmlRetrofitService();

}
