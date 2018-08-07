package pl.piotrskiba.dailywallpaper;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

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
