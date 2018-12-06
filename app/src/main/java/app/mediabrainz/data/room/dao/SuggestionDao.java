package app.mediabrainz.data.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import app.mediabrainz.data.room.entity.Suggestion;


@Dao
public interface SuggestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Suggestion... suggestions);

    @Query("DELETE FROM suggestions")
    void deleteAll();

    @Query("SELECT * from suggestions WHERE word LIKE :word AND field = :field")
    List<Suggestion> findSuggestionsByWordAndField(String word, String field);

}
