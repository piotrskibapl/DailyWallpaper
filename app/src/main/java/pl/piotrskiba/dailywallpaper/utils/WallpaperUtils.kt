package pl.piotrskiba.dailywallpaper.utils

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.media.ThumbnailUtils
import android.view.WindowManager
import java.io.IOException

object WallpaperUtils {

    fun adjustBitmapForWallpaper(context: Context, bitmap: Bitmap): Bitmap {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        // center crop the image
        return ThumbnailUtils.extractThumbnail(bitmap, size.x, size.y)
    }

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