package app.mediabrainz.adapter.pager;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import app.mediabrainz.R;
import app.mediabrainz.fragment.LinksTabFragment;
import app.mediabrainz.fragment.ReleaseCoverArtFragment;
import app.mediabrainz.fragment.ReleaseCreditsFragment;
import app.mediabrainz.fragment.ReleaseInfoTabFragment;


public class ReleaseInfoPagerAdapter extends BaseFragmentPagerAdapter {

    public static final int PAGE_COUNT = 4;
    public final static int TAB_BIO_POS = 0;
    public final static int TAB_CREDITS_POS = 1;
    public final static int TAB_COVER_ARTS_POS = 2;
    public final static int TAB_LINKS_POS = 3;

    public ReleaseInfoPagerAdapter(FragmentManager fm, Resources resources) {
        super(PAGE_COUNT, fm, resources);

        tabTitles[TAB_BIO_POS] = R.string.release_info_tab_bio;
        tabTitles[TAB_CREDITS_POS] = R.string.release_info_tab_credits;
        tabTitles[TAB_COVER_ARTS_POS] = R.string.release_info_tab_cover_arts;
        tabTitles[TAB_LINKS_POS] = R.string.release_info_tab_links;

        tabIcons[TAB_BIO_POS] = R.drawable.ic_wiki_24_dark;
        tabIcons[TAB_CREDITS_POS] = R.drawable.ic_artist_relations_24_dark;
        tabIcons[TAB_COVER_ARTS_POS] = R.drawable.ic_image_24_dark;
        tabIcons[TAB_LINKS_POS] = R.drawable.ic_link_24_dark;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case TAB_BIO_POS:
                return ReleaseInfoTabFragment.newInstance();
            case TAB_CREDITS_POS:
                return ReleaseCreditsFragment.newInstance();
            case TAB_COVER_ARTS_POS:
                return ReleaseCoverArtFragment.newInstance();
            case TAB_LINKS_POS:
                return LinksTabFragment.newInstance();
        }
        return null;
    }

}
