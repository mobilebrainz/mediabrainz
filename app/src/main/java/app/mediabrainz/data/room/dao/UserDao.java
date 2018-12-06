package app.mediabrainz.data.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import app.mediabrainz.data.room.entity.User;


@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User... users);

    @Query("DELETE FROM users")
    void deleteAll();

    @Delete
    void deleteUser(User... users);

    @Query("SELECT * from users WHERE name = :username")
    User findUser(String username);

    @Query("SELECT * from users ORDER BY name ASC")
    List<User> getAllUsers();

}
