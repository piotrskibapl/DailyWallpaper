package pl.piotrskiba.dailywallpaper.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ImageDao {

    @Query("SELECT * FROM image ORDER BY updatedAt")
    LiveData<List<ImageEntry>> loadAllImages();

    @Query("SELECT * FROM image WHERE imageId = :imageId")
    List<ImageEntry> loadImagesByImageId(int imageId);

    @Insert
    void insertImage(ImageEntry imageEntry);

    @Delete
    void deleteImage(ImageEntry imageEntry);
}
