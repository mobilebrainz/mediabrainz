package app.mediabrainz.activity;

import app.mediabrainz.R;


public enum SearchType {

    USER(0, R.string.searchtype_user),
    TAG(1, R.string.searchtype_tag),
    BARCODE(2, R.string.searchtype_barcode);

    private final int id;
    private final int res;

    SearchType(int id, int res) {
        this.id = id;
        this.res = res;
    }

    public int getId() {
        return id;
    }

    public int getRes() {
        return res;
    }
}
