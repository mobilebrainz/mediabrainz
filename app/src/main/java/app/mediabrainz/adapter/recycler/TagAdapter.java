package app.mediabrainz.adapter.recycler;

import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import app.mediabrainz.R;
import app.mediabrainz.api.model.Tag;

import java.util.Collections;
import java.util.List;


public class TagAdapter extends BaseRecyclerViewAdapter<TagAdapter.TagViewHolder> {

    private List<Tag> tags;
    private List<Tag> userTags;

    public static class TagViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_tag;

        private TextView tagNameView;
        private TextView votesCountView;
        private ImageView voteButton;

        public static TagViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new TagViewHolder(view);
        }

        private TagViewHolder(View v) {
            super(v);
            tagNameView = v.findViewById(R.id.tagNameView);
            votesCountView = v.findViewById(R.id.votesCountView);
            voteButton = v.findViewById(R.id.voteButton);
        }

        public void bindTo(Tag tag, boolean votted) {
            tagNameView.setText(tag.getName());
            votesCountView.setText(String.valueOf(tag.getCount()));
            if (votted) {
                voteButton.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.colorAccent)));
            }
        }

        public void setOnVoteTagListener(OnVoteTagListener listener) {
            voteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onVote(getAdapterPosition());
                }
            });
        }
    }

    public TagAdapter(List<Tag> tags, List<Tag> userTags) {
        this.tags = tags;
        this.userTags = userTags;
        Collections.sort(this.tags, (t1, t2) -> t2.getCount() - t1.getCount());
    }

    @Override
    public void onBind(TagViewHolder holder, final int position) {
        holder.setOnVoteTagListener(onVoteTagListener);
        boolean votted = false;
        if (userTags != null && !userTags.isEmpty()) {
            for (Tag userTag : userTags) {
                if (userTag.getName().equalsIgnoreCase(tags.get(position).getName())) {
                    votted = true;
                    break;
                }
            }
        }
        holder.bindTo(tags.get(position), votted);
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    @NonNull
    @Override
    public TagAdapter.TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return TagViewHolder.create(parent);
    }

    public interface OnVoteTagListener {
        void onVote(int position);
    }

    private OnVoteTagListener onVoteTagListener;

    public void setOnVoteTagListener(OnVoteTagListener onVoteTagListener) {
        this.onVoteTagListener = onVoteTagListener;
    }
}
