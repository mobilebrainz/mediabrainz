package app.mediabrainz.communicator;

import app.mediabrainz.api.model.ReleaseGroup;


public interface GetReleaseGroupCommunicator {
    ReleaseGroup getReleaseGroup();
    String getReleaseGroupMbid();
}
