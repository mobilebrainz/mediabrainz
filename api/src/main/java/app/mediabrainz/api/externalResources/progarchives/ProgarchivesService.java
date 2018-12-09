package app.mediabrainz.api.externalResources.progarchives;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import app.mediabrainz.api.externalResources.rateyourmusic.RateyourmusicServiceInterface;


public class ProgarchivesService implements ProgarchivesServiceInterface {

    @Override
    public ProgarchivesResponse parseResponse(String response) {
        ProgarchivesResponse progarchivesResponse = new ProgarchivesResponse();
        Document doc = Jsoup.parse(response);

        Element avgRatingElement = doc.getElementById("avgRatings_1");
        if (avgRatingElement != null) {
            progarchivesResponse.setAvgRating(avgRatingElement.text());
        }

        Element nbRatingElement = doc.getElementById("nbRatings_1");
        if (nbRatingElement != null) {
            progarchivesResponse.setNumRating(nbRatingElement.text());
        }

        return progarchivesResponse;
    }

}
