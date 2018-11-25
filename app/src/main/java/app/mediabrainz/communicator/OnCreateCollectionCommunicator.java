package app.mediabrainz.communicator;

import android.widget.EditText;


public interface OnCreateCollectionCommunicator {
    void onCreateCollection(String name, int type, String description, int publ, EditText editText);
}
