package app.mediabrainz.api.externalResources.youtube;

import java.util.HashMap;

import app.mediabrainz.api.core.WebService;
import app.mediabrainz.api.core.WebServiceInterface;
import app.mediabrainz.api.externalResources.youtube.model.YoutubeSearchListResponse;
import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;

import static app.mediabrainz.api.externalResources.youtube.YoutubeConfig.YOUTUBE_WEB_SERVICE;


public class YoutubeService implements YoutubeServiceInterface {

    private static final WebServiceInterface<YoutubeRetrofitService> webService =
            new WebService(YoutubeRetrofitService.class, YOUTUBE_WEB_SERVICE);


    public YoutubeService() {
    }

    @Override
    public Flowable<Result<YoutubeSearchListResponse>> search(String keyword, String youtubeApiKey) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("key", youtubeApiKey);
        parameters.put("part", "snippet");
        parameters.put("maxResults", "25");
        parameters.put("q", keyword);
        parameters.put("type", "video");

        return webService.getJsonRetrofitService().search(parameters);
    }
}
