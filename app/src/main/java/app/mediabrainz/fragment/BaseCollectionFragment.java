package app.mediabrainz.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.RetryCallback;
import app.mediabrainz.api.model.BaseLookupEntity;
import app.mediabrainz.api.model.Collection;
import app.mediabrainz.api.other.CollectionService;
import app.mediabrainz.api.other.CollectionServiceInterface;
import app.mediabrainz.communicator.GetCollectionCommunicator;
import app.mediabrainz.communicator.GetUsernameCommunicator;
import app.mediabrainz.communicator.ShowFloatingActionButtonCommunicator;
import app.mediabrainz.communicator.ShowTitleCommunicator;
import app.mediabrainz.util.ShowUtil;
import app.mediabrainz.viewModels.BaseCollectionVM;
import app.mediabrainz.viewModels.UserCollectionsSharedVM;

import static app.mediabrainz.MediaBrainzApp.oauth;


public abstract class BaseCollectionFragment extends BaseFragment implements RetryCallback {

    protected boolean isPrivate;
    protected Collection collection;
    protected UserCollectionsSharedVM userCollectionsSharedVM;
    private BaseCollectionVM baseCollectionVM;

    protected SwipeRefreshLayout swipeRefreshLayout;
    protected RecyclerView pagedRecyclerView;
    protected TextView errorMessageTextView;
    protected Button retryLoadingButton;
    protected ProgressBar loadingProgressBar;
    protected View itemNetworkStateView;

    protected View errorView;
    protected View progressView;

    public abstract void load();

    @NonNull
    public abstract BaseCollectionVM initViewModel();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflate(R.layout.fragment_paged_recycler, container);

        errorView = layout.findViewById(R.id.errorView);
        progressView = layout.findViewById(R.id.progressView);

        swipeRefreshLayout = layout.findViewById(R.id.swipeRefreshLayout);
        errorMessageTextView = layout.findViewById(R.id.errorMessageTextView);
        loadingProgressBar = layout.findViewById(R.id.loadingProgressBar);
        itemNetworkStateView = layout.findViewById(R.id.itemNetworkStateView);

        pagedRecyclerView = layout.findViewById(R.id.pagedRecyclerView);
        pagedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pagedRecyclerView.setNestedScrollingEnabled(true);
        pagedRecyclerView.setHasFixedSize(true);
        pagedRecyclerView.setItemViewCacheSize(100);

        retryLoadingButton = layout.findViewById(R.id.retryLoadingButton);
        retryLoadingButton.setOnClickListener(view -> retry());

        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null &&
                getContext() instanceof GetUsernameCommunicator &&
                getContext() instanceof GetCollectionCommunicator) {

            String username = ((GetUsernameCommunicator) getContext()).getUsername();
            isPrivate = oauth.hasAccount() && username.equals(oauth.getName());

            collection = ((GetCollectionCommunicator) getContext()).getCollection();

            if (collection != null) {
                if (getContext() instanceof ShowTitleCommunicator) {
                    ((ShowTitleCommunicator) getContext()).getToolbarTopTitleView().setText(collection.getName());

                }
                if (getContext() instanceof ShowFloatingActionButtonCommunicator && isPrivate) {
                    ((ShowFloatingActionButtonCommunicator) getContext())
                            .showFloatingActionButton(true, ShowFloatingActionButtonCommunicator.FloatingButtonType.EDIT_COLLECTION);
                }

                userCollectionsSharedVM = ViewModelProviders
                        .of(getActivity(), new UserCollectionsSharedVM.Factory(oauth.getName()))
                        .get(UserCollectionsSharedVM.class);

                baseCollectionVM = initViewModel();
                baseCollectionVM.deleteEntityEvent.observeEvent(this, resource -> {
                    if (resource == null) return;
                    switch (resource.getStatus()) {
                        case LOADING:
                            progressView.setVisibility(View.VISIBLE);
                            break;
                        case ERROR:
                            showConnectionWarning(resource.getThrowable());
                            break;
                        case SUCCESS:
                            progressView.setVisibility(View.GONE);
                            if (resource.getData() != null && resource.getData().getMessage().getText().equals("OK")) {
                                load();
                                userCollectionsSharedVM.invalidateUserCollections();
                            } else {
                                toast("Error");
                            }
                            break;
                    }
                });
                load();
            }
        }
    }

    public void onDelete(BaseLookupEntity entity) {
        View titleView = getLayoutInflater().inflate(R.layout.layout_custom_alert_dialog_title, null);
        TextView titleTextView = titleView.findViewById(R.id.titleTextView);
        titleTextView.setText(R.string.collection_delete_entity);

        if (getContext() != null) {
            new AlertDialog.Builder(getContext())
                    .setCustomTitle(titleView)
                    .setMessage(getString(R.string.delete_alert_message))
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        CollectionServiceInterface.CollectionType collType = CollectionService.getCollectionType(collection.getEntityType());
                        if (collType != null) {
                            baseCollectionVM.deleteEntityFromCollection(collection, entity);
                        }
                    })
                    .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.cancel())
                    .show();
        }
    }

    private void showConnectionWarning(Throwable t) {
        progressView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        errorView.findViewById(R.id.retryButton).setOnClickListener(v -> {
            errorView.setVisibility(View.GONE);
            load();
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isPrivate) {
            ((ShowFloatingActionButtonCommunicator) getContext()).showFloatingActionButton(false, ShowFloatingActionButtonCommunicator.FloatingButtonType.EDIT_COLLECTION);
        }
    }
}
