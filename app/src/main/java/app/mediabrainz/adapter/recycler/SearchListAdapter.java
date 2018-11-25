package app.mediabrainz.adapter.recycler;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.mediabrainz.R;

import java.util.List;


public class SearchListAdapter extends BaseRecyclerViewAdapter<SearchListAdapter.SearchListViewHolder> {

    private List<String> strings;

    public static class SearchListViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_search_list;

        private TextView string;

        public static SearchListViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new SearchListViewHolder(view);
        }

        private SearchListViewHolder(View v) {
            super(v);
            string = v.findViewById(R.id.string);
        }

        public void bindTo(String tag) {
            string.setText(tag);
        }
    }

    public SearchListAdapter(List<String> strings) {
        this.strings = strings;
    }

    @Override
    public void onBind(SearchListViewHolder holder, final int position) {
        holder.bindTo(strings.get(position));
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    @NonNull
    @Override
    public SearchListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return SearchListViewHolder.create(parent);
    }

}
