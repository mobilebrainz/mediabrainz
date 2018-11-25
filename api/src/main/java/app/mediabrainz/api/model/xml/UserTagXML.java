package app.mediabrainz.api.model.xml;


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root(name="user-tag")
public class UserTagXML {

    public enum VoteType {
        UPVOTE("upvote"),
        DOWNVOTE("downvote"),
        WITHDRAW("withdraw");

        private final String type;
        VoteType(String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type;
        }
    }

    @Element(name="name")
    private String name;

    @Attribute(name="vote", required=false)
    private String vote;

    public UserTagXML() {
    }

    public UserTagXML(String name) {
        this.name = name;
    }

    public UserTagXML(String name, VoteType voteType) {
        this.name = name;
        this.vote = voteType.toString();
    }

    public String getName() {
        return name;
    }

    public String getVote() {
        return vote;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }
}
