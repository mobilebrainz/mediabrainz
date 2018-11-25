package app.mediabrainz.adapter.recycler;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.mediabrainz.R;
import app.mediabrainz.api.core.ApiUtils;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.Recording;
import app.mediabrainz.api.model.Release;

import java.util.List;


public class TrackSearchAdapter extends BaseRecyclerViewAdapter<TrackSearchAdapter.TrackSearchViewHolder> {

    private List<Recording> recordings;

    public static class TrackSearchViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_search_track;

        private TextView artistName;
        private TextView albumName;
        private TextView trackName;
        private TextView tags;

        public static TrackSearchViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new TrackSearchViewHolder(view);
        }

        private TrackSearchViewHolder(View v) {
            super(v);
            artistName = v.findViewById(R.id.artist_name);
            albumName = v.findViewById(R.id.album_name);
            trackName = v.findViewById(R.id.track_name);
            tags = v.findViewById(R.id.tags);
        }

        public void bindTo(Recording recording) {
            trackName.setText(recording.getTitle());

            List<Artist.ArtistCredit> artists = recording.getArtistCredits();
            Artist artist = null;
            if (artists != null && !artists.isEmpty()) {
                artist = artists.get(0).getArtist();
                artistName.setText(itemView.getResources().getString(R.string.search_track_artist_name, artist.getName()));
                if (recording.getTags() != null && !recording.getTags().isEmpty()) {
                    tags.setText(ApiUtils.getStringFromList(recording.getTags(), ", "));
                } else {
                    tags.setText(artist.getDisambiguation());
                }
            }

            List<Release> releases = recording.getReleases();
            if (releases != null && !releases.isEmpty()) {
                albumName.setText(itemView.getResources().getString(R.string.search_track_album_name, releases.get(0).getTitle()));
            }
        }
    }

    public TrackSearchAdapter(List<Recording> recordings) {
        this.recordings = recordings;
    }

    @Override
    public void onBind(TrackSearchViewHolder holder, final int position) {
        holder.bindTo(recordings.get(position));
    }

    @Override
    public int getItemCount() {
        return recordings.size();
    }

    @NonNull
    @Override
    public TrackSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return TrackSearchViewHolder.create(parent);
    }
}
