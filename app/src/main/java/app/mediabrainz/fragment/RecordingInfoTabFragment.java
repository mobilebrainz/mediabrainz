package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.mediabrainz.R;


public class RecordingInfoTabFragment extends BaseFragment {

    public static RecordingInfoTabFragment newInstance() {
        Bundle args = new Bundle();
        RecordingInfoTabFragment fragment = new RecordingInfoTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflate(R.layout.fragment_recording_info, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        insertNestedFragments();
    }

    private void insertNestedFragments() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction
                .replace(R.id.recordingInformationFragment, new RecordingInformationFragment())
                .replace(R.id.wikipediaWebViewFragment, new WikipediaWebViewFragment())
                .commit();
    }

}
