package app.mediabrainz.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import app.mediabrainz.R;
import app.mediabrainz.api.model.Collection;
import app.mediabrainz.communicator.GetCollectionsCommunicator;


public class CreateCollectionDialogFragment extends DialogFragment {

    public static final String TAG = "CreateCollectionDialogFragment";

    public interface DialogFragmentListener {
        void onCreateCollection(String name, String description, int publ);
    }

    private List<Collection> collections = new ArrayList<>();

    private EditText nameEditText;
    private EditText descriptionEditText;
    private CheckBox publicCheckBox;
    private Button createButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.dialog_fragment_collection_create, container, false);

        nameEditText = layout.findViewById(R.id.collection_name);
        descriptionEditText = layout.findViewById(R.id.collection_description);
        publicCheckBox = layout.findViewById(R.id.collection_public);

        createButton = layout.findViewById(R.id.collection_create_btn);
        createButton.setOnClickListener(v -> create());
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        collections = ((GetCollectionsCommunicator) getContext()).getCollections();
    }

    private void create() {
        nameEditText.setError(null);
        String name = nameEditText.getText().toString().trim();

        if (!TextUtils.isEmpty(name)) {
            boolean existName = false;
            if (collections != null) {
                for (Collection collection : collections) {
                    if (collection.getName().equalsIgnoreCase(name)) {
                        existName = true;
                        nameEditText.setError(getString(R.string.collection_create_exist_name));
                        break;
                    }
                }
            }
            if (!existName) {
                ((DialogFragmentListener) getContext()).onCreateCollection(
                        name,
                        descriptionEditText.getText().toString(),
                        publicCheckBox.isChecked() ? 1 : 0);
                dismiss();
            }
        }
    }

}
