package app.mediabrainz.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import app.mediabrainz.R;
import app.mediabrainz.communicator.GetUsernameCommunicator;
import app.mediabrainz.util.StringFormat;

import static app.mediabrainz.MediaBrainzApp.api;


public class UserProfileFragment extends LazyFragment {

    private String username;
    private boolean isLoading;
    private boolean isError;

    private View errorView;
    private View progressView;
    private ProgressBar avatarLoadingView;
    private FrameLayout avatarFrameView;
    private ImageView avatarView;
    private TextView userTypeView;
    private TextView ageView;
    private TextView genderView;
    private TextView locationView;
    private TextView memberSinceView;
    private TextView homepageView;
    private TextView languagesView;
    private TextView bioView;

    public static UserProfileFragment newInstance() {
        Bundle args = new Bundle();
        UserProfileFragment fragment = new UserProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_user_profile, container, false);

        errorView = layout.findViewById(R.id.errorView);
        progressView = layout.findViewById(R.id.progressView);
        avatarLoadingView = layout.findViewById(R.id.avatarLoadingView);
        avatarFrameView = layout.findViewById(R.id.avatarFrameView);
        avatarView = layout.findViewById(R.id.avatarView);
        userTypeView = layout.findViewById(R.id.userTypeView);
        ageView = layout.findViewById(R.id.ageView);
        genderView = layout.findViewById(R.id.genderView);
        locationView = layout.findViewById(R.id.locationView);
        memberSinceView = layout.findViewById(R.id.memberSinceView);
        homepageView = layout.findViewById(R.id.homepageView);
        languagesView = layout.findViewById(R.id.languagesView);
        bioView = layout.findViewById(R.id.bioView);

        loadView();
        return layout;
    }

    @Override
    protected void lazyLoad() {
        viewError(false);
        viewProgressLoading(false);
        username = ((GetUsernameCommunicator) getContext()).getUsername();
        if (username != null) {
            viewProgressLoading(true);
            api.getUserProfile(username,
                    userProfile -> {
                        viewProgressLoading(false);
                        if (!TextUtils.isEmpty(userProfile.getUserType())) {
                            userTypeView.setVisibility(View.VISIBLE);
                            userTypeView.setText(getResources().getString(R.string.user_profile_user_type, userProfile.getUserType()));
                        }
                        if (!TextUtils.isEmpty(userProfile.getAge())) {
                            ageView.setVisibility(View.VISIBLE);
                            ageView.setText(getResources().getString(R.string.user_profile_age, userProfile.getAge()));
                        }
                        if (!TextUtils.isEmpty(userProfile.getGender())) {
                            genderView.setVisibility(View.VISIBLE);
                            genderView.setText(getResources().getString(R.string.user_profile_gender, userProfile.getGender()));
                        }
                        if (!TextUtils.isEmpty(userProfile.getMemberSince())) {
                            memberSinceView.setVisibility(View.VISIBLE);
                            memberSinceView.setText(getResources().getString(R.string.user_profile_member_since, userProfile.getMemberSince()));
                        }
                        if (!TextUtils.isEmpty(userProfile.getHomepage()) && !userProfile.getHomepage().contains("content is hidden")) {
                            homepageView.setVisibility(View.VISIBLE);
                            homepageView.setText(getResources().getString(R.string.user_profile_homepage, userProfile.getHomepage()));
                        }
                        if (!TextUtils.isEmpty(userProfile.getBio())) {
                            bioView.setVisibility(View.VISIBLE);
                            bioView.setText(getResources().getString(R.string.user_profile_bio, userProfile.getBio()));
                        }
                        List<String> areas = userProfile.getAreas();
                        if (!areas.isEmpty()) {
                            locationView.setVisibility(View.VISIBLE);
                            locationView.setText(getResources().getString(R.string.user_profile_location, StringFormat.join(", ", areas)));
                        }
                        List<String> languages = userProfile.getLanguages();
                        if (!languages.isEmpty()) {
                            languagesView.setVisibility(View.VISIBLE);
                            languagesView.setText(getResources().getString(R.string.user_profile_languages, StringFormat.join(", ", languages)));
                        }

                        if (!userProfile.getGravatar().contains("https://gravatar.com/avatar/placeholder?d=mm&s=108")) {
                            avatarFrameView.setVisibility(View.VISIBLE);
                            avatarLoadingView.setVisibility(View.VISIBLE);
                            Picasso.get().load(userProfile.getGravatar()).fit().centerInside().into(avatarView,
                                    new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            avatarLoadingView.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            avatarFrameView.setVisibility(View.GONE);
                                        }
                                    });
                        }
                    },
                    this::showConnectionWarning);
        }
    }

    private void viewProgressLoading(boolean isView) {
        if (isView) {
            isLoading = true;
            progressView.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            progressView.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean isView) {
        if (isView) {
            isError = true;
            errorView.setVisibility(View.VISIBLE);
        } else {
            isError = false;
            errorView.setVisibility(View.GONE);
        }
    }

    private void showConnectionWarning(Throwable t) {
        //ShowUtil.showError(getContext(), t);
        viewProgressLoading(false);
        viewError(true);
        errorView.findViewById(R.id.retryButton).setOnClickListener(v -> lazyLoad());
    }

}
