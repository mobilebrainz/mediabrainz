package app.mediabrainz.viewModels;


import android.arch.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Map;

import app.mediabrainz.api.site.TagEntity;
import app.mediabrainz.api.site.TagServiceInterface;

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.viewModels.Status.SUCCESS;


public class UserTagVM extends BaseViewModel {

    public MutableLiveData<Resource<Map<TagServiceInterface.UserTagType, List<TagEntity>>>> entitiesMapResource =
            new MutableLiveData<>();

    private Map<TagServiceInterface.UserTagType, List<TagEntity>> entitiesMap;

    /*
    //чтобы использовать, надо инвалидить entitiesMap после каждого теггирования артистов, релизов или зарисей, что
    //очень трудоёмко, а выгода минимальна
    public void lazyLoad(String username, String userTag) {
        Resource<Map<TagServiceInterface.UserTagType, List<TagEntity>>> resource = entitiesMapResource.getValue();
        if (!username.equals(this.username) || userTag.equals(this.userTag) ||
            resource == null || resource.getData() == null || resource.getStatus() != SUCCESS) {
            load(username, userTag);
        }
    }
    */

    public void load(String username, String userTag) {
        entitiesMapResource.setValue(Resource.loading());
        compositeDisposable.add(api.getUserTagEntities(username, userTag,
                map -> {
                    entitiesMap = map;
                    entitiesMapResource.postValue(Resource.success(map));
                },
                throwable -> entitiesMapResource.postValue(Resource.error(throwable))));
    }

    public Map<TagServiceInterface.UserTagType, List<TagEntity>> getEntitiesMap() {
        return entitiesMap;
    }

}
