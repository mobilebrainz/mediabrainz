package app.mediabrainz.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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

import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.ReleaseAdapter;
import app.mediabrainz.api.model.Release;
import app.mediabrainz.intent.ActivityFactory;
import app.mediabrainz.util.ShowUtil;

import java.util.List;

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.MediaBrainzApp.oauth;


public class BarcodeSearchFragment extends Fragment implements TextWatcher {

    private static final String BARCODE = "barcode";

    private String barcode;
    private boolean isLoading;
    private boolean isError;

    private RecyclerView releaseRecycler;
    private AutoCompleteTextView barcodeText;
    private AutoCompleteTextView searchBox;
    private AutoCompleteTextView searchArtist;
    private ImageButton searchButton;
    private TextView instructions;
    private TextView noResults;
    private View contentContainer;
    private View loading;
    private View error;

    public static BarcodeSearchFragment newInstance(String barcode) {
        Bundle args = new Bundle();
        args.putString(BARCODE, barcode);

        BarcodeSearchFragment fragment = new BarcodeSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_barcode_search, container, false);

        searchBox = layout.findViewById(R.id.release_search);
        searchArtist = layout.findViewById(R.id.artist_search);
        barcodeText = layout.findViewById(R.id.barcode_text);
        searchButton = layout.findViewById(R.id.barcode_search_btn);
        instructions = layout.findViewById(R.id.barcode_instructions);
        noResults = layout.findViewById(R.id.noresults);
        loading = layout.findViewById(R.id.loading);
        error = layout.findViewById(R.id.error);
        contentContainer = layout.findViewById(R.id.container);
        releaseRecycler = layout.findViewById(R.id.release_recycler);

        barcode = getArguments().getString(BARCODE);
        barcodeText.setText(barcode);

        setListeners();
        configReleaseRecycler();
        return layout;
    }

    private void configReleaseRecycler() {
        releaseRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        releaseRecycler.setItemViewCacheSize(50);
        releaseRecycler.setHasFixedSize(true);
    }

    private void setListeners() {
        searchBox.setOnEditorActionListener((v, actionId, event) -> {
            if (v.getId() == R.id.release_search && actionId == EditorInfo.IME_NULL && !isLoading) {
                search();
            }
            return false;
        });
        searchButton.setOnClickListener(v -> {
            if (!isLoading) {
                search();
            }
        });
        barcodeText.addTextChangedListener(this);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
    }

    private void search() {
        noResults.setVisibility(View.GONE);
        viewError(false);
        viewProgressLoading(false);

        instructions.setVisibility(View.INVISIBLE);
        releaseRecycler.setAdapter(null);

        String term = searchBox.getText().toString().trim();
        if (!TextUtils.isEmpty(term)) {
            hideKeyboard();

            viewProgressLoading(true);
            api.searchRelease(
                    searchArtist.getText().toString().trim(), term,
                    releaseSearch -> {
                        viewProgressLoading(false);
                        if (releaseSearch.getCount() > 0) {
                            List<Release> releases = releaseSearch.getReleases();
                            ReleaseAdapter adapter = new ReleaseAdapter(releases, "");
                            releaseRecycler.setAdapter(adapter);
                            adapter.setHolderClickListener(position -> {
                                if (!isLoading) {
                                    showAddBarcodeDialog(releases.get(position).getId());
                                }
                            });
                        } else {
                            noResults.setVisibility(View.VISIBLE);
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
        TextView titleText = titleView.findViewById(R.id.title_text);
        titleText.setText(getString(R.string.barcode_add_header));
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
                        Toast.makeText(getContext(), getString(R.string.barcode_added), Toast.LENGTH_SHORT).show();
                    } else {
                        ShowUtil.showMessage(getActivity(), "Error ");
                    }
                },
                t -> {
                    ShowUtil.showError(getActivity(), t);
                    viewProgressLoading(false);
                    viewError(true);
                    error.findViewById(R.id.retry_button).setOnClickListener(v -> confirmSubmission(releaseMbid));
                }
        );
    }

    private void showConnectionWarning(Throwable t) {
        //ShowUtil.showError(getActivity(), t);
        viewProgressLoading(false);
        viewError(true);
        error.findViewById(R.id.retry_button).setOnClickListener(v -> search());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!isDigits(s)) {
            barcodeText.setError(getString(R.string.barcode_invalid_chars));
        } else if (!isBarcodeLengthValid(s)) {
            barcodeText.setError(getString(R.string.barcode_invalid_length));
        } else {
            barcodeText.setError(null);
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
            contentContainer.setAlpha(0.3F);
            loading.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            contentContainer.setAlpha(1.0F);
            loading.setVisibility(View.GONE);
        }
    }

    protected void viewError(boolean isView) {
        if (isView) {
            isError = true;
            contentContainer.setVisibility(View.INVISIBLE);
            error.setVisibility(View.VISIBLE);
        } else {
            isError = false;
            contentContainer.setVisibility(View.VISIBLE);
            error.setVisibility(View.GONE);
        }
    }

}
