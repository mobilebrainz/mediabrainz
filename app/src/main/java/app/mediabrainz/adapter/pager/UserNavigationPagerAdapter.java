package app.mediabrainz.adapter.pager;

import android.content.res.Resources;
import android.support.v4.app.FragmentManager;

import app.mediabrainz.fragment.CollectionsPagerFragment;
import app.mediabrainz.fragment.LazyFragment;
import app.mediabrainz.fragment.UserProfileFragment;
import app.mediabrainz.fragment.UserRatingsPagerFragment;
import app.mediabrainz.fragment.UserRecommendsPagerFragment;
import app.mediabrainz.fragment.UserTagsPagerFragment;


public class UserNavigationPagerAdapter extends BaseFragmentPagerAdapter {

    public static final int PAGE_COUNT = 5;
    public static final int TAB_PROFILE_POS = 0;
    public static final int TAB_COLLECTIONS_POS = 1;
    public static final int TAB_RATINGS_POS = 2;
    public static final int TAB_TAGS_POS = 3;
    public static final int TAB_RECOMMENDS_POS = 4;

    public UserNavigationPagerAdapter(FragmentManager fm, Resources resources) {
        super(PAGE_COUNT, fm, resources);
    }

    @Override
    public LazyFragment getItem(int position) {
        switch (position) {
            case TAB_PROFILE_POS:
                return UserProfileFragment.newInstance();
            case TAB_COLLECTIONS_POS:
                return CollectionsPagerFragment.newInstance();
            case TAB_RATINGS_POS:
                return UserRatingsPagerFragment.newInstance();
            case TAB_TAGS_POS:
                return UserTagsPagerFragment.newInstance();
            case TAB_RECOMMENDS_POS:
                return UserRecommendsPagerFragment.newInstance();
        }
        return null;
    }

}
