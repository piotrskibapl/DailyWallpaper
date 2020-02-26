package pl.piotrskiba.dailywallpaper.interfaces

import pl.piotrskiba.dailywallpaper.models.ImageList

interface ImageListLoadedListener {
    fun onImageListLoaded(result: ImageList)
}