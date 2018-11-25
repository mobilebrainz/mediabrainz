package app.mediabrainz.api.model.relations;

/**
 * https://musicbrainz.org/relationships/artist-artist
 */
public enum ArtistArtistRelationType {

    MEMBER_OF_BAND("member of band"),
    SUBGROUP("subgroup"),
    CONDUCTOR_POSITION("conductor position"),
    FOUNDER("founder"),
    SUPPORTING_MUSICIAN("supporting musician"),
    VOCAL_SUPPORTING_MUSICIAN("vocal supporting musician"),
    INSTRUMENTAL_SUPPORTING_MUSICIAN("instrumental supporting musician"),
    TRIBUTE("tribute"),
    VOICE_ACTOR("voice actor"),
    COLLABORATION("collaboration"),
    IS_PERSON("is person"),
    TEACHER("teacher"),
    COMPOSER_IN_RESIDENCE("composer-in-residence");

    private final String type;

    ArtistArtistRelationType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

    public String getType() {
        return type;
    }
}
