package app.mediabrainz.api.other;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.xml.Metadata;

import static app.mediabrainz.api.model.Collection.AREA_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.ARTIST_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.EVENT_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.INSTRUMENT_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.LABEL_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.PLACE_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.RECORDING_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.RELEASE_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.RELEASE_GROUP_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.SERIES_ENTITY_TYPE;
import static app.mediabrainz.api.model.Collection.WORK_ENTITY_TYPE;


public interface CollectionServiceInterface {

    Flowable<Result<Metadata>> putCollection(String collId, CollectionType collType, String... ids);

    Flowable<Result<Metadata>> deleteCollection(String collId, CollectionType collType, String... ids);

    enum CollectionType {

        AREAS("areas", AREA_ENTITY_TYPE),
        ARTISTS("artists", ARTIST_ENTITY_TYPE),
        EVENTS("events", EVENT_ENTITY_TYPE),
        INSTRUMENT("instruments", INSTRUMENT_ENTITY_TYPE),
        LABELS("labels", LABEL_ENTITY_TYPE),
        PLACES("places", PLACE_ENTITY_TYPE),
        RECORDINGS("recordings", RECORDING_ENTITY_TYPE),
        RELEASE_GROUPS("release-groups", RELEASE_GROUP_ENTITY_TYPE),
        RELEASES("releases", RELEASE_ENTITY_TYPE),
        SERIES("series", SERIES_ENTITY_TYPE),
        WORKS("works", WORK_ENTITY_TYPE);

        private final String type;
        private final String entityType;

        CollectionType (String type, String entityType) {
            this.type = type;
            this.entityType = entityType;
        }
        @Override
        public String toString() {
            return type;
        }

        public String getEntityType() {
            return entityType;
        }

        public String getType() {
            return type;
        }

        public static CollectionType getCollectionType(String entityType) {
            for (CollectionType collectionType : CollectionType.values()) {
                if (entityType.equals(collectionType.getEntityType())) {
                    return collectionType;
                }
            }
            return null;
        }
    }

}
