package app.mediabrainz.adapter.pager;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import app.mediabrainz.R;
import app.mediabrainz.fragment.LinksTabFragment;
import app.mediabrainz.fragment.RecordingCreditsFragment;
import app.mediabrainz.fragment.RecordingInfoTabFragment;


public class RecordingInfoPagerAdapter extends BaseFragmentPagerAdapter {

    public static final int PAGE_COUNT = 3;
    public final static int TAB_INFO_POS = 0;
    public final static int TAB_CREDITS_POS = 1;
    public final static int TAB_LINKS_POS = 2;

    public RecordingInfoPagerAdapter(FragmentManager fm, Resources resources) {
        super(PAGE_COUNT, fm, resources);

        tabTitles[TAB_INFO_POS] = R.string.recording_info_tab_info;
        tabTitles[TAB_CREDITS_POS] = R.string.recording_info_tab_credits;
        tabTitles[TAB_LINKS_POS] = R.string.artist_info_tab_links;

        tabIcons[TAB_INFO_POS] = R.drawable.ic_info_24_dark;
        tabIcons[TAB_CREDITS_POS] = R.drawable.ic_artist_relations_24_dark;
        tabIcons[TAB_LINKS_POS] = R.drawable.ic_link_24_dark;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case TAB_INFO_POS:
                return RecordingInfoTabFragment.newInstance();
            case TAB_CREDITS_POS:
                return RecordingCreditsFragment.newInstance();
            case TAB_LINKS_POS:
                return LinksTabFragment.newInstance();
        }
        return null;
    }

}
