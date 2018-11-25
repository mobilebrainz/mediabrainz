package app.mediabrainz.api.model;

import com.squareup.moshi.Json;

import java.util.List;
import java.util.Objects;

/**
 * https://musicbrainz.org/doc/Folksonomy_Tagging
 */

public class Tag {

    public enum TagType {
        GENRE, TAG
    }

    public static class TagSearch extends BaseSearch {
        @Json(name = "tags")
        private List<Tag> tags;

        public List<Tag> getTags() {
            return tags;
        }

        public void setTags(List<Tag> tags) {
            this.tags = tags;
        }
    }

    @Json(name = "name")
    private String name;

    @Json(name = "count")
    private int count;

    @Json(name = "score")
    private int score;

    public Tag() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(getName(), tag.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
