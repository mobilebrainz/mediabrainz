package app.mediabrainz.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import app.mediabrainz.R;


public class ImageActivity extends AppCompatActivity {

    public static final String IMAGE_URL = "image_url";

    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        final PhotoView photoView = findViewById(R.id.iv_photo);

        if (savedInstanceState != null) {
            imageUrl = savedInstanceState.getString(IMAGE_URL);
        } else {
            imageUrl = getIntent().getStringExtra(IMAGE_URL);
        }

        ProgressBar loading = findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);
        Picasso.get().load(imageUrl).into(photoView, new Callback() {
            @Override
            public void onSuccess() {
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                loading.setVisibility(View.GONE);
                Toast.makeText(ImageActivity.this, getString(R.string.error_image_loading), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(IMAGE_URL, imageUrl);
    }
}
