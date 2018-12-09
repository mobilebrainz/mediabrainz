package app.mediabrainz.api.externalResources.progarchives;

public class ProgarchivesResponse {

    private String avgRating = "";
    private String numRating = "";

    public ProgarchivesResponse() {
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
