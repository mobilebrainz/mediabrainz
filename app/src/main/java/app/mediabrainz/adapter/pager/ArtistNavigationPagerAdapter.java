package app.mediabrainz.adapter.pager;

import android.content.res.Resources;
import android.support.v4.app.FragmentManager;

import app.mediabrainz.fragment.ArtistInfoPagerFragment;
import app.mediabrainz.fragment.ArtistRatingsFragment;
import app.mediabrainz.fragment.EditTagsPagerFragment;
import app.mediabrainz.fragment.LazyFragment;
import app.mediabrainz.fragment.ReleaseGroupsPagerFragment;

import static app.mediabrainz.fragment.EditTagsPagerFragment.TagsPagerType.ARTIST;


public class ArtistNavigationPagerAdapter extends BaseFragmentPagerAdapter {

    public static final int PAGE_COUNT = 4;
    public static final int TAB_RELEASES_POS = 0;
    public static final int TAB_INFO_POS = 1;
    public static final int TAB_RATINGS_POS = 2;
    public static final int TAB_TAGS_POS = 3;

    public ArtistNavigationPagerAdapter(FragmentManager fm, Resources resources) {
        super(PAGE_COUNT, fm, resources);
    }

    @Override
    public LazyFragment getItem(int position) {
        switch (position) {
            case TAB_RELEASES_POS:
                return ReleaseGroupsPagerFragment.newInstance();
            case TAB_INFO_POS:
                return ArtistInfoPagerFragment.newInstance();
            case TAB_RATINGS_POS:
                return ArtistRatingsFragment.newInstance();
            case TAB_TAGS_POS:
                return EditTagsPagerFragment.newInstance(ARTIST.ordinal());
        }
        return null;
    }

}
