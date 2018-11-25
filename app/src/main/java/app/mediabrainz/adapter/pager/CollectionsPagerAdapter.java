package app.mediabrainz.adapter.pager;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.List;

import app.mediabrainz.R;
import app.mediabrainz.fragment.CollectionsTabFragment;

import static app.mediabrainz.adapter.pager.CollectionsPagerAdapter.CollectionTab.*;


public class CollectionsPagerAdapter extends BaseFragmentPagerAdapter {

    public static final CollectionTab[] collectionTabTypeSpinner = {
            RELEASES, RELEASES, RELEASES,
            EVENTS, EVENTS, EVENTS,
            AREAS,
            ARTISTS,
            INSTRUMENTS,
            LABELS,
            PLACES,
            RECORDINGS,
            RELEASE_GROUPS,
            SERIES,
            WORKS
    };

    public enum CollectionTab {
        AREAS(R.string.collections_areas),
        ARTISTS(R.string.collections_artists),
        EVENTS(R.string.collections_events),
        INSTRUMENTS(R.string.collections_instruments),
        LABELS(R.string.collections_labels),
        PLACES(R.string.collections_places),
        RECORDINGS(R.string.collections_recordings),
        RELEASES(R.string.collections_releases),
        RELEASE_GROUPS(R.string.collections_release_groups),
        SERIES(R.string.collections_series),
        WORKS(R.string.collections_works);

        private final int title;

        CollectionTab(int title) {
            this.title = title;
        }

        public int getTitle() {
            return title;
        }

        public Fragment createFragmentPage() {
            return CollectionsTabFragment.newInstance(ordinal());
        }

    }

    private List<CollectionTab> collectionTabs;

    public CollectionsPagerAdapter(FragmentManager fm, Resources resources, List<CollectionTab> collectionTabs) {
        super(collectionTabs.size(), fm, resources);
        this.collectionTabs = collectionTabs;

        for (int i = 0; i < collectionTabs.size(); ++i) {
            tabTitles[i] = collectionTabs.get(i).getTitle();
        }
    }

    @Override
    public Fragment getItem(int position) {
        return collectionTabs.size() > position ? collectionTabs.get(position).createFragmentPage() : null;
    }

}
