package app.mediabrainz.api.model.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root(name="message")
public class MessageXML {

    @Element(name="text")
    private String text;

    public MessageXML() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
