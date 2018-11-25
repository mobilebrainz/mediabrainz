package app.mediabrainz.api.model.xml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;


@Root(name="isrc")
public class IsrcXML {

    @Attribute(name="id")
    private String id;

    public IsrcXML(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
