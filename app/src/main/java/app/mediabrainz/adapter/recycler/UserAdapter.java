package app.mediabrainz.adapter.recycler;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import app.mediabrainz.R;
import app.mediabrainz.data.room.entity.User;


public class UserAdapter extends BaseRecyclerViewAdapter<UserAdapter.UserViewHolder> {

    private List<User> users;

    public static class UserViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_user;

        private TextView userNameView;
        private ImageView deleteView;

        public static UserViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new UserViewHolder(view);
        }

        private UserViewHolder(View v) {
            super(v);
            userNameView = v.findViewById(R.id.userNameView);
            deleteView = v.findViewById(R.id.deleteView);
        }

        public void bindTo(User user) {
            userNameView.setText(user.getName());
        }

        public void setOnDeleteListener(OnDeleteListener listener) {
            deleteView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(getAdapterPosition());
                }
            });
        }
    }

    public UserAdapter(List<User> users) {
        this.users = users;
    }

    @Override
    public void onBind(UserViewHolder holder, final int position) {
        holder.setOnDeleteListener(onDeleteListener);
        holder.bindTo(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return UserViewHolder.create(parent);
    }

    public interface OnDeleteListener {
        void onDelete(int position);
    }

    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }
}
