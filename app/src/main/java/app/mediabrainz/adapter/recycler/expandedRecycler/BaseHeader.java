package app.mediabrainz.adapter.recycler.expandedRecycler;


public abstract class BaseHeader {

    private boolean visible = true;
    private boolean expand = false;
    private int position;
    private int size;

    public abstract String getTitle();

    public BaseHeader() {}

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
