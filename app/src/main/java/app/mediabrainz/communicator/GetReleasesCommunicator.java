package app.mediabrainz.communicator;

import java.util.List;

import app.mediabrainz.api.model.Release;


public interface GetReleasesCommunicator {
    List<Release> getReleases();
}
