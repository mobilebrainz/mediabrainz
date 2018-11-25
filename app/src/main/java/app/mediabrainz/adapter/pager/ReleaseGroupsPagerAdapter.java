package app.mediabrainz.adapter.pager;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import app.mediabrainz.R;
import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.fragment.ReleaseGroupsTabFragment;

import static app.mediabrainz.api.model.ReleaseGroup.PrimaryType.ALBUM;
import static app.mediabrainz.api.model.ReleaseGroup.PrimaryType.EP;
import static app.mediabrainz.api.model.ReleaseGroup.PrimaryType.SINGLE;
import static app.mediabrainz.api.model.ReleaseGroup.SecondaryType.COMPILATION;
import static app.mediabrainz.api.model.ReleaseGroup.SecondaryType.LIVE;


public class ReleaseGroupsPagerAdapter extends BaseFragmentPagerAdapter {

    public enum ReleaseTab {
        ALBUMS(ALBUM, R.string.release_group_albums),
        EPS(EP, R.string.release_group_eps),
        SINGLES(SINGLE, R.string.release_group_singles),
        LIVES(LIVE, R.string.release_group_lives),
        COMPILATIONS(COMPILATION, R.string.release_group_compilations);

        private final ReleaseGroup.AlbumType type;
        private int title;

        ReleaseTab(ReleaseGroup.AlbumType type, int title) {
            this.type = type;
            this.title = title;
        }

        public ReleaseGroup.AlbumType getAlbumType() {
            return type;
        }

        public String getType() {
            return type.toString();
        }

        public int getTitle() {
            return title;
        }

        public Fragment createFragment() {
            return ReleaseGroupsTabFragment.newInstance(ordinal());
        }
    }

    private ReleaseTab[] releaseTabs = ReleaseTab.values();

    public ReleaseGroupsPagerAdapter(FragmentManager fm, Resources resources) {
        super(ReleaseTab.values().length, fm, resources);
        for (int i = 0; i < releaseTabs.length; ++i) {
            tabTitles[i] = releaseTabs[i].getTitle();
        }
    }

    @Override
    public Fragment getItem(int position) {
        return releaseTabs.length > position ? releaseTabs[position].createFragment() : null;
    }

}
