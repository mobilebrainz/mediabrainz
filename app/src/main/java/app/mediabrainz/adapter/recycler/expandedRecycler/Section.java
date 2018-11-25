package app.mediabrainz.adapter.recycler.expandedRecycler;

import java.util.ArrayList;
import java.util.List;


public class Section<T> {

    private BaseHeader header;
    private List<T> items = new ArrayList<>();

    public Section() {
    }

    public Section(BaseHeader header) {
        this.header = header;
    }

    public Section(BaseHeader header, List<T> items) {
        this.header = header;
        this.items = items;
    }

    public BaseHeader getHeader() {
        return header;
    }

    public void setHeader(BaseHeader header) {
        this.header = header;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
