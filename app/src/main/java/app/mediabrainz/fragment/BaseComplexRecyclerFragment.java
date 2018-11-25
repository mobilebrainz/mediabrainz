package app.mediabrainz.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.expandedRecycler.BaseExpandedRecyclerAdapter;
import app.mediabrainz.adapter.recycler.expandedRecycler.Section;


public abstract class BaseComplexRecyclerFragment<T> extends LazyFragment {

    protected static final String CHECKED_ITEMS = "CHECKED_ITEMS";
    protected static final String EXPANDED_ITEMS = "EXPANDED_ITEMS";

    protected boolean[] checkedItems;
    protected boolean[] expandedItems;
    protected List<Section<T>> allSections = new ArrayList<>();
    protected List<Section<T>> viewSections = new ArrayList<>();
    protected BaseExpandedRecyclerAdapter<T> recyclerAdapter;

    protected NestedScrollView scrollView;
    protected FrameLayout frameView;
    protected LinearLayout recyclerContainer;
    protected LinearLayout recyclerSettingView;
    protected CheckBox expandCheckBox;
    protected LinearLayout filterView;
    protected ImageView filterBtnTop;
    protected ImageView selectBtnTop;
    protected RecyclerView recycler;
    protected LinearLayout bottomBar;
    protected ImageView selectBtn;
    protected ImageView expandBtn;
    protected ImageView filterBtn;
    protected ImageView upBtn;

    protected Integer getCustomContentLayout() {
        return null;
    }

    public void addFrameView(View view) {
        frameView.addView(view);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int contentLayout = getCustomContentLayout() == null ? R.layout.fragment_base_complex_recycler : getCustomContentLayout();
        View layout = inflater.inflate(contentLayout, container, false);

        scrollView = layout.findViewById(R.id.scroll_view);
        frameView = layout.findViewById(R.id.frame_view);
        recyclerContainer = layout.findViewById(R.id.recycler_container);
        expandCheckBox = layout.findViewById(R.id.expand_check_box);
        recycler = layout.findViewById(R.id.recycler);
        recyclerSettingView = layout.findViewById(R.id.recycler_setting);
        filterView = layout.findViewById(R.id.filter);
        filterBtnTop = layout.findViewById(R.id.filter_btn_top);
        selectBtnTop = layout.findViewById(R.id.select_btn_top);
        bottomBar = layout.findViewById(R.id.bottom_bar);
        selectBtn = layout.findViewById(R.id.select_btn);
        expandBtn = layout.findViewById(R.id.expand_btn);
        filterBtn = layout.findViewById(R.id.filter_btn);
        upBtn = layout.findViewById(R.id.up_btn);

        return layout;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBooleanArray(CHECKED_ITEMS, checkedItems);
        expandedItems = new boolean[viewSections.size()];
        for (int i = 0; i < expandedItems.length; i++) {
            expandedItems[i] = viewSections.get(i).getHeader().isExpand();
        }
        outState.putBooleanArray(EXPANDED_ITEMS, expandedItems);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            checkedItems = savedInstanceState.getBooleanArray(CHECKED_ITEMS);
            expandedItems = savedInstanceState.getBooleanArray(EXPANDED_ITEMS);
        }
    }

    // call before creation of recycler adapter
    public void restoreRecyclerToolbarState(List<Section<T>> items) {
        if (isChecked() && checkedItems.length == items.size()) {
            for (int i = 0; i < checkedItems.length; i++) {
                items.get(i).getHeader().setVisible(checkedItems[i]);
            }
        }
        if (expandedItems != null && expandedItems.length == items.size()) {
            for (int i = 0; i < expandedItems.length; i++) {
                items.get(i).getHeader().setExpand(expandedItems[i]);
            }
        }
    }

    public boolean isChecked() {
        boolean checked = false;
        if (checkedItems != null) {
            for (boolean checkedItem : checkedItems) {
                if (checkedItem) {
                    checked = true;
                    break;
                }
            }
        }
        return checked;
    }

    public void clearSections() {
        for (Section section : allSections) {
            section.getItems().clear();
        }
    }

    public void filter(View v) {
        int size = recyclerAdapter.getSections().size();
        String[] headerTitles = new String[size];
        for (int i = 0; i < size; i++) {
            headerTitles[i] = recyclerAdapter.getSections().get(i).getHeader().getTitle();
        }
        boolean[] finalCheckedItems = checkedItems == null ? new boolean[size] : checkedItems;

        View titleView = getLayoutInflater().inflate(R.layout.layout_custom_alert_dialog_title, null);
        TextView titleText = titleView.findViewById(R.id.title_text);
        titleText.setText(R.string.select_items);

        new AlertDialog.Builder(getContext())
                .setCustomTitle(titleView)
                .setMultiChoiceItems(headerTitles, finalCheckedItems, (dialog, which, isChecked) -> {
                })
                .setPositiveButton(android.R.string.yes, (dialog, id) -> {
                    this.checkedItems = finalCheckedItems;
                    boolean checked = isChecked();
                    for (int i = 0; i < size; i++) {
                        recyclerAdapter.hide(i, checked && !checkedItems[i]);
                    }
                    if (checked) {
                        if (!expandCheckBox.isChecked()) {
                            expandCheckBox.setChecked(true);
                        } else {
                            recyclerAdapter.expandAll(true);
                        }
                    } else {
                        if (expandCheckBox.isChecked()) {
                            expandCheckBox.setChecked(false);
                        } else {
                            recyclerAdapter.expandAll(false);
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, (dialog, id) -> dialog.cancel())
                .show();
    }

    public void select(View v) {
        int size = recyclerAdapter.getSections().size();
        String[] headerTitles = new String[size + 1];
        headerTitles[0] = getString(R.string.select_all);
        for (int i = 0; i < size; i++) {
            headerTitles[i + 1] = recyclerAdapter.getSections().get(i).getHeader().getTitle();
        }

        View titleView = getLayoutInflater().inflate(R.layout.layout_custom_alert_dialog_title, null);
        TextView titleText = titleView.findViewById(R.id.title_text);
        titleText.setText(R.string.select_item);

        new AlertDialog.Builder(getContext())
            .setCustomTitle(titleView)
            .setItems(headerTitles, (dialog, which) -> {
                if (which == 0) {
                    checkedItems = null;
                    expandCheckBox.setChecked(false);
                    for (int i = 0; i < size; i++) {
                        recyclerAdapter.hide(i, false);
                    }
                } else {
                    if (checkedItems == null) {
                        checkedItems = new boolean[size];
                    }
                    recyclerAdapter.hide(which - 1, false);
                    checkedItems[which - 1] = true;
                    for (int i = 0; i < size; i++) {
                        if (i != which - 1) {
                            checkedItems[i] = false;
                            recyclerAdapter.hide(i, true);
                        }
                    }
                    if (!expandCheckBox.isChecked()) {
                        expandCheckBox.setChecked(true);
                    } else {
                        recyclerAdapter.expand(which - 1, true);
                    }
                }
            })
            .show();

    }

    public void configRecyclerToolbar() {
        if (recyclerAdapter.getSections().size() > 1) {
            recyclerSettingView.setVisibility(View.VISIBLE);
            bottomBar.setVisibility(View.VISIBLE);

            expandCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int img = isChecked ? R.drawable.ic_arrow_collapse_all_24 :
                        R.drawable.ic_arrow_expand_all_24;
                expandBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), img));
                recyclerAdapter.expandAll(isChecked);
            });
            expandBtn.setOnClickListener(v -> expandCheckBox.setChecked(!expandCheckBox.isChecked()));

            filterBtnTop.setOnClickListener(this::filter);
            filterBtn.setOnClickListener(this::filter);

            selectBtnTop.setOnClickListener(this::select);
            selectBtn.setOnClickListener(this::select);

            upBtn.setOnClickListener(v -> {
                scrollView.fullScroll(View.FOCUS_UP);
                scrollView.fullScroll(View.FOCUS_UP);
            });
        } else {
            recyclerSettingView.setVisibility(View.GONE);
            bottomBar.setVisibility(View.GONE);
            recyclerAdapter.expandAll(true);
        }
    }

}
