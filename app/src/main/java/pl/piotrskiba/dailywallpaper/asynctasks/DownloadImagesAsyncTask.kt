package pl.piotrskiba.dailywallpaper.asynctasks

import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import pl.piotrskiba.dailywallpaper.interfaces.ImagesDownloadedListener
import pl.piotrskiba.dailywallpaper.utils.BitmapUtils
import pl.piotrskiba.dailywallpaper.utils.BitmapUtils.saveBitmap
import pl.piotrskiba.dailywallpaper.utils.NetworkUtils.getBitmapFromURL

class DownloadImagesAsyncTask(private val context: Context, private val listener: ImagesDownloadedListener) : AsyncTask<String, Unit, Unit>() {
    override fun doInBackground(vararg strings: String) {
        val bitmaps = arrayOfNulls<Bitmap>(3)
        val imageId = strings[0]
        bitmaps[0] = getBitmapFromURL(strings[1])
        bitmaps[1] = getBitmapFromURL(strings[2])
        bitmaps[2] = getBitmapFromURL(strings[3])
        saveBitmap(context, bitmaps[0]!!, imageId + BitmapUtils.SUFFIX_PREVIEW + BitmapUtils.IMAGE_EXTENSION)
        saveBitmap(context, bitmaps[1]!!, imageId + BitmapUtils.SUFFIX_WEBFORMAT + BitmapUtils.IMAGE_EXTENSION)
        saveBitmap(context, bitmaps[2]!!, imageId + BitmapUtils.SUFFIX_LARGEIMAGE + BitmapUtils.IMAGE_EXTENSION)
    }

    override fun onPostExecute(result: Unit?) {
        listener.onImagesDownloaded()
        super.onPostExecute(result)
    }

}