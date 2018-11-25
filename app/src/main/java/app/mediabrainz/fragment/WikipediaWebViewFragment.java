package app.mediabrainz.fragment;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.os.ConfigurationCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import app.mediabrainz.R;
import app.mediabrainz.api.model.Url;
import app.mediabrainz.communicator.GetUrlsCommunicator;
import app.mediabrainz.communicator.SetWebViewCommunicator;

import java.util.List;
import java.util.Map;

import static app.mediabrainz.MediaBrainzApp.api;


public class WikipediaWebViewFragment extends Fragment {

    private String wikidataQ;
    private String lang;
    private String buttonLang;
    private Map<String, String> urlMap;

    private WebView webView;
    private ProgressBar loading;
    private View error;
    private Button langButton;

    public static WikipediaWebViewFragment newInstance() {
        Bundle args = new Bundle();
        WikipediaWebViewFragment fragment = new WikipediaWebViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_web_view, container, false);

        loading = layout.findViewById(R.id.loading);
        error = layout.findViewById(R.id.error);
        webView = layout.findViewById(R.id.web_view);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                loading.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loading.setVisibility(View.GONE);
                if (buttonLang != null) {
                    langButton.setText(buttonLang.equals("en") ? lang : "en");
                    langButton.setVisibility(View.VISIBLE);
                } else {
                    langButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Toast.makeText(getActivity(), "Cannot getWikidata page", Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.GONE);
            }
        });
        ((SetWebViewCommunicator) getContext()).setWebView(webView);

        lang = ConfigurationCompat.getLocales(getResources().getConfiguration()).get(0).getLanguage();
        langButton = layout.findViewById(R.id.lang_btn);
        langButton.setOnClickListener(v -> {
            buttonLang = buttonLang.equals("en") ? lang : "en";
            webView.loadUrl(urlMap.get(buttonLang));
        });

        getWikidata();
        return layout;
    }

    public void load() {
        viewError(false);

        viewProgressLoading(true);
        api.getSitelinks(
                wikidataQ,
                result -> {
                    urlMap = result;
                    viewProgressLoading(false);

                    String url = null;
                    if (result.containsKey(lang)) {
                        if (buttonLang == null) {
                            buttonLang = lang;
                        }
                        url = result.get(lang);
                    } else if (result.containsKey("en")) {
                        url = result.get("en");
                    }
                    if (url != null) {
                        webView.loadUrl(url);
                    }
                },
                this::showConnectionWarning,
                lang);
    }

    public void getWikidata() {
        List<Url> urls = ((GetUrlsCommunicator) getContext()).getUrls();
        if (urls != null && !urls.isEmpty()) {
            for (Url link : urls) {
                String resource = link.getResource();
                if (link.getType().equalsIgnoreCase("wikidata")) {
                    int pageSplit = resource.lastIndexOf("/") + 1;
                    wikidataQ = resource.substring(pageSplit);
                    if (!TextUtils.isEmpty(wikidataQ)) {
                        load();
                    }
                    break;
                }
            }
        }
    }

    private void viewProgressLoading(boolean isView) {
        if (isView) {
            loading.setVisibility(View.VISIBLE);
        } else {
            loading.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean isView) {
        if (isView) {
            error.setVisibility(View.VISIBLE);
        } else {
            error.setVisibility(View.GONE);
        }
    }

    private void showConnectionWarning(Throwable t) {
        //ShowUtil.showError(getActivity(), t);
        loading.setVisibility(View.GONE);
        viewError(true);
        error.findViewById(R.id.retry_button).setOnClickListener(v -> load());
    }

}
