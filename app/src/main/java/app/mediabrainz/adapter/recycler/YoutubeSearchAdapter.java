package app.mediabrainz.adapter.recycler;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import app.mediabrainz.R;
import app.mediabrainz.api.externalResources.youtube.model.YoutubeSearchResult;
import app.mediabrainz.api.externalResources.youtube.model.YoutubeThumbnail;


public class YoutubeSearchAdapter extends BaseRecyclerViewAdapter<YoutubeSearchAdapter.YoutubeSearchViewHolder> {

    private List<YoutubeSearchResult> youtubeSearchResults;

    public static class YoutubeSearchViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_search_youtube;

        private ImageView imageView;
        private ProgressBar progressLoading;
        private TextView titleView;
        private TextView descriptionView;

        public static YoutubeSearchViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new YoutubeSearchViewHolder(view);
        }

        private YoutubeSearchViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.image);
            progressLoading = v.findViewById(R.id.image_loading);
            titleView = v.findViewById(R.id.title);
            descriptionView = v.findViewById(R.id.description);
        }

        public void bindTo(YoutubeSearchResult youtubeSearchResult) {
            YoutubeSearchResult.YoutubeSnippet youtubeSnippet = youtubeSearchResult.getSnippet();
            titleView.setText(youtubeSnippet.getTitle());
            descriptionView.setText(youtubeSnippet.getDescription());
            loadImage(youtubeSnippet.getThumbnails());
        }

        private void loadImage(YoutubeThumbnail.YoutubeThumbnails thumbnails) {
            if (thumbnails != null && thumbnails.getDefaultThumbnail() != null) {
                showImageProgressLoading(true);
                Picasso.get().load(thumbnails.getDefaultThumbnail().getUrl()).fit()
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                showImageProgressLoading(false);
                            }

                            @Override
                            public void onError(Exception e) {
                                showImageProgressLoading(false);
                            }
                        });
            }
        }

        private void showImageProgressLoading(boolean show) {
            if (show) {
                imageView.setVisibility(View.INVISIBLE);
                progressLoading.setVisibility(View.VISIBLE);
            } else {
                progressLoading.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
            }
        }
    }

    public YoutubeSearchAdapter(List<YoutubeSearchResult> youtubeSearchResults) {
        this.youtubeSearchResults = youtubeSearchResults;
    }

    @Override
    public void onBind(YoutubeSearchViewHolder holder, final int position) {
        holder.bindTo(youtubeSearchResults.get(position));
    }

    @Override
    public int getItemCount() {
        return youtubeSearchResults.size();
    }

    @NonNull
    @Override
    public YoutubeSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return YoutubeSearchViewHolder.create(parent);
    }
}
