package app.mediabrainz.communicator;

import app.mediabrainz.api.model.Artist;


public interface GetArtistCommunicator {
    Artist getArtist();
    String getArtistMbid();
}
