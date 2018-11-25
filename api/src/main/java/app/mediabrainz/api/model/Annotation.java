package app.mediabrainz.api.model;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * https://musicbrainz.org/doc/Annotation
 */

public class Annotation {

    public static class AnnotationSearch extends BaseSearch {
        @Json(name = "annotations")
        private List<Annotation> annotations;

        public List<Annotation> getAnnotations() {
            return annotations;
        }

        public void setAnnotations(List<Annotation> annotations) {
            this.annotations = annotations;
        }
    }

    @Json(name = "entity")
    private String id;

    @Json(name = "type")
    private String type = "";

    @Json(name = "score")
    private int score;

    @Json(name = "name")
    private String name = "";

    @Json(name = "text")
    private String text = "";

    public Annotation() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
