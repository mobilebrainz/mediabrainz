package app.mediabrainz.api.model.xml;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Root(name="recording")
public class RecordingXML extends BaseTagRatingEntityXML {

    @ElementList(name="isrc-list", required=false)
    private List<IsrcXML> isrcs;

    public RecordingXML(String id) {
        super(id);
    }

    public RecordingXML(String id, int userRating) {
        super(id, userRating);
    }

    public void addIsrcs(IsrcXML... isrcs) {
        if (this.isrcs == null) this.isrcs = new ArrayList<>();
        this.isrcs.addAll(Arrays.asList(isrcs));
    }

    public List<IsrcXML> getIsrcs() {
        return isrcs;
    }

    public void setIsrcs(List<IsrcXML> isrcs) {
        this.isrcs = isrcs;
    }
}
