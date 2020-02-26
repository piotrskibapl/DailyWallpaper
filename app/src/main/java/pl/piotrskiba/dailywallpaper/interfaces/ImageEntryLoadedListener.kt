package pl.piotrskiba.dailywallpaper.interfaces

import pl.piotrskiba.dailywallpaper.database.ImageEntry

interface ImageEntryLoadedListener {
    fun onImageEntryLoaded(imageEntry: ImageEntry?)
}