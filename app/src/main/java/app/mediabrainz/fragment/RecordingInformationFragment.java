package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.mediabrainz.R;
import app.mediabrainz.api.core.ApiUtils;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.Recording;
import app.mediabrainz.communicator.GetRecordingCommunicator;
import app.mediabrainz.util.MbUtils;


public class RecordingInformationFragment extends Fragment {

    private Recording recording;

    private TextView recordingName;
    private TextView length;
    private TextView artistNameView;

    public static RecordingInformationFragment newInstance() {
        Bundle args = new Bundle();
        RecordingInformationFragment fragment = new RecordingInformationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_recording_information, container, false);

        recordingName = layout.findViewById(R.id.recordingNameView);
        length = layout.findViewById(R.id.length);
        artistNameView = layout.findViewById(R.id.artistNameView);

        load();
        return layout;
    }

    public void load() {
        recording = ((GetRecordingCommunicator) getContext()).getRecording();
        if (recording != null) {
            setRecordingName();
            setArtistName();
            setLength();
        }
    }

    private void setRecordingName() {
        String name = recording.getTitle();
        if (!TextUtils.isEmpty(name)) {
            recordingName.setVisibility(View.VISIBLE);
            recordingName.setText(name);
        } else {
            recordingName.setVisibility(View.GONE);
        }
    }

    private void setArtistName() {
        List<Artist.ArtistCredit> artistCredits = recording.getArtistCredits();
        List<String> names = new ArrayList<>();
        for (Artist.ArtistCredit artistCredit : artistCredits) {
            names.add(artistCredit.getName());
        }
        String artistNamesString = ApiUtils.getStringFromList(names, ", ");
        if (!TextUtils.isEmpty(artistNamesString)) {
            artistNameView.setVisibility(View.VISIBLE);
            artistNameView.setText(artistNamesString);
        } else {
            artistNameView.setVisibility(View.GONE);
        }
    }

    private void setLength() {
        Long len = recording.getLength();
        if (len != null) {
            length.setVisibility(View.VISIBLE);
            length.setText(MbUtils.formatTime(len));
        } else {
            length.setVisibility(View.GONE);
        }
    }

}
