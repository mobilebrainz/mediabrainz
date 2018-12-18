package app.mediabrainz.api.externalResources.youtube.model;


import com.squareup.moshi.Json;

import java.util.List;


public class YoutubeSearchListResponse {

    public YoutubeSearchListResponse() {
    }

    @Json(name = "kind")
    private String kind;

    //@Json(name = "etag")
    //private Etag etag;

    @Json(name = "nextPageToken")
    private String nextPageToken;

    @Json(name = "prevPageToken")
    private String prevPageToken;

    @Json(name = "regionCode")
    private String regionCode;

    @Json(name = "pageInfo")
    private YoutubePageInfo pageInfo;

    @Json(name = "items")
    private List<YoutubeSearchResult> items;

    public static class YoutubePageInfo {

        @Json(name = "totalResults")
        private int totalResults;

        @Json(name = "resultsPerPage")
        private int resultsPerPage;

        public YoutubePageInfo() {
        }

        public int getTotalResults() {
            return totalResults;
        }

        public void setTotalResults(int totalResults) {
            this.totalResults = totalResults;
        }

        public int getResultsPerPage() {
            return resultsPerPage;
        }

        public void setResultsPerPage(int resultsPerPage) {
            this.resultsPerPage = resultsPerPage;
        }
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public String getPrevPageToken() {
        return prevPageToken;
    }

    public void setPrevPageToken(String prevPageToken) {
        this.prevPageToken = prevPageToken;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public YoutubePageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(YoutubePageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public List<YoutubeSearchResult> getItems() {
        return items;
    }

    public void setItems(List<YoutubeSearchResult> items) {
        this.items = items;
    }

}
