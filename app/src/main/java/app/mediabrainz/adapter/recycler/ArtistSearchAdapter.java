package app.mediabrainz.adapter.recycler;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.R;
import app.mediabrainz.api.core.ApiUtils;
import app.mediabrainz.api.lastfm.model.Image;
import app.mediabrainz.api.model.Artist;

import java.util.List;

import static android.text.TextUtils.TruncateAt.END;
import static app.mediabrainz.MediaBrainzApp.api;


public class ArtistSearchAdapter extends BaseRecyclerViewAdapter<ArtistSearchAdapter.ArtistSearchViewHolder> {

    private List<Artist> artists;

    public static class ArtistSearchViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_search_artist;

        private LinearLayout container;
        private ImageView artistImage;
        private ProgressBar progressLoading;
        private TextView artistName;
        private TextView type;

        public static ArtistSearchViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new ArtistSearchViewHolder(view);
        }

        private ArtistSearchViewHolder(View v) {
            super(v);
            container = v.findViewById(R.id.artist_container);
            artistImage = v.findViewById(R.id.artist_image);
            progressLoading = v.findViewById(R.id.image_loading);
            artistName = v.findViewById(R.id.artist_name);
            type = v.findViewById(R.id.artist_type);
        }

        public void bindTo(Artist artist) {
            artistName.setText(artist.getName());
            type.setText(artist.getType());

            String areastr = "";
            if (artist.getArea() != null && !TextUtils.isEmpty(artist.getArea().getName())) {
                areastr = artist.getArea().getName();
            }
            String foundedstr = "";
            if (artist.getLifeSpan() != null && !TextUtils.isEmpty(artist.getLifeSpan().getBegin())) {
                foundedstr = artist.getLifeSpan().getBegin();
                if (!TextUtils.isEmpty(artist.getLifeSpan().getEnd())) {
                    foundedstr += " - " + artist.getLifeSpan().getEnd();
                }
            }
            String separator = !areastr.equals("") && !foundedstr.equals("") ? ", " : "";
            addTextView(areastr + separator + foundedstr);
            addTextView(artist.getDisambiguation());
            if (artist.getTags() != null && !artist.getTags().isEmpty()) {
                addTextView("Tags: " + ApiUtils.getStringFromList(artist.getTags(), ", "));
            }
            if (MediaBrainzApp.getPreferences().isLoadImagesEnabled()) {
                loadArtistImageFromLastfm(artist.getName());
            } else {
                artistImage.setVisibility(View.VISIBLE);
            }
        }

        private void addTextView(String text) {
            if (!TextUtils.isEmpty(text)) {
                TextView textView = new TextView(itemView.getContext());
                textView.setText(text);
                textView.setTextSize(12);
                textView.setEllipsize(END);
                textView.setSingleLine();
                textView.setTextColor(itemView.getResources().getColor(R.color.colorPrimaryLight));
                container.addView(textView);
            }
        }

        private void loadArtistImageFromLastfm(String name) {
            showImageProgressLoading(true);
            api.getArtistFromLastfm(
                    name,
                    result -> {
                        boolean loaded = true;
                        if (result.getError() == null || result.getError() == 0) {
                            List<Image> images = result.getArtist().getImages();
                            if (images != null && !images.isEmpty()) {
                                for (Image img : images) {
                                    if (img.getSize().equals(Image.SizeType.MEDIUM.toString()) && !TextUtils.isEmpty(img.getText())) {
                                        Picasso.get().load(img.getText()).fit()
                                                .into(artistImage, new Callback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        showImageProgressLoading(false);
                                                    }

                                                    @Override
                                                    public void onError(Exception e) {
                                                        showImageProgressLoading(false);
                                                    }
                                                });
                                        loaded = false;
                                        break;
                                    }
                                }
                            }
                        }
                        if (loaded) {
                            showImageProgressLoading(false);
                        }
                    },
                    t -> showImageProgressLoading(false));
        }

        private void showImageProgressLoading(boolean show) {
            if (show) {
                artistImage.setVisibility(View.INVISIBLE);
                progressLoading.setVisibility(View.VISIBLE);
            } else {
                progressLoading.setVisibility(View.GONE);
                artistImage.setVisibility(View.VISIBLE);
            }
        }

    }

    public ArtistSearchAdapter(List<Artist> artists) {
        this.artists = artists;
    }

    @Override
    public void onBind(ArtistSearchViewHolder holder, final int position) {
        holder.bindTo(artists.get(position));
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    @NonNull
    @Override
    public ArtistSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ArtistSearchViewHolder.create(parent);
    }
}
