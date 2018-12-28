package app.mediabrainz.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.DialogCollectionsAdapter;
import app.mediabrainz.api.model.Collection;
import app.mediabrainz.communicator.GetCollectionsCommunicator;


public class CollectionsDialogFragment extends DialogFragment {

    public static final String TAG = "CollectionsDialogFragment";

    public interface DialogFragmentListener {
        void onCollection(String collectionMbid);

        void showCreateCollection();
    }

    private RecyclerView collectionRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setStyle(android.support.v4.app.DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.dialog_fragment_collections, container, false);
        Button createCollectionButton = layout.findViewById(R.id.createCollectionButton);
        createCollectionButton.setOnClickListener(v -> {
            ((DialogFragmentListener) getContext()).showCreateCollection();
            dismiss();
        });

        collectionRecyclerView = layout.findViewById(R.id.collectionRecyclerView);
        collectionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        collectionRecyclerView.setItemViewCacheSize(50);
        collectionRecyclerView.setHasFixedSize(true);
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        List<Collection> collections = ((GetCollectionsCommunicator) getContext()).getCollections();
        if (collections != null) {
            DialogCollectionsAdapter adapter = new DialogCollectionsAdapter(collections);
            collectionRecyclerView.setAdapter(adapter);
            adapter.setHolderClickListener(position -> {
                ((DialogFragmentListener) getContext()).onCollection(collections.get(position).getId());
                dismiss();
            });
        }
    }

}
