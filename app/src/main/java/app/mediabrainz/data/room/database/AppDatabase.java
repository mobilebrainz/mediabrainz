package app.mediabrainz.data.room.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import app.mediabrainz.data.room.dao.RecommendDao;
import app.mediabrainz.data.room.entity.Recommend;


@Database(entities = {Recommend.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "mediabrainzdb";

    public abstract RecommendDao recommendDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }

}
