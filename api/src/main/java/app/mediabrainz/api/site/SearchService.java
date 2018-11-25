package app.mediabrainz.api.site;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import app.mediabrainz.api.core.WebService;
import app.mediabrainz.api.core.WebServiceInterface;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava2.Result;

import static app.mediabrainz.api.Config.WEB_SERVICE;
import static app.mediabrainz.api.site.SearchServiceInterface.SearchType.EDITOR;


public class SearchService implements SearchServiceInterface {

    private final String METHOD = "indexed";

    private static final WebServiceInterface<SiteRetrofitService> webService =
            new WebService(SiteRetrofitService.class, WEB_SERVICE);

    public SearchService() {
    }

    @Override
    public Flowable<Result<List<String>>> searchTag(String query, int page, int limit) {
        return search(SearchType.TAG, query, page, limit);
    }

    @Override
    public Flowable<Result<List<String>>> searchUser(String query, int page, int limit) {
        return search(EDITOR, query, page, limit);
    }

    @Override
    public Flowable<Result<List<String>>> search(SearchType searchType, String query, int page, int limit) {
        return webService.getRetrofitService().search(query, searchType.getType(), page, limit, METHOD)
                .flatMap((Function<Result<ResponseBody>, Flowable<Result<List<String>>>>) result -> {
                    if (!result.isError()) {
                        String html = result.response().body().string();
                        Document doc = Jsoup.parse(html);
                        List<String> results = new ArrayList<>();

                        Elements elements = doc.select("table.tbl a");
                        for (Element element : elements) {
                            String str = element.text();
                            if (str != null && !str.equals("")) {
                                results.add(str);
                            }
                        }
                        return Flowable.just(Result.response(Response.success(results)));
                    }
                    return Flowable.error(result.error());
                });
    }

}
