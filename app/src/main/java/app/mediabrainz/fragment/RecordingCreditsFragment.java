package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import app.mediabrainz.api.model.Recording;
import app.mediabrainz.api.model.RelationExtractor;
import app.mediabrainz.api.model.Work;
import app.mediabrainz.api.model.relations.Relation;
import app.mediabrainz.communicator.GetRecordingCommunicator;
import app.mediabrainz.communicator.GetWorkCommunicator;
import app.mediabrainz.communicator.OnArtistCommunicator;


public class RecordingCreditsFragment extends Fragment {

    private List<Relation> artistRelations;

    private RecyclerView recycler;
    private View noresults;

    public static RecordingCreditsFragment newInstance() {
        Bundle args = new Bundle();
        RecordingCreditsFragment fragment = new RecordingCreditsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_credits, container, false);

        recycler = layout.findViewById(R.id.credits_recycler);
        noresults = layout.findViewById(R.id.noresults);

        configReleaseRecycler();
        load();
        return layout;
    }

    public void load() {
        Recording recording = ((GetRecordingCommunicator) getContext()).getRecording();
        Work work = ((GetWorkCommunicator) getContext()).getWork();
        if (recording != null && work != null) {
            artistRelations = new RelationExtractor(work).getArtistRelations();
            Comparator<Relation> sortDate = (r1, r2) -> (r1.getType()).compareTo(r2.getType());
            Collections.sort(artistRelations, sortDate);

            List<Relation> artistRels = new RelationExtractor(recording).getArtistRelations();
            Collections.sort(artistRels, sortDate);
            artistRelations.addAll(artistRels);

            if (!artistRelations.isEmpty()) {
                CreditsAdapter adapter = new CreditsAdapter(artistRelations);
                recycler.setAdapter(adapter);
                adapter.setHolderClickListener(position ->
                        ((OnArtistCommunicator) getContext()).onArtist(artistRelations.get(position).getArtist().getId()));
            } else {
                noresults.setVisibility(View.VISIBLE);
            }
        }
    }

    private void configReleaseRecycler() {
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setItemViewCacheSize(25);
        recycler.setHasFixedSize(true);
    }

}
