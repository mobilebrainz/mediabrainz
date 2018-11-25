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

    private TextView versionLabel;

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
        versionLabel = layout.findViewById(R.id.version_text);
        HtmlAssetTextView body = layout.findViewById(R.id.about_text);
        body.setAsset("about.html");
        return layout;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        versionLabel.setText(getText(R.string.version_text) + " " + MediaBrainzApp.getVersion());
    }

}
