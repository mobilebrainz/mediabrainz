package app.mediabrainz.communicator;

import java.util.List;

import app.mediabrainz.api.model.Url;


public interface GetUrlsCommunicator {
    List<Url> getUrls();
}
