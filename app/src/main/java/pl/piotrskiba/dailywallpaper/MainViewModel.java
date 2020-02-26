package pl.piotrskiba.dailywallpaper;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import java.util.List;

import pl.piotrskiba.dailywallpaper.database.AppDatabase;
import pl.piotrskiba.dailywallpaper.database.ImageEntry;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<ImageEntry>> favoriteImages;

    public MainViewModel(@NonNull Application application) {
        super(application);

        AppDatabase database = AppDatabase.getInstance(getApplication());
        favoriteImages = database.imageDao().loadAllImages();
    }

    public LiveData<List<ImageEntry>> getFavoriteImages(){
        return favoriteImages;
    }
}
