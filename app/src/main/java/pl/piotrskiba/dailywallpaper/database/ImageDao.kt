package pl.piotrskiba.dailywallpaper.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ImageDao {
    @Query("SELECT * FROM image ORDER BY updatedAt")
    fun loadAllImages(): LiveData<List<ImageEntry>>

    @Query("SELECT * FROM image WHERE imageId = :imageId")
    fun loadImagesByImageId(imageId: Int): List<ImageEntry>

    @Insert
    fun insertImage(imageEntry: ImageEntry)

    @Delete
    fun deleteImage(imageEntry: ImageEntry)
}