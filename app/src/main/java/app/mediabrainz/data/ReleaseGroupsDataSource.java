package app.mediabrainz.data;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import app.mediabrainz.api.model.ReleaseGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.api.model.ReleaseGroup.PrimaryType.ALBUM;
import static app.mediabrainz.api.model.ReleaseGroup.PrimaryType.EMPTY;
import static app.mediabrainz.api.model.ReleaseGroup.PrimaryType.EP;
import static app.mediabrainz.api.model.ReleaseGroup.PrimaryType.SINGLE;
import static app.mediabrainz.api.model.ReleaseGroup.SecondaryType.COMPILATION;
import static app.mediabrainz.api.model.ReleaseGroup.SecondaryType.LIVE;


public class ReleaseGroupsDataSource extends PageKeyedDataSource<Integer, ReleaseGroup> {

    public static final int RELEASE_GROUPE_BROWSE_LIMIT = 50;

    private CompositeDisposable compositeDisposable;
    private ReleaseGroup.AlbumType albumType;
    private ReleaseGroup.PrimaryType primaryType;
    private ReleaseGroup.SecondaryType secondaryType;
    private List<ReleaseGroup> officialReleaseGroups = new ArrayList<>();
    private String artistMbid;

    private MutableLiveData<Boolean> mutableIsOfficial;
    private MutableLiveData<NetworkState> networkState = new MutableLiveData<>();
    private MutableLiveData<NetworkState> initialLoad = new MutableLiveData<>();
    private Completable retryCompletable;


    public ReleaseGroupsDataSource(CompositeDisposable compositeDisposable, String artistMbid, ReleaseGroup.AlbumType albumType, MutableLiveData<Boolean> mutableIsOfficial) {
        this.artistMbid = artistMbid;
        this.compositeDisposable = compositeDisposable;
        this.mutableIsOfficial = mutableIsOfficial;
        this.albumType = albumType;

        if (mutableIsOfficial.getValue()) {
            if (albumType.equals(ALBUM)) {
                primaryType = ALBUM;
                secondaryType = ReleaseGroup.SecondaryType.NOTHING;
            } else if (albumType.equals(EP)) {
                primaryType = EP;
                secondaryType = ReleaseGroup.SecondaryType.NOTHING;
            } else if (albumType.equals(SINGLE)) {
                primaryType = SINGLE;
                secondaryType = ReleaseGroup.SecondaryType.NOTHING;
            } else if (albumType.equals(LIVE)) {
                primaryType = EMPTY;
                secondaryType = ReleaseGroup.SecondaryType.LIVE;
            } else if (albumType.equals(COMPILATION)) {
                primaryType = EMPTY;
                secondaryType = ReleaseGroup.SecondaryType.COMPILATION;
            }
        }
    }

    public MutableLiveData<Boolean> getMutableIsOfficial() {
        return mutableIsOfficial;
    }

    public void retry() {
        if (retryCompletable != null) {
            compositeDisposable.add(retryCompletable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            () -> {
                            },
                            throwable -> {
                                //Timber.e(throwable.getMessage());
                            }));
        }
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, ReleaseGroup> callback) {
        networkState.postValue(NetworkState.LOADING);
        initialLoad.postValue(NetworkState.LOADING);

        compositeDisposable.add(api.getReleaseGroupsByArtistAndAlbumTypes(
                artistMbid,
                releaseGroupBrowse -> {
                    List<ReleaseGroup> releaseGroups = releaseGroupBrowse.getReleaseGroups();

                    if (!releaseGroups.isEmpty() && mutableIsOfficial.getValue()) {
                        compositeDisposable.add(api.searchOfficialReleaseGroups(
                                artistMbid,
                                releaseGroupSearch -> {
                                    List<ReleaseGroup> rgs = new ArrayList<>();
                                    if (releaseGroupSearch.getCount() > 0) {
                                        officialReleaseGroups = releaseGroupSearch.getReleaseGroups();

                                        setRetry(null);
                                        initialLoad.postValue(NetworkState.LOADED);

                                        rgs = selectOfficialReleaseGroups(releaseGroups);
                                        callback.onResult(rgs, null,
                                                (releaseGroupBrowse.getCount() > RELEASE_GROUPE_BROWSE_LIMIT) ? RELEASE_GROUPE_BROWSE_LIMIT : null);

                                    } else {
                                        callback.onResult(rgs, null, null);
                                    }

                                    networkState.postValue(NetworkState.LOADED);
                                },
                                t -> {},
                                100, 0,
                                primaryType, secondaryType));
                    } else {
                        setRetry(null);
                        initialLoad.postValue(NetworkState.LOADED);

                        List<ReleaseGroup> rgs = selectReleaseGroups(releaseGroups);

                        callback.onResult(rgs, null,
                                (releaseGroups.size() == rgs.size() && releaseGroupBrowse.getCount() > RELEASE_GROUPE_BROWSE_LIMIT) ? RELEASE_GROUPE_BROWSE_LIMIT : null);

                        networkState.postValue(NetworkState.LOADED);
                    }
                },
                throwable -> {
                    setRetry(() -> loadInitial(params, callback));
                    NetworkState error = NetworkState.error(throwable.getMessage());
                    networkState.postValue(error);
                    initialLoad.postValue(error);
                },
                RELEASE_GROUPE_BROWSE_LIMIT, 0, albumType));
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, ReleaseGroup> callback) {
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, ReleaseGroup> callback) {
        networkState.postValue(NetworkState.LOADING);

        compositeDisposable.add(api.getReleaseGroupsByArtistAndAlbumTypes(
                artistMbid,
                releaseGroupBrowse -> {
                    setRetry(null);
                    initialLoad.postValue(NetworkState.LOADED);

                    List<ReleaseGroup> releaseGroups = releaseGroupBrowse.getReleaseGroups();
                    List<ReleaseGroup> rgs = mutableIsOfficial.getValue() ? selectOfficialReleaseGroups(releaseGroups) : selectReleaseGroups(releaseGroups);

                    int nextOffset = releaseGroupBrowse.getOffset() + RELEASE_GROUPE_BROWSE_LIMIT;

                    callback.onResult(rgs,
                            ((mutableIsOfficial.getValue() || releaseGroups.size() == rgs.size()) && releaseGroupBrowse.getCount() > nextOffset) ? nextOffset : null);

                    networkState.postValue(NetworkState.LOADED);

                },
                throwable -> {
                    setRetry(() -> loadAfter(params, callback));
                    networkState.postValue(NetworkState.error(throwable.getMessage()));
                },
                RELEASE_GROUPE_BROWSE_LIMIT, params.key, albumType));
    }

    private List<ReleaseGroup> selectReleaseGroups(List<ReleaseGroup> releaseGroups) {
        List<ReleaseGroup> rgs = new ArrayList<>();
        if (albumType.equals(ALBUM)) {
            for (ReleaseGroup rg : releaseGroups) {
                if (rg.getSecondaryTypes() == null || rg.getSecondaryTypes().isEmpty()) {
                    rgs.add(rg);
                }
            }
        } else {
            rgs.addAll(releaseGroups);
        }
        Collections.sort(rgs, (rg1, rg2) -> rg1.getYear() - rg2.getYear());
        return rgs;
    }

    private List<ReleaseGroup> selectOfficialReleaseGroups(List<ReleaseGroup> releaseGroups) {
        List<ReleaseGroup> rgs = new ArrayList<>();
        for (ReleaseGroup rg : releaseGroups) {
            if (officialReleaseGroups.contains(rg)) {
                rgs.add(rg);
            }
        }
        Collections.sort(rgs, (rg1, rg2) -> rg1.getYear() - rg2.getYear());
        return rgs;
    }

    @NonNull
    public MutableLiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    @NonNull
    public MutableLiveData<NetworkState> getInitialLoad() {
        return initialLoad;
    }

    private void setRetry(final Action action) {
        this.retryCompletable = action == null ? null : Completable.fromAction(action);
    }

    public static class Factory extends PageKeyedDataSource.Factory<Integer, ReleaseGroup> {

        private CompositeDisposable compositeDisposable;
        private ReleaseGroup.AlbumType albumType;
        private String artistMbid;
        private MutableLiveData<Boolean> mutableIsOfficial;
        private MutableLiveData<ReleaseGroupsDataSource> releaseGroupsDataSourceLiveData = new MutableLiveData<>();

        public Factory(CompositeDisposable compositeDisposable, String artistMbid, ReleaseGroup.AlbumType albumType, MutableLiveData<Boolean> mutableIsOfficial) {
            this.compositeDisposable = compositeDisposable;
            this.artistMbid = artistMbid;
            this.albumType = albumType;
            this.mutableIsOfficial = mutableIsOfficial;
        }

        @Override
        public PageKeyedDataSource<Integer, ReleaseGroup> create() {
            ReleaseGroupsDataSource releaseGroupsDataSource = new ReleaseGroupsDataSource(compositeDisposable, artistMbid, albumType, mutableIsOfficial);
            releaseGroupsDataSourceLiveData.postValue(releaseGroupsDataSource);
            return releaseGroupsDataSource;
        }

        @NonNull
        public MutableLiveData<ReleaseGroupsDataSource> getReleaseGroupsDataSourceLiveData() {
            return releaseGroupsDataSourceLiveData;
        }

    }
}
