package app.mediabrainz.viewModels;

import app.mediabrainz.api.model.Release;
import app.mediabrainz.util.event.EventLiveData;

import static app.mediabrainz.MediaBrainzApp.api;


public class UserActivityVM extends BaseViewModel {

    private String username;
    private String releaseGroupMbid;
    public final EventLiveData<Resource<Release.ReleaseBrowse>> releasesResource = new EventLiveData<>();

    public void loadReleases(String releaseGroupMbid) {
        this.releaseGroupMbid = releaseGroupMbid;
        releasesResource.setData(Resource.loading());
        compositeDisposable.add(api.getReleasesByAlbum(
                releaseGroupMbid,
                releaseBrowse -> releasesResource.postEvent(Resource.success(releaseBrowse)),
                throwable -> releasesResource.postEvent(Resource.error(throwable)),
                2, 0));
    }

    public String getReleaseGroupMbid() {
        return releaseGroupMbid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
