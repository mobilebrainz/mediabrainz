package app.mediabrainz.adapter.pager;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import app.mediabrainz.R;
import app.mediabrainz.api.site.RatingServiceInterface;
import app.mediabrainz.fragment.UserRatingsTabFragment;

import static app.mediabrainz.api.site.RatingServiceInterface.RatingType;


public class UserRatingsPagerAdapter extends BaseFragmentPagerAdapter {

    public enum RatingsTab {
        ARTIST(RatingType.ARTIST, R.string.ratings_tab_artists, R.drawable.ic_artist_24_dark),
        RELEASE_GROUP(RatingType.RELEASE_GROUP, R.string.ratings_tab_releases, R.drawable.ic_album_24_dark),
        RECORDING(RatingType.RECORDING, R.string.ratings_tab_recordings, R.drawable.ic_track_24_dark);

        private final RatingServiceInterface.RatingType type;
        private int title;
        private int icon;

        RatingsTab(RatingServiceInterface.RatingType type, int title, int icon) {
            this.type = type;
            this.title = title;
            this.icon = icon;
        }

        public String getType() {
            return type.toString();
        }

        public int getTitle() {
            return title;
        }

        public int getIcon() {
            return icon;
        }

        public Fragment createFragment() {
            return UserRatingsTabFragment.newInstance(ordinal());
        }
    }

    private RatingsTab[] ratingsTabs = RatingsTab.values();

    public UserRatingsPagerAdapter(FragmentManager fm, Resources resources) {
        super(RatingsTab.values().length, fm, resources);
        for (int i = 0; i < ratingsTabs.length; ++i) {
            tabTitles[i] = ratingsTabs[i].getTitle();
            tabIcons[i] = ratingsTabs[i].getIcon();
        }
    }

    @Override
    public Fragment getItem(int position) {
        return ratingsTabs.length > position ? ratingsTabs[position].createFragment() : null;
    }

}
