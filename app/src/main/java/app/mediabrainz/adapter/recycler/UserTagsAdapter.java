package app.mediabrainz.adapter.recycler;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.mediabrainz.R;
import app.mediabrainz.api.model.Tag;

import java.util.List;


public class UserTagsAdapter extends BaseRecyclerViewAdapter<UserTagsAdapter.UserTagsViewHolder> {

    private List<Tag> tags;

    public static class UserTagsViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_user_tag;

        private TextView tagNameView;
        private TextView tagCountView;

        public static UserTagsViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new UserTagsViewHolder(view);
        }

        private UserTagsViewHolder(View v) {
            super(v);
            tagNameView = v.findViewById(R.id.tagNameView);
            tagCountView = v.findViewById(R.id.tagCountView);
        }

        public void bindTo(Tag tag) {
            tagNameView.setText(tag.getName());
            tagCountView.setText(String.valueOf(tag.getCount()));
        }
    }

    public UserTagsAdapter(List<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public void onBind(UserTagsViewHolder holder, final int position) {
        holder.bindTo(tags.get(position));
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    @NonNull
    @Override
    public UserTagsAdapter.UserTagsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return UserTagsViewHolder.create(parent);
    }

}
