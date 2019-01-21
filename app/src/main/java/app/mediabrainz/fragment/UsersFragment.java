package app.mediabrainz.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.UserAdapter;
import app.mediabrainz.communicator.OnUserCommunicator;
import app.mediabrainz.data.room.entity.User;
import app.mediabrainz.data.room.repository.UserRepository;
import app.mediabrainz.functions.Action;
import app.mediabrainz.viewModels.UsersVM;


public class UsersFragment extends LazyFragment {

    private boolean isLoading;
    private boolean isError;

    private UsersVM usersVM;

    private View noresultsView;
    private RecyclerView recyclerView;
    private View errorView;
    private View progressView;

    public static UsersFragment newInstance() {
        Bundle args = new Bundle();
        UsersFragment fragment = new UsersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        errorView = layout.findViewById(R.id.errorView);
        progressView = layout.findViewById(R.id.progressView);
        noresultsView = layout.findViewById(R.id.noresultsView);
        recyclerView = layout.findViewById(R.id.recyclerView);

        configRecycler();
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        usersVM = ViewModelProviders.of(this).get(UsersVM.class);
        usersVM.usersResource.observe(this, resource -> {
            if (resource == null) return;
            switch (resource.getStatus()) {
                case LOADING:
                    viewProgressLoading(true);
                    break;
                case ERROR:
                    break;
                case SUCCESS:
                    viewProgressLoading(false);
                    show(resource.getData());
                    break;
            }
        });
        loadView();
    }

    private void show(List<User> users) {
        if (users != null && !users.isEmpty()) {
            UserAdapter userAdapter = new UserAdapter(users);
            if (getContext() instanceof OnUserCommunicator) {
                userAdapter.setHolderClickListener(position ->
                        ((OnUserCommunicator) getContext()).onUser(users.get(position).getName()));
            }
            recyclerView.setAdapter(userAdapter);

            userAdapter.setOnDeleteListener(position ->
                    onDelete(users.get(position), () -> {
                        users.remove(position);
                        userAdapter.notifyItemRemoved(position);
                        if (users.size() == 0) {
                            noresultsView.setVisibility(View.VISIBLE);
                        }
                    }));
        } else {
            noresultsView.setVisibility(View.VISIBLE);
        }
    }

    private void configRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemViewCacheSize(100);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void lazyLoad() {
        noresultsView.setVisibility(View.GONE);
        viewError(false);
        viewProgressLoading(false);
        usersVM.lazyLoad();
    }

    public void onDelete(User user, Action action) {
        View titleView = getLayoutInflater().inflate(R.layout.layout_custom_alert_dialog_title, null);
        TextView titleTextView = titleView.findViewById(R.id.titleTextView);
        titleTextView.setText(R.string.user_delete_user);

        new AlertDialog.Builder(getContext())
                .setCustomTitle(titleView)
                .setMessage(getString(R.string.delete_alert_message))
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    viewProgressLoading(true);
                    //todo: перенести в UsersVM
                    new UserRepository().delete(() -> {
                        viewProgressLoading(false);
                        action.run();
                    }, user);
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.cancel())
                .show();
    }

    private void viewProgressLoading(boolean isView) {
        if (isView) {
            isLoading = true;
            progressView.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            progressView.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean isView) {
        if (isView) {
            isError = true;
            errorView.setVisibility(View.VISIBLE);
        } else {
            isError = false;
            errorView.setVisibility(View.GONE);
        }
    }

}
