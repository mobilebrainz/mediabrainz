package app.mediabrainz.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import app.mediabrainz.R;
import app.mediabrainz.communicator.ShowTitleCommunicator;
import app.mediabrainz.util.ShowUtil;
import app.mediabrainz.viewModels.CollectionCreateEditVM;
import app.mediabrainz.viewModels.UserCollectionsSharedVM;

import static app.mediabrainz.MediaBrainzApp.oauth;


public class CollectionCreateFragment extends Fragment {

    private UserCollectionsSharedVM userCollectionsSharedVM;
    private CollectionCreateEditVM collectionCreateEditVM;
    private String name;
    private int type;
    private boolean isLoading;
    private boolean isError;

    private EditText collectionNameView;
    private Spinner collectionTypeSpinner;
    private EditText collectionDescriptionView;
    private CheckBox collectionPublicCheckBox;
    private Button collectionCreateButton;
    private LinearLayout containerView;
    private View errorView;
    private View progressView;

    public static CollectionCreateFragment newInstance() {
        Bundle args = new Bundle();
        CollectionCreateFragment fragment = new CollectionCreateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_collection_create, container, false);

        collectionNameView = layout.findViewById(R.id.collectionNameView);
        collectionDescriptionView = layout.findViewById(R.id.collectionDescriptionView);
        collectionPublicCheckBox = layout.findViewById(R.id.collectionPublicCheckBox);
        collectionCreateButton = layout.findViewById(R.id.collectionCreateButton);
        collectionTypeSpinner = layout.findViewById(R.id.collectionTypeSpinner);
        errorView = layout.findViewById(R.id.errorView);
        progressView = layout.findViewById(R.id.progressView);
        containerView = layout.findViewById(R.id.containerView);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.collection_type_spinner,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        collectionTypeSpinner.setAdapter(adapter);

        ((ShowTitleCommunicator) getContext()).getToolbarTopTitleView().setText(R.string.title_create_collection);

        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            userCollectionsSharedVM = ViewModelProviders
                    .of(getActivity(), new UserCollectionsSharedVM.Factory(oauth.getName()))
                    .get(UserCollectionsSharedVM.class);

            collectionCreateEditVM = ViewModelProviders
                    .of(this)
                    .get(CollectionCreateEditVM.class);

            observeEvents();
            collectionCreateButton.setOnClickListener(v -> create());
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
                        collectionCreateEditVM.createCollection(name, type,
                                collectionDescriptionView.getText().toString(),
                                collectionPublicCheckBox.isChecked() ? 1 : 0);
                    } else {
                        collectionNameView.setError(getString(R.string.collection_create_exist_name));
                    }
                    break;
            }
        });

        collectionCreateEditVM.createEvent.observeEvent(this, resource -> {
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
                    ShowUtil.showToast(getContext(), R.string.collection_created);
                    userCollectionsSharedVM.invalidateUserCollections();
                    break;
            }
        });
    }

    private void create() {
        viewError(false);

        collectionNameView.setError(null);
        name = collectionNameView.getText().toString().trim();
        if (!TextUtils.isEmpty(name)) {
            type = collectionTypeSpinner.getSelectedItemPosition() + 1;
            collectionCreateEditVM.existCollection(name, type);
        }
    }

    private void viewProgressLoading(boolean isView) {
        if (isView) {
            isLoading = true;
            containerView.setAlpha(0.3F);
            progressView.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            containerView.setAlpha(1.0F);
            progressView.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean isView) {
        if (isView) {
            isError = true;
            containerView.setVisibility(View.INVISIBLE);
            errorView.setVisibility(View.VISIBLE);
        } else {
            isError = false;
            errorView.setVisibility(View.GONE);
            containerView.setVisibility(View.VISIBLE);
        }
    }

    private void showConnectionWarning(Throwable t) {
        //ShowUtil.showError(getContext(), t);
        viewProgressLoading(false);
        viewError(true);
        errorView.findViewById(R.id.retryButton).setOnClickListener(v -> create());
    }

}
