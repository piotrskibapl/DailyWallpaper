package pl.piotrskiba.dailywallpaper.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import timber.log.Timber
import java.io.FileInputStream
import java.io.FileOutputStream

object BitmapUtils {
    const val SUFFIX_PREVIEW = "_preview"
    const val SUFFIX_WEBFORMAT = "_webformat"
    const val SUFFIX_LARGEIMAGE = "_largeImage"
    const val IMAGE_EXTENSION = ".png"
    @JvmStatic
    fun saveBitmap(context: Context, bitmap: Bitmap, imageName: String) {
        val foStream: FileOutputStream
        try {
            foStream = context.openFileOutput(imageName, Context.MODE_PRIVATE)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, foStream)
            foStream.close()
            Timber.d("Bitmap saved")
        } catch (e: Exception) {
            Timber.d("Could not save an image.")
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun loadBitmap(context: Context, imageName: String): Bitmap? {
        var bitmap: Bitmap? = null
        val fiStream: FileInputStream
        try {
            fiStream = context.openFileInput(imageName)
            bitmap = BitmapFactory.decodeStream(fiStream)
            fiStream.close()
            Timber.d("Bitmap loaded")
        } catch (e: Exception) {
            Timber.d("Could not load an image.")
            e.printStackTrace()
        }
        return bitmap
    }
}