package pl.piotrskiba.dailywallpaper.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

@Database(entities = {ImageEntry.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase{

    private final static Object LOCK = new Object();
    private final static String DATABASE_NAME = "wallpapers";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context){
        if(sInstance == null){
            synchronized (LOCK){
                sInstance = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME).build();
            }
        }

        return sInstance;
    }

    public abstract ImageDao imageDao();
}
