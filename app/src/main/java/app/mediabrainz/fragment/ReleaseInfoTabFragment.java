package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.mediabrainz.R;


public class ReleaseInfoTabFragment extends BaseFragment {

    public static ReleaseInfoTabFragment newInstance() {
        Bundle args = new Bundle();

        ReleaseInfoTabFragment fragment = new ReleaseInfoTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflate(R.layout.fragment_release_info_tab, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        insertNestedFragments();
    }

    private void insertNestedFragments() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.releaseInformationFragment, new ReleaseInformationFragment())
                .replace(R.id.wikipediaWebViewFragment, new WikipediaWebViewFragment())
                .commit();
    }

}
