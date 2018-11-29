package app.mediabrainz.api.externalResources.lyrics.model;

import com.squareup.moshi.Json;


public class LyricsResult {

    public static final String ERROR_EXCEPTION_OBJECT = "exception";
    public static final String ERROR_DETAILS = "details";
    public static final String ERROR_LYRICS_NOT_FOUND = "Song not found";
    public static final String LYRICS_INSTRUMENTAL = "Instrumental";

    @Json(name = "result")
    private Result result;

    public LyricsResult() {
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public static class Result {

        @Json(name = "name")
        private String name;

        @Json(name = "articleUrl")
        private String articleUrl;

        @Json(name = "lyrics")
        private String lyrics;

        public Result() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getArticleUrl() {
            return articleUrl;
        }

        public void setArticleUrl(String articleUrl) {
            this.articleUrl = articleUrl;
        }

        public String getLyrics() {
            return lyrics;
        }

        public void setLyrics(String lyrics) {
            this.lyrics = lyrics;
        }
    }

}
