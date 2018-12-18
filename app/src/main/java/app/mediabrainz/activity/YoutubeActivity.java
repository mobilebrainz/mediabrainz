package app.mediabrainz.activity;

import android.os.Bundle;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayerView;

import app.mediabrainz.R;

import static app.mediabrainz.MediaBrainzApp.YOUTUBE_API_KEY;

public class YoutubeActivity extends YouTubeBaseActivity {

    public static final String VIDEO_ID = "VIDEO_ID";

    private String videoId;
    private YouTubePlayerView youtubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        youtubePlayerView = findViewById(R.id.youtubePlayerView);

        if (savedInstanceState != null) {
            videoId = savedInstanceState.getString(VIDEO_ID);
        } else {
            videoId = getIntent().getStringExtra(VIDEO_ID);
        }

        playVideo(videoId);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(VIDEO_ID, videoId);
    }

    public void playVideo(final String videoId) {
        youtubePlayerView.initialize(YOUTUBE_API_KEY,
                new OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                        youTubePlayer.cueVideo(videoId);
                        //youTubePlayer.setFullscreen(true);
                        youTubePlayer.play();
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                    }
                });
    }

}
