package app.mediabrainz.api.model.xml;

import org.simpleframework.xml.Root;


@Root(name="artist")
public class ArtistXML extends BaseTagRatingEntityXML {
    public ArtistXML(String id) {
        super(id);
    }

    public ArtistXML(String id, int userRating) {
        super(id, userRating);
    }
}

