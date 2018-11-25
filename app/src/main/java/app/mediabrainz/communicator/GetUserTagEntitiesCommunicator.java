package app.mediabrainz.communicator;

import java.util.List;

import app.mediabrainz.api.site.TagEntity;
import app.mediabrainz.api.site.TagServiceInterface;


public interface GetUserTagEntitiesCommunicator {

    List<TagEntity> getEntities(TagServiceInterface.UserTagType userTagType);

}
