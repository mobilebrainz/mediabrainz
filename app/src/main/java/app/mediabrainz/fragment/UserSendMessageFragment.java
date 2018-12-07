package app.mediabrainz.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.MultiAutoCompleteTextView;

import app.mediabrainz.R;
import app.mediabrainz.communicator.GetUsernameCommunicator;
import app.mediabrainz.communicator.ShowTitleCommunicator;
import app.mediabrainz.util.ShowUtil;

import static app.mediabrainz.MediaBrainzApp.api;


public class UserSendMessageFragment extends LazyFragment {

    private String username;
    private boolean isLoading;

    private View loading;
    private View contentFrame;
    private AutoCompleteTextView subjectView;
    private MultiAutoCompleteTextView messageView;
    private CheckBox revealEmailView;

    public static UserSendMessageFragment newInstance() {
        Bundle args = new Bundle();
        UserSendMessageFragment fragment = new UserSendMessageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_user_send_message, container, false);

        loading = layout.findViewById(R.id.loading);
        contentFrame = layout.findViewById(R.id.content_frame);
        subjectView = layout.findViewById(R.id.subject);
        messageView = layout.findViewById(R.id.message);
        revealEmailView = layout.findViewById(R.id.reveal_email_checkbox);

        Button sendBtn = layout.findViewById(R.id.send_btn);
        sendBtn.setOnClickListener(this::send);

        loadView();
        return layout;
    }

    @Override
    protected void lazyLoad() {
        viewProgressLoading(false);
        username = ((GetUsernameCommunicator) getContext()).getUsername();
        if (username != null) {
            ((ShowTitleCommunicator) getContext()).getBottomTitle().setText(getString(R.string.send_email_to_title, username));
        }
    }

    private void send(View view) {
        if (isLoading) return;

        String subject = subjectView.getText().toString().trim();
        String message = messageView.getText().toString().trim();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(subject)) {
            subjectView.setError(getString(R.string.error_field_required));
            focusView = subjectView;
            cancel = true;
        }
        if (TextUtils.isEmpty(message)) {
            messageView.setError(getString(R.string.error_field_required));
            focusView = messageView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            viewProgressLoading(true);
            api.sendEmail(
                    username, subject, message, revealEmailView.isChecked(),
                    responseBody -> {
                        viewProgressLoading(false);
                        ShowUtil.showToast(getContext(), getString(R.string.send_email_success));

                    },
                    t -> {
                        viewProgressLoading(false);
                        ShowUtil.showToast(getContext(), getString(R.string.connection_error));
                    });
        }
    }

    private void viewProgressLoading(boolean isView) {
        if (isView) {
            isLoading = true;
            contentFrame.setAlpha(0.25f);
            loading.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            contentFrame.setAlpha(1.0f);
            loading.setVisibility(View.GONE);
        }
    }

}
