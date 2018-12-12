package app.mediabrainz.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.mediabrainz.R;
import app.mediabrainz.fragment.ArtistSettingsFragment;


public class LocalSettingsDialogFragment extends DialogFragment {

    public static final String TAG = "LocalSettingsDialogFragment";
    private static final String LOCAL_SETTINGS_TYPE = "LOCAL_SETTINGS_TYPE";

    public enum LocalSettingsType {
        ARTIST_SETTINGS(new ArtistSettingsFragment());

        private final PreferenceFragmentCompat preferenceFragmentCompat;

        LocalSettingsType(PreferenceFragmentCompat preferenceFragmentCompat) {
            this.preferenceFragmentCompat = preferenceFragmentCompat;
        }

        public PreferenceFragmentCompat gerLocalSettingsFragment() {
            return preferenceFragmentCompat;
        }
    }

    private LocalSettingsType localSettingsType;

    public static LocalSettingsDialogFragment newInstance(int localSettingsType) {
        Bundle args = new Bundle();
        args.putInt(LOCAL_SETTINGS_TYPE, localSettingsType);
        LocalSettingsDialogFragment fragment = new LocalSettingsDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        localSettingsType = LocalSettingsType.values()[getArguments().getInt(LOCAL_SETTINGS_TYPE, 0)];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_local_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.settings_fragment, localSettingsType.gerLocalSettingsFragment()).commit();
    }

}
