package app.mediabrainz.communicator;

import java.util.List;

import app.mediabrainz.api.model.ReleaseGroup;


public interface GetReleaseGroupsCommunicator {
    List<ReleaseGroup> getReleaseGroups();
}
