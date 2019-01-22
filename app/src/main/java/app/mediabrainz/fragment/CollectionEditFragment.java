package app.mediabrainz.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import app.mediabrainz.R;
import app.mediabrainz.api.model.Collection;
import app.mediabrainz.api.site.SiteService;
import app.mediabrainz.communicator.GetCollectionCommunicator;
import app.mediabrainz.communicator.ShowTitleCommunicator;
import app.mediabrainz.util.ShowUtil;
import app.mediabrainz.viewModels.CollectionCreateEditVM;
import app.mediabrainz.viewModels.UserCollectionsSharedVM;

import static app.mediabrainz.MediaBrainzApp.oauth;


public class CollectionEditFragment extends BaseFragment {

    public static final String TAG = "CollectionEditFragment";

    String name;
    boolean isLoading;
    boolean isError;
    private Collection collection;
    private UserCollectionsSharedVM userCollectionsSharedVM;
    private CollectionCreateEditVM collectionCreateEditVM;

    private View errorView;
    private View progressView;

    private View contentView;
    private EditText collectionNameView;
    private EditText collectionDescriptionView;
    private CheckBox collectionPublicCheckBox;
    private Button collectionEditButton;

    public static CollectionEditFragment newInstance() {
        Bundle args = new Bundle();
        CollectionEditFragment fragment = new CollectionEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflate(R.layout.fragment_collection_edit, container);

        contentView = layout.findViewById(R.id.contentView);
        errorView = layout.findViewById(R.id.errorView);
        progressView = layout.findViewById(R.id.progressView);
        collectionNameView = layout.findViewById(R.id.collectionNameView);
        collectionDescriptionView = layout.findViewById(R.id.collectionDescriptionView);
        collectionPublicCheckBox = layout.findViewById(R.id.collectionPublicCheckBox);
        collectionEditButton = layout.findViewById(R.id.collectionEditButton);

        if (getContext() instanceof ShowTitleCommunicator) {
            ((ShowTitleCommunicator) getContext()).getToolbarTopTitleView().setText(R.string.title_edit_collection);
        }
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getContext() instanceof GetCollectionCommunicator &&
                (collection = ((GetCollectionCommunicator) getContext()).getCollection()) != null) {

            collectionNameView.setText(collection.getName());
            collectionNameView.setError(null);

            if (getActivity() != null) {
                userCollectionsSharedVM = ViewModelProviders
                        .of(getActivity(), new UserCollectionsSharedVM.Factory(oauth.getName()))
                        .get(UserCollectionsSharedVM.class);

                collectionCreateEditVM = ViewModelProviders
                        .of(this)
                        .get(CollectionCreateEditVM.class);

                observeEvents();
                collectionEditButton.setOnClickListener(v -> edit());
            }
        }
    }

    private void observeEvents() {
        collectionCreateEditVM.existEvent.observeEvent(this, resource -> {
            if (resource == null) return;
            switch (resource.getStatus()) {
                case LOADING:
                    viewProgressLoading(true);
                    break;
                case ERROR:
                    showConnectionWarning(resource.getThrowable());
                    break;
                case SUCCESS:
                    viewProgressLoading(false);
                    if (resource.getData() != null && !resource.getData()) {
                        collectionCreateEditVM.editCollection(collection, name,
                                SiteService.getCollectionTypeFromSpinner(collection.getType()),
                                collectionDescriptionView.getText().toString(),
                                collectionPublicCheckBox.isChecked() ? 1 : 0);
                    } else {
                        collectionNameView.setError(getString(R.string.collection_create_exist_name));
                    }
                    break;
            }
        });

        collectionCreateEditVM.editEvent.observeEvent(this, resource -> {
            if (resource == null) return;
            switch (resource.getStatus()) {
                case LOADING:
                    viewProgressLoading(true);
                    break;
                case ERROR:
                    showConnectionWarning(resource.getThrowable());
                    break;
                case SUCCESS:
                    viewProgressLoading(false);
                    collection.setName(name);
                    toast(R.string.collection_edited);
                    userCollectionsSharedVM.invalidateUserCollections();
                    break;
            }
        });
    }

    private void edit() {
        viewError(false);
        collectionNameView.setError(null);
        name = collectionNameView.getText().toString().trim();
        if (!TextUtils.isEmpty(name)) {
            collectionCreateEditVM.existCollection(name, collection.getType());
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
