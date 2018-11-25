package app.mediabrainz.communicator;

import app.mediabrainz.api.model.Collection;


public interface GetCollectionCommunicator {
    Collection getCollection();

    String getCollectionMbid();
}
