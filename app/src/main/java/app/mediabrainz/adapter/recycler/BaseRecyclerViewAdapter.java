package app.mediabrainz.adapter.recycler;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public abstract class BaseRecyclerViewAdapter<BVH extends BaseRecyclerViewAdapter.BaseViewHolder> extends RecyclerView.Adapter<BVH> {

    public interface HolderClickListener {
        void onClick(int position);
    }

    private HolderClickListener holderClickListener;

    public void setHolderClickListener(HolderClickListener holderClickListener) {
        this.holderClickListener = holderClickListener;
    }

    public abstract void onBind(BVH holder, final int position);

    @Override
    public final void onBindViewHolder(BVH holder, final int position) {
        holder.setHolderClickListener(holderClickListener);
        onBind(holder, position);
    }

    public CardView inflateCardView(ViewGroup parent, int cardViewRes) {
        return (CardView) LayoutInflater.from(parent.getContext())
                .inflate(cardViewRes, parent, false);
    }

    public abstract static class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(View view) {
            super(view);
        }

        public void setHolderClickListener(HolderClickListener listener) {
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onClick(getAdapterPosition());
                }
            });
        }
    }

}
