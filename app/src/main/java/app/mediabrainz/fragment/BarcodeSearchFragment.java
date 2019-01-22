package app.mediabrainz.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.ReleaseAdapter;
import app.mediabrainz.api.model.Release;
import app.mediabrainz.intent.ActivityFactory;
import app.mediabrainz.util.ShowUtil;

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.MediaBrainzApp.oauth;


public class BarcodeSearchFragment extends BaseFragment implements TextWatcher {

    private static final String BARCODE = "barcode";

    private String barcode;
    private boolean isLoading;
    private boolean isError;

    private RecyclerView releaseRecyclerView;
    private AutoCompleteTextView barcodeTextView;
    private AutoCompleteTextView releaseSearchView;
    private AutoCompleteTextView artistSearchView;
    private ImageButton barcodeSearchView;
    private TextView barcodeInstructionsView;
    private TextView noresultsView;
    private View containerView;
    private View progressView;
    private View errorView;

    public static BarcodeSearchFragment newInstance(String barcode) {
        Bundle args = new Bundle();
        args.putString(BARCODE, barcode);

        BarcodeSearchFragment fragment = new BarcodeSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflate(R.layout.fragment_barcode_search, container);

        releaseSearchView = layout.findViewById(R.id.releaseSearchView);
        artistSearchView = layout.findViewById(R.id.artistSearchView);
        barcodeTextView = layout.findViewById(R.id.barcodeTextView);
        barcodeSearchView = layout.findViewById(R.id.barcodeSearchView);
        barcodeInstructionsView = layout.findViewById(R.id.barcodeInstructionsView);
        noresultsView = layout.findViewById(R.id.noresultsView);
        progressView = layout.findViewById(R.id.progressView);
        errorView = layout.findViewById(R.id.errorView);
        containerView = layout.findViewById(R.id.containerView);
        releaseRecyclerView = layout.findViewById(R.id.releaseRecyclerView);

        barcode = getArguments().getString(BARCODE);
        barcodeTextView.setText(barcode);

        setListeners();
        configReleaseRecycler();
        return layout;
    }

    private void configReleaseRecycler() {
        releaseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        releaseRecyclerView.setItemViewCacheSize(50);
        releaseRecyclerView.setHasFixedSize(true);
    }

    private void setListeners() {
        releaseSearchView.setOnEditorActionListener((v, actionId, event) -> {
            if (v.getId() == R.id.releaseSearchView && actionId == EditorInfo.IME_NULL && !isLoading) {
                search();
            }
            return false;
        });
        barcodeSearchView.setOnClickListener(v -> {
            if (!isLoading) {
                search();
            }
        });
        barcodeTextView.addTextChangedListener(this);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(releaseSearchView.getWindowToken(), 0);
    }

    private void search() {
        noresultsView.setVisibility(View.GONE);
        viewError(false);
        viewProgressLoading(false);

        barcodeInstructionsView.setVisibility(View.INVISIBLE);
        releaseRecyclerView.setAdapter(null);

        String term = releaseSearchView.getText().toString().trim();
        if (!TextUtils.isEmpty(term)) {
            hideKeyboard();

            viewProgressLoading(true);
            api.searchRelease(
                    artistSearchView.getText().toString().trim(), term,
                    releaseSearch -> {
                        viewProgressLoading(false);
                        if (releaseSearch.getCount() > 0) {
                            List<Release> releases = releaseSearch.getReleases();
                            ReleaseAdapter adapter = new ReleaseAdapter(releases, "");
                            releaseRecyclerView.setAdapter(adapter);
                            adapter.setHolderClickListener(position -> {
                                if (!isLoading) {
                                    showAddBarcodeDialog(releases.get(position).getId());
                                }
                            });
                        } else {
                            noresultsView.setVisibility(View.VISIBLE);
                        }
                    },
                    this::showConnectionWarning,
                    100, 0
            );
        } else {
            Toast.makeText(getContext(), R.string.toast_search_err, Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddBarcodeDialog(String releaseMbid) {
        View titleView = getLayoutInflater().inflate(R.layout.layout_custom_alert_dialog_title, null);
        TextView titleTextView = titleView.findViewById(R.id.titleTextView);
        titleTextView.setText(getString(R.string.barcode_add_header));
        if (oauth.hasAccount()) {
            new AlertDialog.Builder(getContext())
                    .setCustomTitle(titleView)
                    .setMessage(getString(R.string.barcode_add_info))
                    .setPositiveButton(R.string.barcode_add_btn, (dialog, which) -> confirmSubmission(releaseMbid))
                    .setNegativeButton(R.string.barcode_cancel, (dialog, which) -> dialog.cancel())
                    .show();
        } else {
            new AlertDialog.Builder(getContext())
                    .setCustomTitle(titleView)
                    .setMessage(R.string.barcode_info_nolog)
                    .setPositiveButton(R.string.login, (dialog, which) -> ActivityFactory.startLoginActivity(getContext()));
        }
    }

    public void confirmSubmission(String releaseMbid) {
        viewError(false);
        viewProgressLoading(true);
        api.postBarcode(
                releaseMbid, barcode,
                metadata -> {
                    viewProgressLoading(false);
                    if (metadata.getMessage().getText().equals("OK")) {
                        toast(R.string.barcode_added);
                    } else {
                        toast("Error ");
                    }
                },
                t -> {
                    //toast(t.getMessage());
                    viewProgressLoading(false);
                    viewError(true);
                    errorView.findViewById(R.id.retryButton).setOnClickListener(v -> confirmSubmission(releaseMbid));
                }
        );
    }

    private void showConnectionWarning(Throwable t) {
        //ShowUtil.showError(getActivity(), t);
        viewProgressLoading(false);
        viewError(true);
        errorView.findViewById(R.id.retryButton).setOnClickListener(v -> search());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!isDigits(s)) {
            barcodeTextView.setError(getString(R.string.barcode_invalid_chars));
        } else if (!isBarcodeLengthValid(s)) {
            barcodeTextView.setError(getString(R.string.barcode_invalid_length));
        } else {
            barcodeTextView.setError(null);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private boolean isBarcodeLengthValid(CharSequence s) {
        return s.length() == 12 || s.length() == 13;
    }

    private boolean isDigits(CharSequence s) {
        String barcode = s.toString();
        char[] chars = barcode.toCharArray();
        for (char c : chars) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    protected void viewProgressLoading(boolean isView) {
        if (isView) {
            isLoading = true;
            containerView.setAlpha(0.3F);
            progressView.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            containerView.setAlpha(1.0F);
            progressView.setVisibility(View.GONE);
        }
    }

    protected void viewError(boolean isView) {
        if (isView) {
            isError = true;
            containerView.setVisibility(View.INVISIBLE);
            errorView.setVisibility(View.VISIBLE);
        } else {
            isError = false;
            containerView.setVisibility(View.VISIBLE);
            errorView.setVisibility(View.GONE);
        }
    }

}
