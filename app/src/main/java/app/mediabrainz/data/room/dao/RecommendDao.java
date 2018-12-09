package app.mediabrainz.data.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import app.mediabrainz.data.room.entity.Recommend;

@Dao
public interface RecommendDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Recommend recommend);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecommends(Recommend... recommends);

    @Delete
    void delete(Recommend recommend);

    @Delete
    void deleteRecommends(Recommend... recommends);

    @Query("DELETE FROM recommends")
    void deleteAll();

    @Query("SELECT * from recommends WHERE tag = :tag")
    Recommend findRecommendByTag(String tag);

    @Query("SELECT * from recommends ORDER BY number DESC")
    List<Recommend> getAllRecommends();

}
