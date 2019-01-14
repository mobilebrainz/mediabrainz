package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.List;

import app.mediabrainz.R;
import app.mediabrainz.api.model.Collection;
import app.mediabrainz.api.site.SiteService;
import app.mediabrainz.communicator.GetCollectionCommunicator;
import app.mediabrainz.communicator.OnEditCollectionCommunicator;
import app.mediabrainz.communicator.ShowTitleCommunicator;

import static app.mediabrainz.MediaBrainzApp.api;


public class CollectionEditFragment extends Fragment {

    public static final String TAG = "CollectionEditFragment";

    boolean isLoading;
    boolean isError;
    private Collection collection;

    private View errorView;
    private View progressView;
    private View contentView;
    private EditText collectionNameView;
    private EditText collectionDescriptionView;
    private CheckBox collectionPublicCheckBox;

    public static CollectionEditFragment newInstance() {
        Bundle args = new Bundle();
        CollectionEditFragment fragment = new CollectionEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_collection_edit, container, false);

        contentView = layout.findViewById(R.id.contentView);
        errorView = layout.findViewById(R.id.errorView);
        progressView = layout.findViewById(R.id.progressView);
        collectionNameView = layout.findViewById(R.id.collectionNameView);
        collectionDescriptionView = layout.findViewById(R.id.collectionDescriptionView);
        collectionPublicCheckBox = layout.findViewById(R.id.collectionPublicCheckBox);

        Button collectionEditButton = layout.findViewById(R.id.collectionEditButton);
        collectionEditButton.setOnClickListener(v -> edit());

        collection = ((GetCollectionCommunicator) getContext()).getCollection();
        if (collection != null) {
            collectionNameView.setText(collection.getName());
            collectionNameView.setError(null);
        }

        ((ShowTitleCommunicator) getContext()).getToolbarTopTitleView().setText(R.string.title_edit_collection);
        return layout;
    }

    private void edit() {
        collectionNameView.setError(null);

        String name = collectionNameView.getText().toString().trim();
        if (!TextUtils.isEmpty(name)) {
            viewProgressLoading(true);
            //TODO: make .browse(n, m)
            api.getCollections(
                    collectionBrowse -> {
                        viewProgressLoading(false);
                        boolean existName = false;
                        if (collectionBrowse.getCount() > 0) {
                            List<Collection> collections = collectionBrowse.getCollections();
                            for (Collection coll : collections) {
                                if (coll.getName().equalsIgnoreCase(name) && coll.getType().equals(collection.getType())) {
                                    existName = true;
                                    collectionNameView.setError(getString(R.string.collection_create_exist_name));
                                    break;
                                }
                            }
                        }
                        if (!existName) {
                            ((OnEditCollectionCommunicator) getContext()).onEditCollection(
                                    name,
                                    SiteService.getCollectionTypeFromSpinner(collection.getType()),
                                    collectionDescriptionView.getText().toString(),
                                    collectionPublicCheckBox.isChecked() ? 1 : 0);
                        }
                    },
                    this::showConnectionWarning,
                    100, 0);
        }
    }

    private void showConnectionWarning(Throwable t) {
        //ShowUtil.showError(getContext(), t);
        viewProgressLoading(false);
        viewError(true);
        errorView.findViewById(R.id.retryButton).setOnClickListener(v -> edit());
    }

    private void viewProgressLoading(boolean isView) {
        if (isView) {
            isLoading = true;
            contentView.setAlpha(0.3F);
            progressView.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            contentView.setAlpha(1.0F);
            progressView.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean isView) {
        if (isView) {
            isError = true;
            contentView.setVisibility(View.INVISIBLE);
            errorView.setVisibility(View.VISIBLE);
        } else {
            isError = false;
            errorView.setVisibility(View.GONE);
            contentView.setVisibility(View.VISIBLE);
        }
    }

}
