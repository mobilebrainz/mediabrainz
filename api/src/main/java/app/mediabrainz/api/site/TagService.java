package app.mediabrainz.api.site;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import app.mediabrainz.api.core.WebService;
import app.mediabrainz.api.core.WebServiceInterface;
import app.mediabrainz.api.model.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava2.Result;

import static app.mediabrainz.api.Config.WEB_SERVICE;


public class TagService implements TagServiceInterface {

    private static final WebServiceInterface<SiteRetrofitService> webService =
            new WebService(SiteRetrofitService.class, WEB_SERVICE);

    public TagService() {
    }

    @Override
    public Flowable<Result<Map<Tag.TagType, List<Tag>>>> getUserTags(String username) {
        return webService.getRetrofitService().getUserTags(username)
                .flatMap((Function<Result<ResponseBody>, Flowable<Result<Map<Tag.TagType, List<Tag>>>>>) result -> {
                    if (!result.isError()) {
                        String html = result.response().body().string();
                        Document doc = Jsoup.parse(html);

                        Map<Tag.TagType, List<Tag>> tagMap = new HashMap<>();
                        tagMap.put(Tag.TagType.GENRE, extractTags(doc, "ul.genre-list > li"));
                        tagMap.put(Tag.TagType.TAG, extractTags(doc, "ul.tag-list > li"));

                        return Flowable.just(Result.response(Response.success(tagMap)));
                    }
                    return Flowable.error(result.error());
                });
    }

    private List<Tag> extractTags(Document doc, String sccQuery) {
        List<Tag> tags = new ArrayList<>();
        Elements genreElements = doc.select(sccQuery);
        for (Element element : genreElements) {
            Tag tag = new Tag();
            tag.setName(element.getElementsByTag("a").first().text());
            tag.setCount(Integer.valueOf(element.select("span.tag-count").first().text()));
            tags.add(tag);
        }
        return tags;
    }

    @Override
    public Flowable<Result<Map<UserTagType, List<TagEntity>>>> getUserTagEntities(String username, String tag) {
        return webService.getRetrofitService().getUserTagEntities(username, tag)
                .flatMap((Function<Result<ResponseBody>, Flowable<Result<Map<UserTagType, List<TagEntity>>>>>) result -> {
                    if (!result.isError()) {
                        Map<UserTagType, List<TagEntity>> map = new HashMap<>();

                        String html = result.response().body().string();
                        Document doc = Jsoup.parse(html);
                        Elements headers = doc.getElementsByTag("h3");
                        for (Element header : headers) {
                            String headerText = header.text();

                            for (UserTagType userTagType : UserTagType.values()) {
                                if (headerText.equalsIgnoreCase(userTagType.toString())) {
                                    if (!map.containsKey(userTagType)) {
                                        map.put(userTagType, new ArrayList<>());
                                    }

                                    Element ul = header.nextElementSibling();
                                    if (ul != null) {
                                        Elements lis = ul.getElementsByTag("li");
                                        for (Element li : lis) {
                                            TagEntity tagEntity = new TagEntity();
                                            Elements tags = li.getElementsByTag("a");

                                            Element a = tags.first();
                                            String[] arr = a.attr("href").split("/");
                                            tagEntity.setMbid(arr[arr.length - 1]);
                                            tagEntity.setName(a.text());

                                            if (!userTagType.equals(UserTagType.ARTISTS)) {
                                                Element artistTag = tags.last();
                                                String[] artistArr = artistTag.attr("href").split("/");
                                                tagEntity.setArtistName(artistTag.text());
                                                tagEntity.setArtistMbid(artistArr[artistArr.length - 1]);
                                            } else {
                                                Element commentElement = li.select("span.comment > bdi").first();
                                                if (commentElement != null) {
                                                    tagEntity.setArtistComment(commentElement.text());
                                                }
                                            }

                                            map.get(userTagType).add(tagEntity);
                                        }
                                    }
                                }
                            }
                        }
                        return Flowable.just(Result.response(Response.success(map)));
                    }
                    return Flowable.error(result.error());
                });
    }

    @Override
    public Flowable<Result<TagEntity.Page>> getTagEntities(String tag, TagType tagType, int page) {
        return getTagEntities(tag, tagType, page, true);
    }

    @Override
    public Flowable<Result<TagEntity.Page>> getTagEntities(String tag, TagType tagType, int page, boolean findPageCount) {
        return webService.getRetrofitService().getTagEntities(tag, tagType.toString(), page)
                .flatMap((Function<Result<ResponseBody>, Flowable<Result<TagEntity.Page>>>) result -> {
                    if (!result.isError()) {
                        TagEntity.Page tagPage = new TagEntity.Page();
                        tagPage.setCurrent(page);

                        String html = result.response().body().string();
                        Document doc = Jsoup.parse(html);

                        if (findPageCount) {
                            tagPage.setCount(findPageCount(doc));
                        }

                        Elements elements = doc.select("div#content > ul > li");
                        for (Element element : elements) {
                            int voteCount = Integer.valueOf(element.text().split("-")[0].trim());
                            Elements as = element.getElementsByTag("a");
                            Element a = as.first();
                            String name = a.text();
                            String[] arr = a.attr("href").split("/");
                            String mbid = arr[arr.length - 1];

                            TagEntity tagEntity = new TagEntity(mbid, name, voteCount);
                            if (!tagType.equals(TagType.ARTIST)) {
                                Element artistTag = as.last();
                                String[] artistArr = artistTag.attr("href").split("/");
                                tagEntity.setArtistName(artistTag.text());
                                tagEntity.setArtistMbid(artistArr[artistArr.length - 1]);
                            } else {
                                Element commentElement = element.select("span.comment > bdi").first();
                                if (commentElement != null) {
                                    tagEntity.setArtistComment(commentElement.text());
                                }
                            }
                            tagPage.getTagEntities().add(tagEntity);
                        }
                        return Flowable.just(Result.response(Response.success(tagPage)));
                    }
                    return Flowable.error(result.error());
                });
    }

    @Override
    public Flowable<Result<Integer>> getCountPage(String tag, TagType tagType) {
        return webService.getRetrofitService().getTagEntities(tag, tagType.toString(), 1)
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
