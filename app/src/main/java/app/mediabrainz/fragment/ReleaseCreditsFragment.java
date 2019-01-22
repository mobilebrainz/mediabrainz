package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.CreditsAdapter;
import app.mediabrainz.api.model.RelationExtractor;
import app.mediabrainz.api.model.Release;
import app.mediabrainz.api.model.relations.Relation;
import app.mediabrainz.communicator.GetReleaseCommunicator;
import app.mediabrainz.communicator.OnArtistCommunicator;


public class ReleaseCreditsFragment extends BaseFragment {

    private List<Relation> artistRelations;

    private RecyclerView recyclerView;
    private View noresultsView;

    public static ReleaseCreditsFragment newInstance() {
        Bundle args = new Bundle();
        ReleaseCreditsFragment fragment = new ReleaseCreditsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflate(R.layout.fragment_recycler_view, container);

        recyclerView = layout.findViewById(R.id.recyclerView);
        noresultsView = layout.findViewById(R.id.noresultsView);

        configReleaseRecycler();
        load();
        return layout;
    }

    public void load() {
        Release release = ((GetReleaseCommunicator) getContext()).getRelease();

        if (release != null) {
            artistRelations = new RelationExtractor(release).getArtistRelations();

            if (!artistRelations.isEmpty()) {
                Comparator<Relation> sortDate = (r1, r2) -> (r1.getType()).compareTo(r2.getType());
                Collections.sort(artistRelations, sortDate);
                CreditsAdapter adapter = new CreditsAdapter(artistRelations);
                recyclerView.setAdapter(adapter);
                adapter.setHolderClickListener(position ->
                        ((OnArtistCommunicator) getContext()).onArtist(artistRelations.get(position).getArtist().getId()));
            } else {
                noresultsView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void configReleaseRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemViewCacheSize(50);
        recyclerView.setHasFixedSize(true);
    }

}
