package app.mediabrainz.communicator;

import app.mediabrainz.api.model.Recording;


public interface GetRecordingCommunicator {
    Recording getRecording();
    String getRecordingMbid();
}
