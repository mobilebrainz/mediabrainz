package app.mediabrainz.api.externalResources.rateyourmusic;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class RateyourmusicService implements RateyourmusicServiceInterface {

    @Override
    public RateyourmusicResponse parseResponse(String response) {

        RateyourmusicResponse rateyourmusicResponse = new RateyourmusicResponse();
        Document doc = Jsoup.parse(response);

        Elements avgRatingElements = doc.select("span.avg_rating");
        if (avgRatingElements != null && !avgRatingElements.isEmpty()) {
            String avgRating = avgRatingElements.get(0).text();
            rateyourmusicResponse.setAvgRating(avgRating);
        }
        Elements numRatingElements = doc.select("span.num_ratings span");
        if (numRatingElements != null && !numRatingElements.isEmpty()) {
            String numRating = numRatingElements.get(0).text();
            rateyourmusicResponse.setNumRating(numRating);
        }

        return rateyourmusicResponse;
    }

}
