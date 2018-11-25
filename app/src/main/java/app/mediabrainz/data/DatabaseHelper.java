package app.mediabrainz.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import app.mediabrainz.api.model.Tag;

import java.util.Collections;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "mediabrainzdb";

    public static final String RECOMMENDS_TABLE = "recommends";
    public static final String RECOMMENDS_COLUMN_TAG = "TAG";
    public static final String RECOMMENDS_COLUMN_NUMBER = "NUMBER";

    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createContactsTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void setRecommends(List<Tag> tags) {
        if (tags != null && !tags.isEmpty()) {
            Collections.sort(tags, (t1, t2) -> t2.getCount() - t1.getCount());
            Tag tag1 = tags.get(0);
            if (tags.size() == 1) {
                addTag(tag1.getName(), 1);
            } else {
                Tag tag2 = tags.get(1);
                addTag(tag2.getName(), 1);
                addTag(tag1.getName(), tag1.getCount() > tag2.getCount() ? 2 : 1);
            }
        }
    }

    public Cursor selectTag(String tagName) {
        return getReadableDatabase().query(
                RECOMMENDS_TABLE,
                new String[]{RECOMMENDS_COLUMN_TAG, RECOMMENDS_COLUMN_NUMBER},
                RECOMMENDS_COLUMN_TAG + " = ?",
                new String[]{tagName},
                null, null, null);
    }

    public Cursor selectAllTags() {
        return getReadableDatabase().query(
                RECOMMENDS_TABLE,
                new String[]{RECOMMENDS_COLUMN_TAG, RECOMMENDS_COLUMN_NUMBER},
                null, null, null, null, RECOMMENDS_COLUMN_NUMBER + " DESC");
    }

    public void addTag(String tagName, int number) {
        ContentValues contactsValues = new ContentValues();
        Cursor cursor = selectTag(tagName);
        if (cursor.moveToFirst()) {
            contactsValues.put(RECOMMENDS_COLUMN_NUMBER, cursor.getInt(1) + number);
            getWritableDatabase().update(RECOMMENDS_TABLE, contactsValues, RECOMMENDS_COLUMN_TAG + " = ?", new String[]{tagName});
        } else {
            contactsValues.put(RECOMMENDS_COLUMN_TAG, tagName);
            contactsValues.put(RECOMMENDS_COLUMN_NUMBER, number);
            getWritableDatabase().insert(RECOMMENDS_TABLE, null, contactsValues);
        }
        cursor.close();
    }

    public void deleteTag(String tagName) {
        getWritableDatabase().delete(RECOMMENDS_TABLE, RECOMMENDS_COLUMN_TAG + " = ?", new String[]{tagName});
    }

    public void deleteAllTags() {
        getWritableDatabase().delete(RECOMMENDS_TABLE, null, null);
    }

    private void createContactsTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + RECOMMENDS_TABLE + " ("
                + RECOMMENDS_COLUMN_TAG + " TEXT UNIQUE, "
                + RECOMMENDS_COLUMN_NUMBER + " INTEGER);"
        );
    }
}
