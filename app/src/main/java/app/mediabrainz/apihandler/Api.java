package app.mediabrainz.apihandler;

import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.account.OAuth;
import app.mediabrainz.api.browse.AreaBrowseService;
import app.mediabrainz.api.browse.ArtistBrowseService;
import app.mediabrainz.api.browse.CollectionBrowseService;
import app.mediabrainz.api.browse.EventBrowseService;
import app.mediabrainz.api.browse.LabelBrowseService;
import app.mediabrainz.api.browse.PlaceBrowseService;
import app.mediabrainz.api.browse.RecordingBrowseService;
import app.mediabrainz.api.browse.ReleaseBrowseService;
import app.mediabrainz.api.browse.ReleaseGroupBrowseService;
import app.mediabrainz.api.browse.SeriesBrowseService;
import app.mediabrainz.api.browse.WorkBrowseService;
import app.mediabrainz.api.coverart.CoverArtService;
import app.mediabrainz.api.coverart.ReleaseCoverArt;
import app.mediabrainz.api.lastfm.LastfmService;
import app.mediabrainz.api.lastfm.model.LastfmResult;
import app.mediabrainz.api.lookup.ArtistLookupService;
import app.mediabrainz.api.lookup.ArtistLookupService.ArtistIncType;
import app.mediabrainz.api.lookup.CollectionLookupService;
import app.mediabrainz.api.lookup.LookupServiceInterface.RelsType;
import app.mediabrainz.api.lookup.RecordingLookupService;
import app.mediabrainz.api.lookup.RecordingLookupService.RecordingIncType;
import app.mediabrainz.api.lookup.ReleaseGroupLookupService;
import app.mediabrainz.api.lookup.ReleaseGroupLookupService.ReleaseGroupIncType;
import app.mediabrainz.api.lookup.ReleaseLookupService;
import app.mediabrainz.api.lookup.WorkLookupService;
import app.mediabrainz.api.lyrics.LyricsService;
import app.mediabrainz.api.lyrics.model.LyricsApi;
import app.mediabrainz.api.lyrics.model.LyricsResult;
import app.mediabrainz.api.model.Area;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.BaseLookupEntity;
import app.mediabrainz.api.model.Collection;
import app.mediabrainz.api.model.Event;
import app.mediabrainz.api.model.Label;
import app.mediabrainz.api.model.Place;
import app.mediabrainz.api.model.Recording;
import app.mediabrainz.api.model.Release;
import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.api.model.Series;
import app.mediabrainz.api.model.Tag;
import app.mediabrainz.api.model.Work;
import app.mediabrainz.api.model.xml.Metadata;
import app.mediabrainz.api.model.xml.UserTagXML;
import app.mediabrainz.api.other.CollectionService;
import app.mediabrainz.api.other.CollectionServiceInterface;
import app.mediabrainz.api.other.PostWebService;
import app.mediabrainz.api.other.genres.GenresService;
import app.mediabrainz.api.search.ArtistSearchService;
import app.mediabrainz.api.search.RecordingSearchService;
import app.mediabrainz.api.search.RecordingSearchService.RecordingSearchField;
import app.mediabrainz.api.search.ReleaseGroupSearchService;
import app.mediabrainz.api.search.ReleaseSearchService;
import app.mediabrainz.api.search.TagSearchService;
import app.mediabrainz.api.site.Rating;
import app.mediabrainz.api.site.RatingService;
import app.mediabrainz.api.site.RatingServiceInterface;
import app.mediabrainz.api.site.SearchService;
import app.mediabrainz.api.site.SiteService;
import app.mediabrainz.api.site.TagEntity;
import app.mediabrainz.api.site.TagService;
import app.mediabrainz.api.site.TagServiceInterface;
import app.mediabrainz.api.site.UserProfile;
import app.mediabrainz.api.site.UserProfileService;
import app.mediabrainz.api.wiki.WikiService;
import app.mediabrainz.api.wiki.model.Wikipedia;
import app.mediabrainz.functions.Consumer;
import app.mediabrainz.functions.ErrorHandler;

import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

import static app.mediabrainz.api.browse.CollectionBrowseService.CollectionBrowseEntityType.EDITOR;
import static app.mediabrainz.api.browse.CollectionBrowseService.CollectionIncType.USER_COLLECTIONS;
import static app.mediabrainz.api.browse.RecordingBrowseService.RecordingBrowseEntityType.RELEASE;
import static app.mediabrainz.api.browse.ReleaseBrowseService.ReleaseBrowseEntityType.RECORDING;
import static app.mediabrainz.api.browse.ReleaseBrowseService.ReleaseBrowseEntityType.RELEASE_GROUP;
import static app.mediabrainz.api.browse.ReleaseGroupBrowseService.ReleaseGroupBrowseEntityType.ARTIST;
import static app.mediabrainz.api.lookup.LookupServiceInterface.RelsType.ARTIST_RELS;
import static app.mediabrainz.api.lookup.LookupServiceInterface.RelsType.URL_RELS;


public class Api {

    // for post request and Digest Authentication MediaBrainzApp.getVersion()
    public static final String CLIENT = MediaBrainzApp.getPackage() + "-" + MediaBrainzApp.getVersion();

    private OAuth oauth;

    private SiteService siteService;

    public SiteService getSiteService() {
        if (siteService == null) {
            siteService = new SiteService(oauth.getName(), oauth.getPassword());
        }
        return siteService;
    }

    public Api(OAuth oauth) {
        this.oauth = oauth;
    }

    public Disposable getCollections(Consumer<Collection.CollectionBrowse> consumer, ErrorHandler errorHandler, int limit, int offset) {
        return oauth.refreshToken(
                () -> ApiHandler.subscribe503(
                        new CollectionBrowseService(EDITOR, oauth.getAccount().name)
                                .addIncs(USER_COLLECTIONS)
                                .browse(limit, offset),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable getCollections(String username, Consumer<Collection.CollectionBrowse> consumer, ErrorHandler errorHandler, int limit, int offset) {
        return (oauth.hasAccount() && oauth.getName().equals(username)) ?
                getCollections(consumer, errorHandler, limit, offset) :
                ApiHandler.subscribe503(
                        new CollectionBrowseService(EDITOR, username).browse(limit, offset),
                        consumer, errorHandler);
    }

    public Disposable getCollection(String mbid, Consumer<Collection> consumer, ErrorHandler errorHandler) {
        return ApiHandler.subscribe503(
                new CollectionLookupService(mbid).lookup(),
                consumer, errorHandler);
    }

    public Disposable getPrivateCollection(String mbid, Consumer<Collection> consumer, ErrorHandler errorHandler) {
        return oauth.refreshToken(
                () -> ApiHandler.subscribe503(
                        new CollectionLookupService(mbid).lookup(),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable getCollection(String mbid, Consumer<Collection> consumer, ErrorHandler errorHandler, boolean isPrivate) {
        return isPrivate ? getPrivateCollection(mbid, consumer, errorHandler) : getCollection(mbid, consumer, errorHandler);
    }

    public Disposable createCollection(String name, int type, String description, int isPublic, Consumer<ResponseBody> consumer, ErrorHandler errorHandler) {
        return ApiHandler.subscribe(
                getSiteService().createCollection(name, type, description, isPublic),
                consumer, errorHandler);
    }

    public Disposable deleteCollection(Collection collection, Consumer<ResponseBody> consumer, ErrorHandler errorHandler) {
        return ApiHandler.subscribe(
                getSiteService().deleteCollection(collection.getId()),
                consumer, errorHandler);
    }

    public Disposable editCollection(Collection collection, String name, int typeInt, String description, int isPublic, Consumer<ResponseBody> consumer, ErrorHandler errorHandler) {
        return ApiHandler.subscribe(
                getSiteService().editCollection(collection.getId(), name, typeInt, description, isPublic),
                consumer, errorHandler);
    }

    public boolean deleteEntityFromCollection(Collection collection, BaseLookupEntity entity, Consumer<Metadata> consumer, ErrorHandler errorHandler) {
        CollectionServiceInterface.CollectionType collType = CollectionService.getCollectionType(collection.getEntityType());
        if (collType != null) {
            oauth.refreshToken(
                    () -> ApiHandler.subscribe(
                            new CollectionService(CLIENT).deleteCollection(collection.getId(), collType, entity.getId()),
                            consumer, errorHandler),
                    errorHandler);
            return true;
        }
        return false;
    }

    public Disposable addEntityToCollection(String collId, CollectionServiceInterface.CollectionType collType, String id, Consumer<Metadata> consumer, ErrorHandler errorHandler) {
        return oauth.refreshToken(
                () -> ApiHandler.subscribe(
                        new CollectionService(CLIENT).putCollection(collId, collType, id),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable getAreasFromCollection(Collection collection, Consumer<Area.AreaBrowse> consumer, ErrorHandler errorHandler, int limit, int offset) {
        return oauth.refreshToken(
                () -> ApiHandler.subscribe503(
                        new AreaBrowseService(AreaBrowseService.AreaBrowseEntityType.COLLECTION, collection.getId())
                                .browse(100, 0),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable getArtistsFromCollection(Collection collection, Consumer<Artist.ArtistBrowse> consumer, ErrorHandler errorHandler, int limit, int offset) {
        return oauth.refreshToken(
                () -> ApiHandler.subscribe503(
                        new ArtistBrowseService(ArtistBrowseService.ArtistBrowseEntityType.COLLECTION, collection.getId())
                                .addIncs(ArtistBrowseService.ArtistIncType.RATINGS, ArtistBrowseService.ArtistIncType.USER_RATINGS)
                                .browse(100, 0),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable getEventsFromCollection(Collection collection, Consumer<Event.EventBrowse> consumer, ErrorHandler errorHandler, int limit, int offset) {
        return oauth.refreshToken(
                () -> ApiHandler.subscribe503(
                        new EventBrowseService(EventBrowseService.EventBrowseEntityType.COLLECTION, collection.getId())
                                .browse(100, 0),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable getLabelsFromCollection(Collection collection, Consumer<Label.LabelBrowse> consumer, ErrorHandler errorHandler, int limit, int offset) {
        return oauth.refreshToken(
                () -> ApiHandler.subscribe503(
                        new LabelBrowseService(LabelBrowseService.LabelBrowseEntityType.COLLECTION, collection.getId())
                                .browse(100, 0),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable getPlacesFromCollection(Collection collection, Consumer<Place.PlaceBrowse> consumer, ErrorHandler errorHandler, int limit, int offset) {
        return oauth.refreshToken(
                () -> ApiHandler.subscribe503(
                        new PlaceBrowseService(PlaceBrowseService.PlaceBrowseEntityType.COLLECTION, collection.getId())
                                .browse(100, 0),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable getRecordingsFromCollection(Collection collection, Consumer<Recording.RecordingBrowse> consumer, ErrorHandler errorHandler, int limit, int offset) {
        return oauth.refreshToken(
                () -> ApiHandler.subscribe503(
                        new RecordingBrowseService(RecordingBrowseService.RecordingBrowseEntityType.COLLECTION, collection.getId())
                                .addIncs(RecordingBrowseService.RecordingIncType.RATINGS, RecordingBrowseService.RecordingIncType.USER_RATINGS)
                                .browse(100, 0),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable getReleasesFromCollection(Collection collection, Consumer<Release.ReleaseBrowse> consumer, ErrorHandler errorHandler, int limit, int offset) {
        return oauth.refreshToken(
                () -> ApiHandler.subscribe503(
                        new ReleaseBrowseService(ReleaseBrowseService.ReleaseBrowseEntityType.COLLECTION, collection.getId())
                                .browse(100, 0),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable getReleaseGroupsFromCollection(Collection collection, Consumer<ReleaseGroup.ReleaseGroupBrowse> consumer, ErrorHandler errorHandler, int limit, int offset) {
        return oauth.refreshToken(
                () -> ApiHandler.subscribe503(
                        new ReleaseGroupBrowseService(ReleaseGroupBrowseService.ReleaseGroupBrowseEntityType.COLLECTION, collection.getId())
                                .addIncs(ReleaseGroupBrowseService.ReleaseGroupIncType.RATINGS, ReleaseGroupBrowseService.ReleaseGroupIncType.USER_RATINGS)
                                .browse(100, 0),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable getSeriesFromCollection(Collection collection, Consumer<Series.SeriesBrowse> consumer, ErrorHandler errorHandler, int limit, int offset) {
        return oauth.refreshToken(
                () -> ApiHandler.subscribe503(
                        new SeriesBrowseService(SeriesBrowseService.SeriesBrowseEntityType.COLLECTION, collection.getId())
                                .browse(100, 0),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable getWorksFromCollection(Collection collection, Consumer<Work.WorkBrowse> consumer, ErrorHandler errorHandler, int limit, int offset) {
        return oauth.refreshToken(
                () -> ApiHandler.subscribe503(
                        new WorkBrowseService(WorkBrowseService.WorkBrowseEntityType.COLLECTION, collection.getId())
                                .browse(100, 0),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable searchAlbum(String artist, String album, Consumer<ReleaseGroup.ReleaseGroupSearch> consumer, ErrorHandler errorHandler) {
        return ApiHandler.subscribe503(
                new ReleaseGroupSearchService()
                        .add(ReleaseGroupSearchService.ReleaseGroupSearchField.ARTIST, artist)
                        .add(ReleaseGroupSearchService.ReleaseGroupSearchField.RELEASE_GROUP, album)
                        .search(),
                consumer, errorHandler);
    }

    public Disposable getReleasesByAlbum(String releaseGroupMbid, Consumer<Release.ReleaseBrowse> consumer, ErrorHandler errorHandler, int limit, int offset) {
        return ApiHandler.subscribe503(
                new ReleaseBrowseService(RELEASE_GROUP, releaseGroupMbid)
                        .addIncs(ReleaseBrowseService.ReleaseIncType.ARTIST_CREDITS,
                                ReleaseBrowseService.ReleaseIncType.LABELS,
                                ReleaseBrowseService.ReleaseIncType.MEDIA)
                        .browse(limit, offset),
                consumer, errorHandler);
    }

    public Disposable getReleasesByRecording(String recordingMbid, Consumer<Release.ReleaseBrowse> consumer, ErrorHandler errorHandler, int limit, int offset) {
        return ApiHandler.subscribe503(
                new ReleaseBrowseService(RECORDING, recordingMbid)
                        .addIncs(ReleaseBrowseService.ReleaseIncType.LABELS,
                                ReleaseBrowseService.ReleaseIncType.MEDIA,
                                ReleaseBrowseService.ReleaseIncType.RELEASE_GROUPS)
                        .browse(limit, offset),
                consumer, errorHandler);
    }

    public Disposable getReleasesByType(ReleaseBrowseService.ReleaseBrowseEntityType type, String mbid, Consumer<Release.ReleaseBrowse> consumer, ErrorHandler errorHandler, int limit, int offset) {
        return ApiHandler.subscribe503(
                new ReleaseBrowseService(type, mbid)
                        .addIncs(ReleaseBrowseService.ReleaseIncType.ARTIST_CREDITS,
                                ReleaseBrowseService.ReleaseIncType.LABELS,
                                ReleaseBrowseService.ReleaseIncType.MEDIA,
                                ReleaseBrowseService.ReleaseIncType.RELEASE_GROUPS)
                        .browse(limit, offset),
                consumer, errorHandler);
    }

    public Disposable searchArtist(String artist, Consumer<Artist.ArtistSearch> consumer, ErrorHandler errorHandler) {
        return ApiHandler.subscribe503(new ArtistSearchService().search(artist), consumer, errorHandler);
    }

    public Disposable searchRecording(String artist, String album, String track, Consumer<Recording.RecordingSearch> consumer, ErrorHandler errorHandler) {
        return ApiHandler.subscribe503(
                new RecordingSearchService()
                        .add(RecordingSearchField.ARTIST, artist)
                        .add(RecordingSearchField.RELEASE, album)
                        .add(RecordingSearchField.RECORDING, track)
                        .search(),
                consumer, errorHandler);
    }

    public Disposable searchRelease(String artist, String release, Consumer<Release.ReleaseSearch> consumer, ErrorHandler errorHandler, int limit, int offset) {
        return ApiHandler.subscribe503(
                new ReleaseSearchService()
                        .add(ReleaseSearchService.ReleaseSearchField.ARTIST, artist)
                        .add(ReleaseSearchService.ReleaseSearchField.RELEASE, release)
                        .search(limit, offset),
                consumer, errorHandler);
    }

    public Disposable searchReleasesByBarcode(String barcode, Consumer<Release.ReleaseSearch> consumer, ErrorHandler errorHandler) {
        return ApiHandler.subscribe503(
                new ReleaseSearchService().add(ReleaseSearchService.ReleaseSearchField.BARCODE, barcode).search(),
                consumer, errorHandler);
    }

    public Disposable getArtist(String artistMbid, Consumer<Artist> consumer, ErrorHandler errorHandler) {
        ArtistLookupService.ArtistIncType[] incs = oauth.hasAccount() ?
                new ArtistIncType[]{
                        ArtistIncType.RATINGS,
                        ArtistIncType.GENRES,
                        ArtistIncType.TAGS,
                        ArtistIncType.USER_RATINGS,
                        ArtistIncType.USER_GENRES,
                        ArtistIncType.USER_TAGS} :
                new ArtistIncType[]{
                        ArtistIncType.RATINGS,
                        ArtistIncType.GENRES,
                        ArtistIncType.TAGS};
        return oauth.refreshToken(
                () -> ApiHandler.subscribe503(
                        new ArtistLookupService(artistMbid)
                                .addIncs(incs)
                                .addRels(RelsType.URL_RELS, RelsType.ARTIST_RELS)
                                .lookup(),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable getReleaseGroup(String releaseGroupMbid, Consumer<ReleaseGroup> consumer, ErrorHandler errorHandler) {
        ReleaseGroupLookupService.ReleaseGroupIncType[] incs = oauth.hasAccount() ?
                new ReleaseGroupIncType[]{
                        ReleaseGroupIncType.RATINGS,
                        ReleaseGroupIncType.GENRES,
                        ReleaseGroupIncType.TAGS,
                        ReleaseGroupIncType.USER_RATINGS,
                        ReleaseGroupIncType.USER_GENRES,
                        ReleaseGroupIncType.USER_TAGS} :
                new ReleaseGroupIncType[]{
                        ReleaseGroupIncType.RATINGS,
                        ReleaseGroupIncType.GENRES,
                        ReleaseGroupIncType.TAGS};

        return oauth.refreshToken(
                () -> ApiHandler.subscribe503(
                        new ReleaseGroupLookupService(releaseGroupMbid)
                                .addIncs(incs)
                                .addIncs(ReleaseGroupIncType.ARTIST_CREDITS)
                                .addRels(RelsType.URL_RELS)
                                .lookup(),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable getReleaseGroupsByArtist(String artistMbid, Consumer<ReleaseGroup.ReleaseGroupBrowse> consumer, ErrorHandler errorHandler, int limit, int offset) {
        ReleaseGroupBrowseService.ReleaseGroupIncType[] incs = oauth.hasAccount() ?
                new ReleaseGroupBrowseService.ReleaseGroupIncType[]{
                        ReleaseGroupBrowseService.ReleaseGroupIncType.RATINGS,
                        ReleaseGroupBrowseService.ReleaseGroupIncType.USER_RATINGS} :
                new ReleaseGroupBrowseService.ReleaseGroupIncType[]{
                        ReleaseGroupBrowseService.ReleaseGroupIncType.RATINGS};
        return oauth.refreshToken(
                () -> ApiHandler.subscribe503(
                        new ReleaseGroupBrowseService(ARTIST, artistMbid)
                                .addIncs(incs)
                                .browse(limit, offset),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable searchOfficialReleaseGroups(
            String artistMbid,
            Consumer<ReleaseGroup.ReleaseGroupSearch> consumer,
            ErrorHandler errorHandler, int limit, int offset,
            ReleaseGroup.PrimaryType primaryType,
            ReleaseGroup.SecondaryType secondaryType) {

        return ApiHandler.subscribe503(
                new ReleaseGroupSearchService().
                        addPrimaryType(primaryType).
                        addSecondaryType(secondaryType).
                        add(ReleaseGroupSearchService.ReleaseGroupSearchField.STATUS, Release.Status.OFFICIAL.toString()).
                        add(ReleaseGroupSearchService.ReleaseGroupSearchField.ARID, artistMbid).
                        search(limit, offset),
                consumer, errorHandler);
    }

    public Disposable getReleaseGroupsByArtistAndAlbumTypes(String artistMbid, Consumer<ReleaseGroup.ReleaseGroupBrowse> consumer, ErrorHandler errorHandler, int limit, int offset, ReleaseGroup.AlbumType... albumTypes) {
        ReleaseGroupBrowseService.ReleaseGroupIncType[] incs = oauth.hasAccount() ?
                new ReleaseGroupBrowseService.ReleaseGroupIncType[]{
                        ReleaseGroupBrowseService.ReleaseGroupIncType.RATINGS,
                        ReleaseGroupBrowseService.ReleaseGroupIncType.USER_RATINGS} :
                new ReleaseGroupBrowseService.ReleaseGroupIncType[]{
                        ReleaseGroupBrowseService.ReleaseGroupIncType.RATINGS};
        return oauth.refreshToken(
                () -> ApiHandler.subscribe503(
                        new ReleaseGroupBrowseService(ARTIST, artistMbid)
                                .addTypes(albumTypes)
                                .addIncs(incs)
                                .browse(limit, offset),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable getRelease(String releaseMbid, Consumer<Release> consumer, ErrorHandler errorHandler) {
        return ApiHandler.subscribe503(
                new ReleaseLookupService(releaseMbid)
                        .addIncs(ReleaseLookupService.ReleaseIncType.RECORDINGS, ReleaseLookupService.ReleaseIncType.LABELS, ReleaseLookupService.ReleaseIncType.RELEASE_GROUPS)
                        .addRels(RelsType.ARTIST_RELS)
                        .lookup(),
                consumer, errorHandler);
    }

    public Disposable getRecording(String recordingMbid, Consumer<Recording> consumer, ErrorHandler errorHandler) {
        RecordingLookupService.RecordingIncType[] incs = oauth.hasAccount() ?
                new RecordingIncType[]{
                        RecordingIncType.RATINGS,
                        RecordingIncType.GENRES,
                        RecordingIncType.TAGS,
                        RecordingIncType.USER_GENRES,
                        RecordingIncType.USER_RATINGS,
                        RecordingIncType.USER_TAGS} :
                new RecordingIncType[]{
                        RecordingIncType.RATINGS,
                        RecordingIncType.GENRES,
                        RecordingIncType.TAGS};
        return oauth.refreshToken(
                () -> ApiHandler.subscribe503(
                        new RecordingLookupService(recordingMbid)
                                .addIncs(incs)
                                .addIncs(RecordingIncType.ARTIST_CREDITS)
                                .addRels(RelsType.WORK_RELS, RelsType.URL_RELS)
                                .lookup(),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable postBarcode(String releaseMbid, String barcode, Consumer<Metadata> consumer, ErrorHandler errorHandler) {
        return oauth.refreshToken(
                () -> ApiHandler.subscribe(
                        new PostWebService(CLIENT).postBarcode(releaseMbid, barcode),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable getWikipedia(String pageName, Consumer<Wikipedia> consumer, ErrorHandler errorHandler) {
        return ApiHandler.subscribe(
                new WikiService().getWikiMobileview(pageName),
                consumer, errorHandler);
    }

    public Disposable getSitelinks(String q, Consumer<Map<String, String>> consumer, ErrorHandler errorHandler, String... langs) {
        return ApiHandler.subscribe(
                new WikiService().getWikiSitelinks(q, langs),
                consumer, errorHandler);
    }

    public Disposable getArtistFromLastfm(String artist, Consumer<LastfmResult> consumer, ErrorHandler errorHandler) {
        return ApiHandler.subscribe(
                new LastfmService().getArtistInfoByName(artist),
                consumer, errorHandler);
    }

    public Disposable getAlbumFromLastfm(String artist, String album, Consumer<LastfmResult> consumer, ErrorHandler errorHandler) {
        return ApiHandler.subscribe(
                new LastfmService().getAlbumInfoByName(artist, album),
                consumer, errorHandler);
    }

    public Disposable getTrackFromLastfm(String artist, String track, Consumer<LastfmResult> consumer, ErrorHandler errorHandler) {
        return ApiHandler.subscribe(
                new LastfmService().getTrackInfoByName(artist, track),
                consumer, errorHandler);
    }

    public Disposable getArtistTags(String artistMbid, Consumer<Artist> consumer, ErrorHandler errorHandler) {
        return oauth.refreshToken(
                () -> ApiHandler.subscribe(
                        new ArtistLookupService(artistMbid).addIncs(
                                ArtistIncType.TAGS,
                                ArtistIncType.USER_TAGS,
                                ArtistIncType.GENRES,
                                ArtistIncType.USER_GENRES)
                                .lookup(),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable postArtistTag(String artistMbid, String tag, UserTagXML.VoteType voteType, Consumer<Metadata> consumer, ErrorHandler errorHandler) {
        return oauth.refreshToken(
                () -> ApiHandler.subscribe(
                        new PostWebService(CLIENT).postArtistTags(artistMbid, new UserTagXML(tag, voteType)),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable getArtistRatings(String artistMbid, Consumer<Artist> consumer, ErrorHandler errorHandler) {
        return oauth.refreshToken(
                () -> ApiHandler.subscribe(
                        new ArtistLookupService(artistMbid).addIncs(ArtistIncType.RATINGS, ArtistIncType.USER_RATINGS).lookup(),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable postArtistRating(String artistMbid, float rating, Consumer<Metadata> consumer, ErrorHandler errorHandler) {
        return oauth.refreshToken(
                () -> ApiHandler.subscribe(
                        new PostWebService(CLIENT).postArtistRating(artistMbid, (int) rating * 20),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable postRecordingTag(String recordingMbid, String tag, UserTagXML.VoteType voteType, Consumer<Metadata> consumer, ErrorHandler errorHandler) {
        return oauth.refreshToken(
                () -> ApiHandler.subscribe(
                        new PostWebService(CLIENT).postRecordingTags(recordingMbid, new UserTagXML(tag, voteType)),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable getRecordingTags(String recordingMbid, Consumer<Recording> consumer, ErrorHandler errorHandler) {
        return oauth.refreshToken(
                () -> ApiHandler.subscribe(
                        new RecordingLookupService(recordingMbid).addIncs(
                                RecordingIncType.TAGS,
                                RecordingIncType.USER_TAGS,
                                RecordingIncType.GENRES,
                                RecordingIncType.USER_GENRES)
                                .lookup(),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable postRecordingRating(String recordingMbid, float rating, Consumer<Metadata> consumer, ErrorHandler errorHandler) {
        return oauth.refreshToken(
                () -> ApiHandler.subscribe(
                        new PostWebService(CLIENT).postRecordingRating(recordingMbid, (int) rating * 20),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable getRecordingRatings(String recordingMbid, Consumer<Recording> consumer, ErrorHandler errorHandler) {
        return oauth.refreshToken(
                () -> ApiHandler.subscribe(
                        new RecordingLookupService(recordingMbid).addIncs(RecordingIncType.RATINGS, RecordingIncType.USER_RATINGS).lookup(),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable postAlbumTag(String albumMbid, String tag, UserTagXML.VoteType voteType, Consumer<Metadata> consumer, ErrorHandler errorHandler) {
        return oauth.refreshToken(
                () -> ApiHandler.subscribe(
                        new PostWebService(CLIENT).postReleaseGroupTags(albumMbid, new UserTagXML(tag, voteType)),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable getAlbumTags(String albumMbid, Consumer<ReleaseGroup> consumer, ErrorHandler errorHandler) {
        return oauth.refreshToken(
                () -> ApiHandler.subscribe(
                        new ReleaseGroupLookupService(albumMbid).addIncs(
                                ReleaseGroupIncType.TAGS,
                                ReleaseGroupIncType.USER_TAGS,
                                ReleaseGroupIncType.GENRES,
                                ReleaseGroupIncType.USER_GENRES)
                                .lookup(),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable postAlbumRating(String albumMbid, float rating, Consumer<Metadata> consumer, ErrorHandler errorHandler) {
        return oauth.refreshToken(
                () -> ApiHandler.subscribe(
                        new PostWebService(CLIENT).postReleaseGroupRating(albumMbid, (int) rating * 20),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable getAlbumRatings(String albumMbid, Consumer<ReleaseGroup> consumer, ErrorHandler errorHandler) {
        return oauth.refreshToken(
                () -> ApiHandler.subscribe(
                        new ReleaseGroupLookupService(albumMbid).addIncs(ReleaseGroupIncType.RATINGS, ReleaseGroupIncType.USER_RATINGS).lookup(),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable getLyricsWikia(String artist, String track, Consumer<LyricsResult> consumer, ErrorHandler errorHandler) {
        return ApiHandler.subscribe(
                new LyricsService().getLyricsWikia(artist, track),
                consumer, errorHandler);
    }

    public Disposable getLyricsWikiaApi(String artist, String track, Consumer<LyricsApi> consumer, ErrorHandler errorHandler) {
        return ApiHandler.subscribe(
                new LyricsService().getLyricsWikiaApi(artist, track),
                consumer, errorHandler);
    }

    public Disposable getReleaseCoverArt(String releaseMbid, Consumer<ReleaseCoverArt> consumer, ErrorHandler errorHandler) {
        return ApiHandler.subscribe503(
                new CoverArtService().getReleaseCoverArt(releaseMbid),
                consumer, errorHandler);
    }

    public Disposable getReleaseGroupCoverArt(String albumMbid, Consumer<ReleaseCoverArt> consumer, ErrorHandler errorHandler) {
        return ApiHandler.subscribe503(
                new CoverArtService().getReleaseGroupCoverArt(albumMbid),
                consumer, errorHandler);
    }

    public Disposable getRecordingRatingsByRelease(String releaseMbid, Consumer<Recording.RecordingBrowse> consumer, ErrorHandler errorHandler, int limit, int offset) {
        RecordingBrowseService.RecordingIncType[] incs = oauth.hasAccount() ?
                new RecordingBrowseService.RecordingIncType[]{RecordingBrowseService.RecordingIncType.RATINGS, RecordingBrowseService.RecordingIncType.USER_RATINGS} :
                new RecordingBrowseService.RecordingIncType[]{RecordingBrowseService.RecordingIncType.RATINGS};
        return oauth.refreshToken(
                () -> ApiHandler.subscribe(
                        new RecordingBrowseService(RELEASE, releaseMbid)
                                .addIncs(incs).browse(100, 0),
                        consumer, errorHandler),
                errorHandler);
    }

    public Disposable getWork(String workMbid, Consumer<Work> consumer, ErrorHandler errorHandler) {
        return ApiHandler.subscribe503(
                new WorkLookupService(workMbid).addRels(ARTIST_RELS, URL_RELS).lookup(),
                consumer, errorHandler);
    }

    public Disposable getRatings(RatingServiceInterface.RatingType ratingType, String username, int page, Consumer<Rating.Page> consumer, ErrorHandler errorHandler) {
        return ApiHandler.subscribe(
                new RatingService().getRatings(ratingType, username, page),
                consumer, errorHandler);
    }

    public Disposable getTags(String username, Consumer<Map<Tag.TagType, List<Tag>>> consumer, ErrorHandler errorHandler) {
        return ApiHandler.subscribe(
                new TagService().getUserTags(username),
                consumer, errorHandler);
    }

    public Disposable getUserTagEntities(String username, String tag, Consumer<Map<TagServiceInterface.UserTagType, List<TagEntity>>> consumer, ErrorHandler errorHandler) {
        return ApiHandler.subscribe(
                new TagService().getUserTagEntities(username, tag),
                consumer, errorHandler);
    }

    public Disposable getTagEntities(TagServiceInterface.TagType tagType, String tag, int page, Consumer<TagEntity.Page> consumer, ErrorHandler errorHandler) {
        return ApiHandler.subscribe(
                new TagService().getTagEntities(tag, tagType, page),
                consumer, errorHandler);
    }

    public Disposable getUserProfile(String username, Consumer<UserProfile> consumer, ErrorHandler errorHandler) {
        return ApiHandler.subscribe(
                new UserProfileService().getUserProfile(username),
                consumer, errorHandler);
    }

    public Disposable searchTagFromWebservice(String tag, int page, int limit, Consumer<Tag.TagSearch> consumer, ErrorHandler errorHandler) {
        return ApiHandler.subscribe503(
                new TagSearchService().search(tag, limit, page),
                //new TagSearchService().add(TAG, tag).search(limit, page),
                consumer, errorHandler);
    }

    public Disposable searchTagFromSite(String tag, int page, int limit, Consumer<List<String>> consumer, ErrorHandler errorHandler) {
        return ApiHandler.subscribe(
                new SearchService().searchTag(tag, page, limit),
                consumer, errorHandler);
    }

    public Disposable searchUserFromSite(String user, int page, int limit, Consumer<List<String>> consumer, ErrorHandler errorHandler) {
        return ApiHandler.subscribe(
                new SearchService().searchUser(user, page, limit),
                consumer, errorHandler);
    }

    public Disposable getGenres(Consumer<List<String>> consumer, ErrorHandler errorHandler) {
        return ApiHandler.subscribe(
                new GenresService().gerGenres(),
                consumer, errorHandler);
    }

}
