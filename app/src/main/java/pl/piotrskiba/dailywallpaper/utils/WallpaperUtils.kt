package pl.piotrskiba.dailywallpaper.utils

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import java.io.IOException

object WallpaperUtils {

    fun changeWallpaper(context: Context, bitmap: Bitmap): Boolean {
        val wallpaperManager = WallpaperManager.getInstance(context)
        try {
            wallpaperManager.setBitmap(bitmap)
            return true
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }
}