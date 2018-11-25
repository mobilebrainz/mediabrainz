package app.mediabrainz.communicator;

import app.mediabrainz.api.model.Tag;

import java.util.List;


public interface GetTagsCommunicator {

    List<Tag> getTags();
}
