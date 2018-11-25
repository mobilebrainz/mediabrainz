package app.mediabrainz.communicator;

import app.mediabrainz.api.model.Work;


public interface GetWorkCommunicator {
    Work getWork();

    String getWorkMbid();
}
