package app.mediabrainz.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.R;
import app.mediabrainz.ui.view.HtmlAssetTextView;


public class AboutFragment extends Fragment {

    private TextView appVersionView;

    public static AboutFragment newInstance() {
        Bundle args = new Bundle();

        AboutFragment fragment = new AboutFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_about, container);
        appVersionView = layout.findViewById(R.id.appVersionView);
        HtmlAssetTextView aboutView = layout.findViewById(R.id.aboutView);
        aboutView.setAsset("about.html");
        return layout;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        appVersionView.setText(getText(R.string.version_text) + " " + MediaBrainzApp.getVersion());
    }

}
