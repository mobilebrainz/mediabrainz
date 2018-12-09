package app.mediabrainz.api.externalResources.wiki;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.core.WebService;
import app.mediabrainz.api.core.WebServiceInterface;
import app.mediabrainz.api.externalResources.wiki.model.Wikipedia;

import static app.mediabrainz.api.externalResources.wiki.WikiConfig.ACTION_MOBILEVIEW;
import static app.mediabrainz.api.externalResources.wiki.WikiConfig.ACTION_WBGETENTITIES;
import static app.mediabrainz.api.externalResources.wiki.WikiConfig.FORMAT_JSON;
import static app.mediabrainz.api.externalResources.wiki.WikiConfig.PROPS_SITELINKS;
import static app.mediabrainz.api.externalResources.wiki.WikiConfig.WIKIDATA_WEB_SERVICE;
import static app.mediabrainz.api.externalResources.wiki.WikiConfig.WIKIPEDIA_WEB_SERVICE;


public class WikiService implements WikiServiceInterface {

    private static final WebServiceInterface<WikiRetrofitService> webService =
            new WebService(WikiRetrofitService.class, WIKIPEDIA_WEB_SERVICE);

    private static final WebServiceInterface<WikiRetrofitService> wikidataService =
            new WebService(WikiRetrofitService.class, WIKIDATA_WEB_SERVICE);

    public WikiService() {
    }

    @Override
    public Flowable<Result<Wikipedia>> getWikiMobileview(String pageName) {
        return getWikiMobileview(0, pageName, "text");
    }

    @Override
    public Flowable<Result<Wikipedia>> getWikiMobileview(int section, String pageName, String prop) {
        Map<String, String> params = new HashMap<>();
        params.put("format", FORMAT_JSON);
        params.put("action", ACTION_MOBILEVIEW);
        params.put("sections", section + "");
        params.put("page", pageName);
        if (prop != null && !prop.equals("")) {
            params.put("prop", prop);
        }
        return webService.getJsonRetrofitService().getWikiMobileview(params);
    }

    /**
     * @param q
     * @param langs language keys
     * @return Map<String   languageKey   ,   String   url>
     */
    @Override
    public Flowable<Result<Map<String, String>>> getWikiSitelinks(String q, String... langs) {
        return wikidataService.getRetrofitService().getWikiEntityData(q + "." + FORMAT_JSON)
                .flatMap((Function<Result<ResponseBody>, Flowable<Result<Map<String, String>>>>) result -> {
                    if (!result.isError()) {
                        Map<String, String> wikiSitelinkMap = new HashMap<>();

                        String response = result.response().body().string();
                        try {
                            JSONObject mainObject = new JSONObject(response);
                            JSONObject entities = mainObject.getJSONObject("entities");
                            JSONObject qObj = entities.getJSONObject(q);
                            JSONObject sitelinks = qObj.getJSONObject("sitelinks");

                            boolean isEn = false;
                            for (String lang : langs) {
                                String wikiKey = lang + "wiki";
                                if (lang.equals("en")) {
                                    isEn = true;
                                }
                                if (sitelinks.has(wikiKey)) {
                                    wikiSitelinkMap.put(lang, sitelinks.getJSONObject(wikiKey).getString("url"));
                                }
                            }
                            if (!isEn) {
                                if (sitelinks.has("enwiki")) {
                                    wikiSitelinkMap.put("en", sitelinks.getJSONObject("enwiki").getString("url"));
                                }
                            }
                        } catch (JSONException e) {
                        }
                        return Flowable.just(Result.response(Response.success(wikiSitelinkMap)));
                    }
                    return Flowable.error(result.error());
                });
    }

    /*
        Возвращает заглавие ссылки сайта (последний элемент пути).
        Надо дополнительно создавать ссылку путём конкатенации sitefilter, wikipedia-пути и полученного заглавия.
     */
    @Override
    public Flowable<Result<String>> getWikiSiteTitle(String q, String lang) {
        String wikiKey = lang + "wiki";
        Map<String, String> params = new HashMap<>();
        params.put("format", FORMAT_JSON);
        params.put("action", ACTION_WBGETENTITIES);
        params.put("props", PROPS_SITELINKS);
        params.put("ids", q);
        params.put("sitefilter", wikiKey);

        return wikidataService.getRetrofitService().getWikiSitelinks(params)
                .flatMap((Function<Result<ResponseBody>, Flowable<Result<String>>>) result -> {
                    if (!result.isError()) {
                        String title = null;

                        String response = result.response().body().string();
                        try {
                            JSONObject mainObject = new JSONObject(response);
                            JSONObject entities = mainObject.getJSONObject("entities");
                            JSONObject qObj = entities.getJSONObject(q);
                            JSONObject sitelinks = qObj.getJSONObject("sitelinks");
                            JSONObject sitelink = sitelinks.getJSONObject(wikiKey);
                            title = sitelink.getString("title");

                        } catch (JSONException e) {
                        }
                        return Flowable.just(Result.response(Response.success(title)));
                    }
                    return Flowable.error(result.error());
                });
    }

}
