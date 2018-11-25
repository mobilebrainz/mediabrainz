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

    private View noresults;
    private RecyclerView linksRecycler;
    private List<Url> urls;

    public static LinksTabFragment newInstance() {
        Bundle args = new Bundle();
        LinksTabFragment fragment = new LinksTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_links_tab, container, false);

        noresults = layout.findViewById(R.id.noresults);
        linksRecycler = layout.findViewById(R.id.links_recycler);

        load();
        return layout;
    }

    public void load() {
        urls = ((GetUrlsCommunicator) getContext()).getUrls();
        if (urls != null) {
            Collections.sort(urls);
            if (urls.isEmpty()) {
                noresults.setVisibility(View.VISIBLE);
            } else {
                noresults.setVisibility(View.GONE);
                configLinksRecycler();
                LinkAdapter adapter = new LinkAdapter(urls);
                adapter.setHolderClickListener(position ->
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urls.get(position).getResource())))
                );
                linksRecycler.setAdapter(adapter);
            }
        }
    }

    private void configLinksRecycler() {
        linksRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        linksRecycler.setItemViewCacheSize(100);
        linksRecycler.setHasFixedSize(true);
    }

}
