package app.mediabrainz.communicator;

import java.util.List;

import app.mediabrainz.api.model.Collection;


public interface GetCollectionsCommunicator {
    List<Collection> getCollections();
}
