package pl.piotrskiba.dailywallpaper.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ImageEntry::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageDao

    companion object {
        private val LOCK = Any()
        private const val DATABASE_NAME = "wallpapers"
        private lateinit var sInstance: AppDatabase
        @JvmStatic
        fun getInstance(context: Context): AppDatabase {
            synchronized(LOCK) {
                sInstance = Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
            }
            return sInstance
        }
    }
}