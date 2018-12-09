package app.mediabrainz.api.externalResources.rateyourmusic;

public class RateyourmusicResponse {

    private String avgRating = "";
    private String numRating = "";

    public RateyourmusicResponse() {
    }

    public String getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(String avgRating) {
        this.avgRating = avgRating;
    }

    public String getNumRating() {
        return numRating;
    }

    public void setNumRating(String numRating) {
        this.numRating = numRating;
    }
}
