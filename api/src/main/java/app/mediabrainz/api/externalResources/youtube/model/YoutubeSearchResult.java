package app.mediabrainz.api.externalResources.youtube.model;


import com.squareup.moshi.Json;

/*
    {
        "kind": "youtube#searchResult",
        "etag": etag,
        "id": {
            "kind": string,
            "videoId": string,
            "channelId": string,
            "playlistId": string
        },
        "snippet": {
            "publishedAt": datetime,
            "channelId": string,
            "title": string,
            "description": string,
            "thumbnails": {
                (key): {
                    "url": string,
                    "width": unsigned integer,
                    "height": unsigned integer
                }
            },
            "channelTitle": string,
            "liveBroadcastContent": string
        }
    }
*/
public class YoutubeSearchResult {

    @Json(name = "kind")
    private String kind;

    //@Json(name = "etag")
    //private Etag etag;

    @Json(name = "id")
    private YoutubeId id;

    @Json(name = "snippet")
    private YoutubeSnippet snippet;

    public static class YoutubeId {

        @Json(name = "kind")
        private String kind;

        @Json(name = "videoId")
        private String videoId;

        @Json(name = "channelId")
        private String channelId;

        @Json(name = "playlistId")
        private String playlistId;

        public YoutubeId() {
        }

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public String getPlaylistId() {
            return playlistId;
        }

        public void setPlaylistId(String playlistId) {
            this.playlistId = playlistId;
        }
    }

    public static class YoutubeSnippet {

        //datetime
        //@Json(name = "publishedAt")
        //private String publishedAt;

        @Json(name = "channelId")
        private String channelId;

        @Json(name = "title")
        private String title;

        @Json(name = "description")
        private String description;

        @Json(name = "channelTitle")
        private String channelTitle;

        @Json(name = "liveBroadcastContent")
        private String liveBroadcastContent;

        @Json(name = "thumbnails")
        private YoutubeThumbnail.YoutubeThumbnails thumbnails;

        public YoutubeSnippet() {
        }

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getChannelTitle() {
            return channelTitle;
        }

        public void setChannelTitle(String channelTitle) {
            this.channelTitle = channelTitle;
        }

        public String getLiveBroadcastContent() {
            return liveBroadcastContent;
        }

        public void setLiveBroadcastContent(String liveBroadcastContent) {
            this.liveBroadcastContent = liveBroadcastContent;
        }

        public YoutubeThumbnail.YoutubeThumbnails getThumbnails() {
            return thumbnails;
        }

        public void setThumbnails(YoutubeThumbnail.YoutubeThumbnails thumbnails) {
            this.thumbnails = thumbnails;
        }
    }

    public YoutubeSearchResult() {
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public YoutubeId getId() {
        return id;
    }

    public void setId(YoutubeId id) {
        this.id = id;
    }

    public YoutubeSnippet getSnippet() {
        return snippet;
    }

    public void setSnippet(YoutubeSnippet snippet) {
        this.snippet = snippet;
    }
}
