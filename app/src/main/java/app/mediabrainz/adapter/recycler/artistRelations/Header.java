package app.mediabrainz.adapter.recycler.artistRelations;

import app.mediabrainz.adapter.recycler.expandedRecycler.BaseHeader;


public class Header extends BaseHeader {

    private String title;
    private String description;

    public Header() {
        super();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
