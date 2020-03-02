package pl.piotrskiba.dailywallpaper.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import timber.log.Timber
import java.io.File

object BitmapUtils {
    const val SUFFIX_PREVIEW = "_preview"
    const val SUFFIX_WEBFORMAT = "_webformat"
    const val SUFFIX_LARGEIMAGE = "_largeImage"
    const val IMAGE_EXTENSION = ".png"
    @JvmStatic
    fun saveBitmap(context: Context, bitmap: Bitmap, imageName: String) {
        try {
            val foStream = context.openFileOutput(imageName, Context.MODE_PRIVATE)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, foStream)
            foStream.close()
            Timber.d("Saved bitmap \"$imageName\"")
        } catch (e: Exception) {
            Timber.d("Could not save a bitmap (\"$imageName\")")
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun loadBitmap(context: Context, imageName: String): Bitmap? {
        val file = File(context.filesDir, imageName)
        if(file.isFile && file.canRead()){
            try {
                val fiStream = context.openFileInput(imageName)
                val bitmap = BitmapFactory.decodeStream(fiStream)
                fiStream.close()
                Timber.d("Bitmap \"$imageName\" loaded successfully")
                return bitmap
            }
            catch(e: java.lang.Exception){
                Timber.d("Could not load a bitmap (\"$imageName\")")
                e.printStackTrace()
            }
        }
        else{
            Timber.d("Could not load a bitmap \"$imageName\" (isFile: ${file.isFile}, canRead: ${file.canRead()}))")
        }

        return null
    }
}