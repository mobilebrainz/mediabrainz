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

    private EditText collectionNameView;
    private Spinner collectionTypeSpinner;
    private EditText collectionDescriptionView;
    private CheckBox collectionPublicCheckBox;

    public static CollectionCreateFragment newInstance() {
        Bundle args = new Bundle();
        CollectionCreateFragment fragment = new CollectionCreateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_collection_create, container, false);

        collectionNameView = layout.findViewById(R.id.collectionNameView);
        collectionDescriptionView = layout.findViewById(R.id.collectionDescriptionView);
        collectionPublicCheckBox = layout.findViewById(R.id.collectionPublicCheckBox);

        Button collectionCreateButton = layout.findViewById(R.id.collectionCreateButton);
        collectionCreateButton.setOnClickListener(v -> create());

        collectionTypeSpinner = layout.findViewById(R.id.collectionTypeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.collection_type_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        collectionTypeSpinner.setAdapter(adapter);

        ((ShowTitleCommunicator) getContext()).getToolbarTopTitleView().setText(R.string.title_create_collection);

        return layout;
    }

    private void create() {
        collectionNameView.setError(null);
        String name = collectionNameView.getText().toString().trim();
        if (!TextUtils.isEmpty(name)) {
            ((OnCreateCollectionCommunicator) getContext()).onCreateCollection(
                    name, collectionTypeSpinner.getSelectedItemPosition() + 1,
                    collectionDescriptionView.getText().toString(),
                    collectionPublicCheckBox.isChecked() ? 1 : 0,
                    collectionNameView);
        }
    }

}
