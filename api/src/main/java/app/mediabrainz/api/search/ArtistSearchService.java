package app.mediabrainz.api.search;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.Artist.ArtistSearch;

import static app.mediabrainz.api.search.ArtistSearchService.ArtistSearchField.TYPE;

/**
 * unconditional search: Artist search terms with no fields specified search the ARTIST, SORTNAME and ALIAS fields.
 *    new ArtistSearchService().search("deep purple")
 *    new ArtistSearchService().search("riverside", 5, 10)
 */
public class ArtistSearchService extends
        BaseSearchService<ArtistSearch, ArtistSearchService.ArtistSearchField> {

    @Override
    public Flowable<Result<ArtistSearch>> search() {
        return getJsonRetrofitService().searchArtist(getParams());
    }

    public ArtistSearchService addType(Artist.ArtistType artistType) {
        add(TYPE, artistType.toString());
        return this;
    }

    @Override
    public ArtistSearchService add(LuceneBuilder.Operator operator) {
        super.add(operator);
        return this;
    }

    public enum ArtistSearchField implements SearchServiceInterface.SearchFieldInterface {
        /**
         * an alias attached to the artist
         */
        ALIAS("alias"),

        /**
         * the artist's main associated area
         */
        AREA("area"),

        /**
         * the artist's MBID
         */
        ARID("arid"),

        /**
         * the artist's name (without accented characters)
         */
        ARTIST("artist"),

        /**
         * the artist's name (with accented characters)
         */
        ARTISTACCENT("artistaccent"),

        /**
         * 	the artist's begin date
         */
        BEGIN("begin"),

        /**
         * the artist's begin area
         */
        BEGINAREA("beginarea"),

        /**
         * 	the artist's disambiguation comment
         */
        COMMENT("comment"),

        /**
         * the 2-letter code (ISO 3166-1 alpha-2) for the artist's main associated country, or “unknown”
         */
        COUNTRY("country"),

        /**
         * the artist's end date
         */
        END("end"),

        /**
         * the artist's end area
         */
        ENDAREA("endarea"),

        /**
         * a flag indicating whether or not the artist has ended
         */
        ENDED("ended"),

        /**
         * the artist's gender (“male”, “female”, or “other”)
         */
        GENDER("gender"),

        /**
         * an IPI code associated with the artist
         */
        IPI("ipi"),

        /**
         * the artist's sort name
         */
        SORTNAME("sortname"),

        /**
         * a tag attached to the artist
         */
        TAG("tag"),

        /**
         * the artist's type (“person”, “group”, ...)
         */
        TYPE("type");

        private final String searchField;

        ArtistSearchField(String searchField) {
            this.searchField = searchField;
        }

        @Override
        public String toString() {
            return searchField;
        }
    }

}
