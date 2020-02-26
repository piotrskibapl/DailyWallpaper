package pl.piotrskiba.dailywallpaper.interfaces

import android.graphics.Bitmap

interface BitmapLoadedListener {
    fun onBitmapLoaded(bitmap: Bitmap)
}