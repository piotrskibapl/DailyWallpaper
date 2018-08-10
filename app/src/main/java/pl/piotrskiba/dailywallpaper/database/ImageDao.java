package pl.piotrskiba.dailywallpaper.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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
