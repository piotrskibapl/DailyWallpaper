package pl.piotrskiba.dailywallpaper.asynctasks

import android.graphics.Bitmap
import android.os.AsyncTask
import pl.piotrskiba.dailywallpaper.interfaces.BitmapLoadedListener
import pl.piotrskiba.dailywallpaper.utils.NetworkUtils.getBitmapFromURL

class GetBitmapFromUrlAsyncTask(private val listener: BitmapLoadedListener) : AsyncTask<String, Void, Bitmap>() {
    override fun doInBackground(vararg strings: String?): Bitmap {
        val url = strings[0]!!
        return getBitmapFromURL(url)!!
    }

    override fun onPostExecute(bitmap: Bitmap?) {
        listener.onBitmapLoaded(bitmap!!)
        super.onPostExecute(bitmap)
    }

}