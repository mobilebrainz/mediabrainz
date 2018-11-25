package app.mediabrainz.api.model;

import java.util.ArrayList;
import java.util.List;

import app.mediabrainz.api.model.relations.Relation;


public class RelationExtractor {

    private List<Area> areas = new ArrayList<>();
    private List<Artist> artists = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    private List<Instrument> instruments = new ArrayList<>();
    private List<Label> labels = new ArrayList<>();
    private List<Place> places = new ArrayList<>();
    private List<Recording> recordings = new ArrayList<>();
    private List<Release> releases = new ArrayList<>();
    private List<ReleaseGroup> releaseGroups = new ArrayList<>();
    private List<Work> works = new ArrayList<>();
    private List<Series> series = new ArrayList<>();
    private List<Url> urls = new ArrayList<>();

    private List<Relation> areaRelations = new ArrayList<>();
    private List<Relation> artistRelations = new ArrayList<>();
    private List<Relation> eventRelations = new ArrayList<>();
    private List<Relation> instrumentRelations = new ArrayList<>();
    private List<Relation> labelRelations = new ArrayList<>();
    private List<Relation> placeRelations = new ArrayList<>();
    private List<Relation> recordingRelations = new ArrayList<>();
    private List<Relation> releaseRelations = new ArrayList<>();
    private List<Relation> releaseGroupRelations = new ArrayList<>();
    private List<Relation> workRelations = new ArrayList<>();
    private List<Relation> seriesRelations = new ArrayList<>();
    private List<Relation> urlRelations = new ArrayList<>();

    public RelationExtractor(BaseLookupEntity baseLookupEntity) {
        extract(baseLookupEntity);
    }

    private void extract(BaseLookupEntity baseLookupEntity) {
        List<Relation> relations = baseLookupEntity.getRelations();
        if (relations != null && !relations.isEmpty()) {
            for (Relation relation : relations) {
                if (relation.getUrl() != null) {
                    Url url = relation.getUrl();
                    url.setType(relation.getType());
                    urlRelations.add(relation);
                    urls.add(url);
                } else if (relation.getArtist() != null) {
                    Artist artist = relation.getArtist();
                    artistRelations.add(relation);
                    artists.add(artist);
                } else if (relation.getArea() != null) {
                    Area area = relation.getArea();
                    areaRelations.add(relation);
                    areas.add(area);
                } else if (relation.getEvent() != null) {
                    Event event = relation.getEvent();
                    eventRelations.add(relation);
                    events.add(event);
                } else if (relation.getInstrument() != null) {
                    Instrument instrument = relation.getInstrument();
                    instrumentRelations.add(relation);
                    instruments.add(instrument);
                } else if (relation.getLabel() != null) {
                    Label label = relation.getLabel();
                    labelRelations.add(relation);
                    labels.add(label);
                } else if (relation.getPlace() != null) {
                    Place place = relation.getPlace();
                    placeRelations.add(relation);
                    places.add(place);
                } else if (relation.getRecording() != null) {
                    Recording recording = relation.getRecording();
                    recordingRelations.add(relation);
                    recordings.add(recording);
                } else if (relation.getRelease() != null) {
                    Release release = relation.getRelease();
                    releaseRelations.add(relation);
                    releases.add(release);
                } else if (relation.getReleaseGroup() != null) {
                    ReleaseGroup releaseGroup = relation.getReleaseGroup();
                    releaseGroupRelations.add(relation);
                    releaseGroups.add(releaseGroup);
                } else if (relation.getSeries() != null) {
                    Series s = relation.getSeries();
                    seriesRelations.add(relation);
                    series.add(s);
                } else if (relation.getWork() != null) {
                    Work work = relation.getWork();
                    workRelations.add(relation);
                    works.add(work);
                }
            }
        }
    }

    public List<Url> getUrls() {
        return urls;
    }

    public List<Area> getAreas() {
        return areas;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public List<Event> getEvents() {
        return events;
    }

    public List<Instrument> getInstruments() {
        return instruments;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public List<Recording> getRecordings() {
        return recordings;
    }

    public List<Release> getReleases() {
        return releases;
    }

    public List<ReleaseGroup> getReleaseGroups() {
        return releaseGroups;
    }

    public List<Work> getWorks() {
        return works;
    }

    public List<Series> getSeries() {
        return series;
    }

    public List<Relation> getAreaRelations() {
        return areaRelations;
    }

    public List<Relation> getArtistRelations() {
        return artistRelations;
    }

    public List<Relation> getEventRelations() {
        return eventRelations;
    }

    public List<Relation> getInstrumentRelations() {
        return instrumentRelations;
    }

    public List<Relation> getLabelRelations() {
        return labelRelations;
    }

    public List<Relation> getPlaceRelations() {
        return placeRelations;
    }

    public List<Relation> getRecordingRelations() {
        return recordingRelations;
    }

    public List<Relation> getReleaseRelations() {
        return releaseRelations;
    }

    public List<Relation> getReleaseGroupRelations() {
        return releaseGroupRelations;
    }

    public List<Relation> getWorkRelations() {
        return workRelations;
    }

    public List<Relation> getSeriesRelations() {
        return seriesRelations;
    }

    public List<Relation> getUrlRelations() {
        return urlRelations;
    }
}
