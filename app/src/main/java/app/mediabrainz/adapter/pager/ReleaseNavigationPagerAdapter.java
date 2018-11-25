package app.mediabrainz.adapter.pager;

import android.content.res.Resources;
import android.support.v4.app.FragmentManager;

import app.mediabrainz.fragment.EditTagsPagerFragment;
import app.mediabrainz.fragment.LazyFragment;
import app.mediabrainz.fragment.ReleaseInfoPagerFragment;
import app.mediabrainz.fragment.ReleaseRatingsFragment;
import app.mediabrainz.fragment.ReleaseTracksFragment;
import app.mediabrainz.fragment.ReleasesFragment;

import static app.mediabrainz.fragment.EditTagsPagerFragment.TagsPagerType.RELEASE;


public class ReleaseNavigationPagerAdapter extends BaseFragmentPagerAdapter {

    public static final int PAGE_COUNT = 5;
    public static final int TAB_TRACKS_POS = 0;
    public static final int TAB_INFO_POS = 1;
    public static final int TAB_RELEASES_POS = 2;
    public static final int TAB_RATINGS_POS = 3;
    public static final int TAB_TAGS_POS = 4;

    public ReleaseNavigationPagerAdapter(FragmentManager fm, Resources resources) {
        super(PAGE_COUNT, fm, resources);
    }

    @Override
    public LazyFragment getItem(int position) {
        switch (position) {
            case TAB_TRACKS_POS:
                return ReleaseTracksFragment.newInstance();
            case TAB_INFO_POS:
                return ReleaseInfoPagerFragment.newInstance();
            case TAB_RELEASES_POS:
                return ReleasesFragment.newInstance(ReleasesFragment.ALBUM_TYPE);
            case TAB_RATINGS_POS:
                return ReleaseRatingsFragment.newInstance();
            case TAB_TAGS_POS:
                return EditTagsPagerFragment.newInstance(RELEASE.ordinal());
        }
        return null;
    }

}
