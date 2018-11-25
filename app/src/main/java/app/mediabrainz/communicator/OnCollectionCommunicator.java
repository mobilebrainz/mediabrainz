package app.mediabrainz.communicator;

import app.mediabrainz.api.model.Collection;


public interface OnCollectionCommunicator {
    void onCollection(Collection collection);
}
