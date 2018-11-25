package app.mediabrainz.ui;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;


public class SmartNestedScrollView extends NestedScrollView {

    public SmartNestedScrollView(@NonNull Context context) {
        super(context);
    }

    public SmartNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SmartNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        if (findNestedRecyclerView(child) != null) {
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(lp.topMargin + lp.bottomMargin, MeasureSpec.AT_MOST);
            child.measure(parentWidthMeasureSpec, childHeightMeasureSpec);
        } else {
            super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
        }
    }

    private RecyclerView findNestedRecyclerView(View view) {
        if (view instanceof RecyclerView) return (RecyclerView) view;
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int index = 0;
            do {
                View child = viewGroup.getChildAt(index);
                RecyclerView recyclerView = findNestedRecyclerView(child);
                if (recyclerView == null) {
                    index += 1;
                } else {
                    return recyclerView;
                }
            } while (index < ((ViewGroup) view).getChildCount());
        }
        return null;
    }
}
