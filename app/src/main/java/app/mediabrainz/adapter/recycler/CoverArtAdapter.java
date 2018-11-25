package app.mediabrainz.adapter.recycler;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import app.mediabrainz.R;
import app.mediabrainz.api.coverart.CoverArtImage;

import java.util.List;


public class CoverArtAdapter extends BaseRecyclerViewAdapter<CoverArtAdapter.CoverArtViewHolder> {

    private List<CoverArtImage> coverArts;

    public static class CoverArtViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_coverart;

        private ImageView coverart;
        private ProgressBar coverartLoading;

        public static CoverArtViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new CoverArtViewHolder(view);
        }

        private CoverArtViewHolder(View v) {
            super(v);
            coverart = v.findViewById(R.id.coverart);
            coverartLoading = v.findViewById(R.id.coverart_loading);
        }

        public void bindTo(@NonNull CoverArtImage coverArtImage) {

            coverartLoading.setVisibility(View.VISIBLE);
            Picasso.get().load(coverArtImage.getThumbnails().getLarge())
                    .into(coverart, new Callback() {
                        @Override
                        public void onSuccess() {
                            coverartLoading.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            coverartLoading.setVisibility(View.GONE);
                        }
                    });

        }
    }

    public CoverArtAdapter(List<CoverArtImage> coverArts) {
        this.coverArts = coverArts;
    }

    @Override
    public void onBind(CoverArtViewHolder holder, final int position) {
        holder.bindTo(coverArts.get(position));
    }

    @Override
    public int getItemCount() {
        return coverArts.size();
    }

    @NonNull
    @Override
    public CoverArtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return CoverArtViewHolder.create(parent);
    }

}
