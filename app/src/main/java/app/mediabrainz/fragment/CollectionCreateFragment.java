package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import app.mediabrainz.R;
import app.mediabrainz.communicator.OnCreateCollectionCommunicator;
import app.mediabrainz.communicator.ShowTitleCommunicator;


public class CollectionCreateFragment extends Fragment {

    private EditText nameEditText;
    private Spinner typeSpinner;
    private EditText descriptionEditText;
    private CheckBox publicCheckBox;

    public static CollectionCreateFragment newInstance() {
        Bundle args = new Bundle();
        CollectionCreateFragment fragment = new CollectionCreateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_collection_create, container, false);

        nameEditText = layout.findViewById(R.id.collection_name);
        descriptionEditText = layout.findViewById(R.id.collection_description);
        publicCheckBox = layout.findViewById(R.id.collection_public);

        Button createButton = layout.findViewById(R.id.collection_create_btn);
        createButton.setOnClickListener(v -> create());

        typeSpinner = layout.findViewById(R.id.collection_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.collection_type_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        ((ShowTitleCommunicator) getContext()).getTopTitle().setText(R.string.title_create_collection);

        return layout;
    }

    private void create() {
        nameEditText.setError(null);
        String name = nameEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(name)) {
            ((OnCreateCollectionCommunicator) getContext()).onCreateCollection(
                    name, typeSpinner.getSelectedItemPosition() + 1,
                    descriptionEditText.getText().toString(),
                    publicCheckBox.isChecked() ? 1 : 0,
                    nameEditText);
        }
    }

}
