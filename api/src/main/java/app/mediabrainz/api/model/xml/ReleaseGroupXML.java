package app.mediabrainz.api.model.xml;


import org.simpleframework.xml.Root;


@Root(name="release-group")
public class ReleaseGroupXML extends BaseTagRatingEntityXML {
    public ReleaseGroupXML(String id) {
        super(id);
    }

    public ReleaseGroupXML(String id, int userRating) {
        super(id, userRating);
    }
}
