package app.mediabrainz.api.model.xml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public abstract class BaseTagRatingEntityXML {

    @Attribute(name="id")
    protected String id;

    @Element(name="user-rating", required=false)
    protected int userRating;

    @ElementList(name="user-tag-list", required=false)
    protected List<UserTagXML> userTags;

    public BaseTagRatingEntityXML(String id) {
        this.id = id;
    }

    public BaseTagRatingEntityXML(String id, int userRating) {
        this.id = id;
        this.userRating = userRating;
    }

    public void addUserTags(UserTagXML... userTags) {
        if (this.userTags == null) this.userTags = new ArrayList<>();
        this.userTags.addAll(Arrays.asList(userTags));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUserRating() {
        return userRating;
    }

    public void setUserRating(int userRating) {
        this.userRating = userRating;
    }

    public List<UserTagXML> getUserTags() {
        return userTags;
    }

    public void setUserTags(List<UserTagXML> userTags) {
        this.userTags = userTags;
    }
}
