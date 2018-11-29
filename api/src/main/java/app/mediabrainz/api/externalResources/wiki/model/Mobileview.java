package app.mediabrainz.api.externalResources.wiki.model;

import com.squareup.moshi.Json;

import java.util.List;


public class Mobileview {

    @Json(name = "normalizedtitle")
    private String normalizedtitle;

    @Json(name = "sections")
    private List<Section> sections;

    public static class Section {

        @Json(name = "id")
        private Integer id;

        @Json(name = "toclevel")
        private Integer toclevel;

        @Json(name = "line")
        private String line;

        @Json(name = "text")
        private String text;

        public Section() {
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getToclevel() {
            return toclevel;
        }

        public void setToclevel(Integer toclevel) {
            this.toclevel = toclevel;
        }

        public String getLine() {
            return line;
        }

        public void setLine(String line) {
            this.line = line;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public Mobileview() {
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public String getNormalizedtitle() {
        return normalizedtitle;
    }

    public void setNormalizedtitle(String normalizedtitle) {
        this.normalizedtitle = normalizedtitle;
    }
}
