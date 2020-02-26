package pl.piotrskiba.dailywallpaper.interfaces

import android.view.View
import pl.piotrskiba.dailywallpaper.models.Image

interface ImageClickListener {
    fun onImageClick(clickedImage: Image, view: View)
}