package app.mediabrainz.adapter.pager;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import app.mediabrainz.R;
import app.mediabrainz.api.site.TagServiceInterface.TagType;
import app.mediabrainz.fragment.UserRecommendsTabFragment;


public class UserRecommendsPagerAdapter extends BaseFragmentPagerAdapter {

    public enum RecommendsTab {
        ARTIST(TagType.ARTIST, R.string.ratings_tab_artists, R.drawable.ic_artist_24_dark),
        RELEASE_GROUP(TagType.RELEASE_GROUP, R.string.ratings_tab_releases, R.drawable.ic_album_24_dark),
        RECORDING(TagType.RECORDING, R.string.ratings_tab_recordings, R.drawable.ic_track_24_dark);

        private final TagType type;
        private int title;
        private int icon;

        RecommendsTab(TagType type, int title, int icon) {
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
            return UserRecommendsTabFragment.newInstance(ordinal());
        }

    }

    private RecommendsTab[] recommendsTabs = RecommendsTab.values();

    public UserRecommendsPagerAdapter(FragmentManager fm, Resources resources) {
        super(RecommendsTab.values().length, fm, resources);
        for (int i = 0; i < recommendsTabs.length; ++i) {
            tabTitles[i] = recommendsTabs[i].getTitle();
            tabIcons[i] = recommendsTabs[i].getIcon();
        }
    }

    @Override
    public Fragment getItem(int position) {
        return recommendsTabs.length > position ? recommendsTabs[position].createFragment() : null;
    }

}
