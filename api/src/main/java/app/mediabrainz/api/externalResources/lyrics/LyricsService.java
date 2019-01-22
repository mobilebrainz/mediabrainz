package app.mediabrainz.api.externalResources.lyrics;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.core.WebService;
import app.mediabrainz.api.core.WebServiceInterface;
import app.mediabrainz.api.externalResources.lyrics.model.LyricsResult;
import app.mediabrainz.api.externalResources.lyrics.model.LyricsApi;

import static app.mediabrainz.api.externalResources.lyrics.LyricsConfig.FOTMAT_XML;
import static app.mediabrainz.api.externalResources.lyrics.LyricsConfig.LYRICS_WIKIA_API_ACTION;
import static app.mediabrainz.api.externalResources.lyrics.LyricsConfig.LYRICS_WIKIA_API_FUNC;
import static app.mediabrainz.api.externalResources.lyrics.LyricsConfig.LYRICS_WIKIA_CONTROLLER;
import static app.mediabrainz.api.externalResources.lyrics.LyricsConfig.LYRICS_WIKIA_METHOD;
import static app.mediabrainz.api.externalResources.lyrics.LyricsConfig.LYRICS_WIKIA_WEB_SERVICE;
import static app.mediabrainz.api.externalResources.lyrics.model.LyricsApi.LYRICS_INSTRUMENTAL;
import static app.mediabrainz.api.externalResources.lyrics.model.LyricsApi.LYRICS_NOT_FOUND;
import static app.mediabrainz.api.externalResources.lyrics.model.LyricsResult.ERROR_DETAILS;
import static app.mediabrainz.api.externalResources.lyrics.model.LyricsResult.ERROR_EXCEPTION_OBJECT;
import static app.mediabrainz.api.externalResources.lyrics.model.LyricsResult.ERROR_LYRICS_NOT_FOUND;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Whitelist;


public class LyricsService implements LyricsServiceInterface {

    private static final WebServiceInterface<LyricsRetrofitService> webService =
            new WebService(LyricsRetrofitService.class, LYRICS_WIKIA_WEB_SERVICE);

    /*
    Прямое получение лирики из сервиса http://lyrics.wikia.com/wikia.php. Возможно в будущем будет закрыт, тогда использовать
    парсинг сайта LyricsWikia через метод getLyricsWikiaApi(artist, song).
    Пока будет доступен этот сервис предпочтительнее его использовать, а не getLyricsWikiaApi(artist, song).
     */
    @Override
    public Flowable<Result<LyricsResult>> getLyricsWikia(@NonNull String artist, @NonNull String song) {
        Map<String, String> params = new HashMap<>();
        params.put("controller", LYRICS_WIKIA_CONTROLLER);
        params.put("method", LYRICS_WIKIA_METHOD);
        params.put("artist", artist);
        params.put("song", song);
        return webService.getJsonRetrofitService().getLyricsWikia(params);
    }

    public static boolean isNotFound(String errorContent) {
        try {
            JSONObject mainObject = new JSONObject(errorContent);
            JSONObject exceptionObj = mainObject.getJSONObject(ERROR_EXCEPTION_OBJECT);
            String details = exceptionObj.getString(ERROR_DETAILS);
            if (details.equals(ERROR_LYRICS_NOT_FOUND)) {
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            return false;
        }
    }

    @Override
    public Flowable<Result<LyricsApi>> getLyricsWikiaApi(@NonNull String artist, @NonNull String song) {
        Map<String, String> params = new HashMap<>();
        params.put("fmt", FOTMAT_XML);
        params.put("action", LYRICS_WIKIA_API_ACTION);
        params.put("func", LYRICS_WIKIA_API_FUNC);
        params.put("artist", artist);
        params.put("song", song);

        //return webService.getXmlRetrofitService().getLyricsWikiaApi(params);

        return webService.getXmlRetrofitService().getLyricsWikiaApi(params)
                .flatMap((Function<Result<LyricsApi>, Flowable<Result<LyricsApi>>>) result -> {
                    if (!result.isError()) {
                        LyricsApi lyricsApi = result.response().body();
                        if (!lyricsApi.getLyrics().equals(LYRICS_NOT_FOUND) && !lyricsApi.getLyrics().equals(LYRICS_INSTRUMENTAL)) {
                            String text;
                            try {
                                Document lyricsPage = Jsoup.connect(lyricsApi.getUrl()).get();
                                Element lyricbox = lyricsPage.select("div.lyricBox").get(0);
                                lyricbox.after(lyricbox.childNode(0));
                                lyricbox.getElementsByClass("references").remove();
                                String lyricsHtml = lyricbox.html();
                                final Document.OutputSettings outputSettings = new Document.OutputSettings().prettyPrint(false);
                                text = Jsoup.clean(lyricsHtml, "", new Whitelist().addTags("br"), outputSettings);
                                if (text.contains("&#")) {
                                    text = Parser.unescapeEntities(text, true);
                                }
                                text = text.replaceAll("\\[\\d\\]", "").trim();
                                text = text.replaceAll("<br>","");
                            } catch (IndexOutOfBoundsException | IOException e) {
                                lyricsApi.setLyrics(LYRICS_NOT_FOUND);
                                return Flowable.just(result);
                            }

                            if (text.contains("Unfortunately, we are not licensed to display the full lyrics for this song at the moment.") ||
                                    text.equals("") || text.length() < 3) {
                                lyricsApi.setLyrics(LYRICS_NOT_FOUND);
                            } else if (text.equals("Instrumental <br />")) {
                                lyricsApi.setLyrics(LYRICS_INSTRUMENTAL);
                            } else {
                                lyricsApi.setLyrics(text);
                            }
                        }
                    }
                    return Flowable.just(result);
                });
    }

}
