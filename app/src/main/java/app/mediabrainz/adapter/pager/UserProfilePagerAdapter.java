package app.mediabrainz.adapter.pager;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import app.mediabrainz.R;
import app.mediabrainz.fragment.UserProfileFragment;
import app.mediabrainz.fragment.UsersFragment;


public class UserProfilePagerAdapter extends UpdatableFragmentPagerAdapter {

    public static final int PAGE_COUNT = 2;
    public final static int TAB_PROFILE_POS = 0;
    public final static int TAB_USERS_POS = 1;

    public UserProfilePagerAdapter(FragmentManager fm, Resources resources) {
        super(PAGE_COUNT, fm, resources);
        tabTitles[TAB_PROFILE_POS] = R.string.user_profile_tab_profile;
        tabTitles[TAB_USERS_POS] = R.string.user_profile_tab_users;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case TAB_PROFILE_POS:
                return UserProfileFragment.newInstance();
            case TAB_USERS_POS:
                return UsersFragment.newInstance();
        }
        return null;
    }

}
