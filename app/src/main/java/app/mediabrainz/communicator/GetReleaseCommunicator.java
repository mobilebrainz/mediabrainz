package app.mediabrainz.communicator;

import app.mediabrainz.api.model.Release;


public interface GetReleaseCommunicator {
    Release getRelease();
    String getReleaseMbid();
}
