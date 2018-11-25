package app.mediabrainz.api.model.xml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root(name="release")
public class ReleaseXML {

    @Attribute(name="id")
    private String id;

    @Element(name="barcode")
    private String barcode;

    public ReleaseXML(String id, String barcode) {
        this.id = id;
        this.barcode = barcode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
