package app.mediabrainz.adapter.pager;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import app.mediabrainz.R;
import app.mediabrainz.api.model.Tag;
import app.mediabrainz.fragment.EditTagsTabFragment;


public class EditTagsPagerAdapter extends BaseFragmentPagerAdapter {

    public enum TagsTab {
        GENRES(Tag.TagType.GENRE, R.string.tags_tab_genres),
        TAGS(Tag.TagType.TAG, R.string.tags_tab_tags);

        private final Tag.TagType type;
        private int title;

        TagsTab(Tag.TagType type, int title) {
            this.type = type;
            this.title = title;
        }

        public String getType() {
            return type.toString();
        }

        public int getTitle() {
            return title;
        }

        public Fragment createFragment() {
            return EditTagsTabFragment.newInstance(ordinal());
        }
    }

    private TagsTab[] tagsTabs = TagsTab.values();

    public EditTagsPagerAdapter(FragmentManager fm, Resources resources) {
        super(TagsTab.values().length, fm, resources);
        for (int i = 0; i < tagsTabs.length; ++i) {
            tabTitles[i] = tagsTabs[i].getTitle();
        }
    }

    @Override
    public Fragment getItem(int position) {
        return tagsTabs.length > position ? tagsTabs[position].createFragment() : null;
    }

}
