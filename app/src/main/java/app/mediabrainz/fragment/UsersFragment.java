package app.mediabrainz.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.UserAdapter;
import app.mediabrainz.communicator.OnUserCommunicator;
import app.mediabrainz.data.room.entity.User;
import app.mediabrainz.data.room.repository.UserRepository;
import app.mediabrainz.functions.Action;


public class UsersFragment extends LazyFragment {

    private boolean isLoading;
    private boolean isError;

    private View noresults;
    private RecyclerView recycler;
    private View error;
    private View loading;

    public static UsersFragment newInstance() {
        Bundle args = new Bundle();
        UsersFragment fragment = new UsersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        error = layout.findViewById(R.id.error);
        loading = layout.findViewById(R.id.loading);
        noresults = layout.findViewById(R.id.noresults);
        recycler = layout.findViewById(R.id.recycler);

        configRecycler();
        loadView();
        return layout;
    }

    private void configRecycler() {
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setItemViewCacheSize(100);
        recycler.setHasFixedSize(true);
    }

    @Override
    protected void lazyLoad() {
        noresults.setVisibility(View.GONE);
        viewError(false);
        viewProgressLoading(true);

        new UserRepository().getUsers(users -> {
            viewProgressLoading(false);
            if (!users.isEmpty()) {

                UserAdapter userAdapter = new UserAdapter(users);
                userAdapter.setHolderClickListener(position ->
                        ((OnUserCommunicator) getContext()).onUser(users.get(position).getName()));
                recycler.setAdapter(userAdapter);

                userAdapter.setOnDeleteListener(position ->
                        onDelete(users.get(position), () -> {
                            users.remove(position);
                            userAdapter.notifyItemRemoved(position);
                            if (users.size() == 0) {
                                noresults.setVisibility(View.VISIBLE);
                            }
                        }));
            } else {
                noresults.setVisibility(View.VISIBLE);
            }
        });

    }

    public void onDelete(User user, Action action) {
        View titleView = getLayoutInflater().inflate(R.layout.layout_custom_alert_dialog_title, null);
        TextView titleText = titleView.findViewById(R.id.title_text);
        titleText.setText(R.string.user_delete_user);

        new AlertDialog.Builder(getContext())
                .setCustomTitle(titleView)
                .setMessage(getString(R.string.delete_alert_message))
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    viewProgressLoading(true);
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
            loading.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            loading.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean isView) {
        if (isView) {
            isError = true;
            error.setVisibility(View.VISIBLE);
        } else {
            isError = false;
            error.setVisibility(View.GONE);
        }
    }

}