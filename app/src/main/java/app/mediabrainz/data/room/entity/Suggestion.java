package app.mediabrainz.data.room.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;


@Entity(tableName = "suggestions",
        primaryKeys = {"word","field"})
public class Suggestion {

    public enum SuggestionField {
        ARTIST("artist"),
        ALBUM("album"),
        TRACK("track"),
        TAG("tag"),
        USER("user");

        private final String field;

        SuggestionField(String field) {
            this.field = field;
        }

        public String getField() {
            return field;
        }
    }

    @NonNull
    @ColumnInfo(name = "word")
    private String word;

    @NonNull
    @ColumnInfo(name = "field")
    private String field;

    public Suggestion() {
    }

    public Suggestion(@NonNull String word, @NonNull String field) {
        this.word = word;
        this.field = field;
    }

    public Suggestion(@NonNull String word, @NonNull SuggestionField suggestionField) {
        this(word, suggestionField.getField());
    }

    @NonNull
    public String getWord() {
        return word;
    }

    public void setWord(@NonNull String word) {
        this.word = word;
    }

    @NonNull
    public String getField() {
        return field;
    }

    public void setField(@NonNull String field) {
        this.field = field;
    }

    @NonNull
    @Override
    public String toString() {
        return word;
    }
}
