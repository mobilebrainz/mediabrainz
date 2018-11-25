package app.mediabrainz.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import app.mediabrainz.R;
import app.mediabrainz.api.oauth.OAuthException;
import app.mediabrainz.util.ShowUtil;

import static app.mediabrainz.MediaBrainzApp.oauth;


public class LoginActivity extends BaseActivity {

    private final String CREATE_ACCOUNT_URI = "https://musicbrainz.org/register";
    private final String FORGOT_USERNAME_URI = "https://musicbrainz.org/lost-username";
    private final String FORGOT_PASSWORD_URI = "https://musicbrainz.org/lost-password";

    private EditText usernameView;
    private EditText passwordView;
    private View progressView;
    private View loginFormView;

    @Override
    protected int initContentLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.login_progress);
        usernameView = findViewById(R.id.username);
        passwordView = findViewById(R.id.password);
        passwordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        Button signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(view -> attemptLogin());

        Button createAccountButton = findViewById(R.id.create_account_button);
        createAccountButton.setOnClickListener(
                v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(CREATE_ACCOUNT_URI))));

        Button forgotUsernameButton = findViewById(R.id.forgot_username_button);
        forgotUsernameButton.setOnClickListener(
                v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(FORGOT_USERNAME_URI))));

        Button forgotPasswordButton = findViewById(R.id.forgot_password_button);
        forgotPasswordButton.setOnClickListener(
                v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(FORGOT_PASSWORD_URI))));
    }

    private void attemptLogin() {
        if (progressView.getVisibility() == View.VISIBLE) {
            return;
        }
        usernameView.setError(null);
        passwordView.setError(null);

        String username = usernameView.getText().toString().trim();
        String password = passwordView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(username)) {
            usernameView.setError(getString(R.string.error_field_required));
            focusView = usernameView;
            cancel = true;
        }
        if (TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_field_required));
            focusView = passwordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            oauth.authorize(
                    username, password,
                    () -> {
                        showProgress(false);
                        finish();
                    },
                    t -> {
                        showProgress(false);
                        if (t.equals(OAuthException.INVALID_AUTENTICATION_ERROR)) {
                            usernameView.setError(getString(R.string.error_invalid_username));
                            passwordView.setError(getString(R.string.error_invalid_password));
                        } else {
                            ShowUtil.showError(this, t);
                        }
                    });
        }
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        loginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                    }
                });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });
    }

}

