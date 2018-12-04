package app.mediabrainz.data.room.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity(tableName = "recommends")
public class Recommend {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "tag")
    private String tag;

    @ColumnInfo(name = "number")
    private int number;

    public Recommend(@NonNull String tag, int number) {
        this.tag = tag;
        this.number = number;
    }

    @NonNull
    public String getTag() {
        return tag;
    }

    public void setTag(@NonNull String tag) {
        this.tag = tag;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
