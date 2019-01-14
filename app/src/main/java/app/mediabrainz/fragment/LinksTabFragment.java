package app.mediabrainz.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.LinkAdapter;
import app.mediabrainz.api.model.Url;
import app.mediabrainz.communicator.GetUrlsCommunicator;


public class LinksTabFragment extends Fragment {

    private View noresultsView;
    private RecyclerView recyclerView;
    private List<Url> urls;

    public static LinksTabFragment newInstance() {
        Bundle args = new Bundle();
        LinksTabFragment fragment = new LinksTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        noresultsView = layout.findViewById(R.id.noresultsView);
        recyclerView = layout.findViewById(R.id.recyclerView);

        load();
        return layout;
    }

    public void load() {
        urls = ((GetUrlsCommunicator) getContext()).getUrls();
        if (urls != null) {
            Collections.sort(urls);
            if (urls.isEmpty()) {
                noresultsView.setVisibility(View.VISIBLE);
            } else {
                noresultsView.setVisibility(View.GONE);
                configLinksRecycler();
                LinkAdapter adapter = new LinkAdapter(urls);
                adapter.setHolderClickListener(position ->
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urls.get(position).getResource())))
                );
                recyclerView.setAdapter(adapter);
            }
        }
    }

    private void configLinksRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemViewCacheSize(100);
        recyclerView.setHasFixedSize(true);
    }

}
