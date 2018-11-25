package app.mediabrainz.api.model.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Root(name="metadata")
@Namespace(reference="http://musicbrainz.org/ns/mmd-2.0#")
public class Metadata {

    @Element(name="user-rating", required=false)
    private Integer userRating;

    @Element(name="message", required=false)
    private MessageXML message;

    @ElementList(name="user-tag-list", required=false)
    private List<UserTagXML> userTags;

    @ElementList(name="artist-list", required=false)
    private List<ArtistXML> artists;

    @ElementList(name="recording-list", required=false)
    private List<RecordingXML> recordings;

    @ElementList(name="release-group-list", required=false)
    private List<ReleaseXML> releases;

    @ElementList(name="release-list", required=false)
    private List<ReleaseGroupXML> releaseGroups;

    public Metadata() {
    }

    public Metadata addArtists(ArtistXML... artists) {
        if (this.artists == null) this.artists = new ArrayList<>();
        this.artists.addAll(Arrays.asList(artists));
        return this;
    }

    public Metadata addRecordings(RecordingXML... recordings) {
        if (this.recordings == null) this.recordings = new ArrayList<>();
        this.recordings.addAll(Arrays.asList(recordings));
        return this;
    }

    public Metadata addReleaseGroups(ReleaseGroupXML... releaseGroups) {
        if (this.releaseGroups == null) this.releaseGroups = new ArrayList<>();
        this.releaseGroups.addAll(Arrays.asList(releaseGroups));
        return this;
    }

    public Metadata addReleases(ReleaseXML... releases) {
        if (this.releases == null) this.releases = new ArrayList<>();
        this.releases.addAll(Arrays.asList(releases));
        return this;
    }

    public List<ArtistXML> getArtists() {
        return artists;
    }

    public List<RecordingXML> getRecordings() {
        return recordings;
    }

    public void setArtists(List<ArtistXML> artists) {
        this.artists = artists;
    }

    public void setRecordings(List<RecordingXML> recordings) {
        this.recordings = recordings;
    }

    public List<ReleaseGroupXML> getReleaseGroups() {
        return releaseGroups;
    }

    public void setReleaseGroups(List<ReleaseGroupXML> releaseGroups) {
        this.releaseGroups = releaseGroups;
    }

    public List<ReleaseXML> getReleases() {
        return releases;
    }

    public void setReleases(List<ReleaseXML> releases) {
        this.releases = releases;
    }

    public Integer getUserRating() {
        return userRating;
    }

    public void setUserRating(Integer userRating) {
        this.userRating = userRating;
    }

    public List<UserTagXML> getUserTags() {
        return userTags;
    }

    public void setUserTags(List<UserTagXML> userTags) {
        this.userTags = userTags;
    }

    public MessageXML getMessage() {
        return message;
    }

    public void setMessage(MessageXML message) {
        this.message = message;
    }
}
