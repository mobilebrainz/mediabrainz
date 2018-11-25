package app.mediabrainz.api.site;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.core.WebService;
import app.mediabrainz.api.core.WebServiceInterface;

import static app.mediabrainz.api.Config.WEB_SERVICE;


public class RatingService implements RatingServiceInterface {

    private static final WebServiceInterface<SiteRetrofitService> webService =
            new WebService(SiteRetrofitService.class, WEB_SERVICE);

    public RatingService() {
    }

    @Override
    public Flowable<Result<Rating.Page>> getRatings(RatingType ratingType, String username, int page) {
        return getRatings(ratingType, username, page, true);
    }

    @Override
    public Flowable<Result<Rating.Page>> getRatings(RatingType ratingType, String username, int page, boolean findPageCount) {
        return webService.getRetrofitService().getRatings(username, ratingType.toString(), page)
                .flatMap((Function<Result<ResponseBody>, Flowable<Result<Rating.Page>>>) result -> {
                    if (!result.isError()) {
                        Rating.Page rPage = new Rating.Page();
                        rPage.setCurrent(page);

                        String html = result.response().body().string();
                        Document doc = Jsoup.parse(html);

                        if (findPageCount) {
                            rPage.setCount(findPageCount(doc));
                        }

                        Elements elements = doc.select("div#page > ul > li");
                        for (Element element : elements) {
                            float rate = Float.valueOf(element.select("span.current-rating").first().text());
                            Elements tags = element.getElementsByTag("a");

                            Element tag = tags.first();
                            String name = tag.text();
                            String[] arr = tag.attr("href").split("/");
                            String mbid = arr[arr.length - 1];

                            Rating rating = new Rating(username, mbid, name, rate);

                            if (!ratingType.equals(RatingType.ARTIST)) {
                                Element artistTag = tags.last();
                                String[] artistArr = artistTag.attr("href").split("/");
                                rating.setArtistName(artistTag.text());
                                rating.setArtistMbid(artistArr[artistArr.length - 1]);
                            } else {
                                Element commentElement = element.select("span.comment > bdi").first();
                                if (commentElement != null) {
                                    rating.setArtistComment(commentElement.text());
                                }
                            }
                            rPage.getRatings().add(rating);
                        }
                        return Flowable.just(Result.response(Response.success(rPage)));
                    }
                    return Flowable.error(result.error());
                });
    }


    @Override
    public Flowable<Result<Integer>> getCountPage(RatingType ratingType, String username) {
        return webService.getRetrofitService().getRatings(username, ratingType.toString(), 1)
                .flatMap((Function<Result<ResponseBody>, Flowable<Result<Integer>>>) result -> {
                    if (!result.isError()) {
                        String html = result.response().body().string();
                        Document doc = Jsoup.parse(html);
                        return Flowable.just(Result.response(Response.success(findPageCount(doc))));
                    }
                    return Flowable.error(result.error());
                });
    }

    private int findPageCount(Document doc) {
        Element pagination = doc.select("ul.pagination").first();
        int pageCount = 1;
        if (pagination != null) {
            Elements hrefs = pagination.getElementsByTag("a");
            Element href = hrefs.get(hrefs.size() - 2);
            pageCount = Integer.valueOf(href.text());
        }
        return pageCount;
    }

}
