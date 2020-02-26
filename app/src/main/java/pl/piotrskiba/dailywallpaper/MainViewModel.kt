package pl.piotrskiba.dailywallpaper

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import pl.piotrskiba.dailywallpaper.database.AppDatabase
import pl.piotrskiba.dailywallpaper.database.ImageEntry

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val favoriteImages: LiveData<List<ImageEntry>>

    init {
        val database = AppDatabase.getInstance(getApplication())
        favoriteImages = database.imageDao().loadAllImages()
    }
}