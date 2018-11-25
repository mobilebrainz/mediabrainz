package app.mediabrainz.adapter.pager;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import app.mediabrainz.R;
import app.mediabrainz.api.site.TagServiceInterface.TagType;
import app.mediabrainz.fragment.TagTabFragment;


public class TagPagerAdapter extends BaseFragmentPagerAdapter {

    public enum TagTab {
        ARTIST(TagType.ARTIST, R.string.tag_tab_artists, R.drawable.ic_artist_24),
        RELEASE_GROUP(TagType.RELEASE_GROUP, R.string.tag_tab_releases, R.drawable.ic_album_24),
        RECORDING(TagType.RECORDING, R.string.tag_tab_recordings, R.drawable.ic_track_24);

        private final TagType type;
        private int title;
        private int icon;

        TagTab(TagType type, int title, int icon) {
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
            return TagTabFragment.newInstance(ordinal());
        }
    }

    private TagTab[] tagTabs = TagTab.values();

    public TagPagerAdapter(FragmentManager fm, Resources resources) {
        super(TagTab.values().length, fm, resources);
        for (int i = 0; i < tagTabs.length; ++i) {
            tabTitles[i] = tagTabs[i].getTitle();
            tabIcons[i] = tagTabs[i].getIcon();
        }
    }

    @Override
    public Fragment getItem(int position) {
        return tagTabs.length > position ? tagTabs[position].createFragment() : null;
    }

}
