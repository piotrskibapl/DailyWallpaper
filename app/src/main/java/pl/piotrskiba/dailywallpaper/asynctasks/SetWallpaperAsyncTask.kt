package pl.piotrskiba.dailywallpaper.asynctasks

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import pl.piotrskiba.dailywallpaper.interfaces.WallpaperSetListener
import java.io.IOException

class SetWallpaperAsyncTask(private val context: Context, private val listener: WallpaperSetListener) : AsyncTask<Bitmap, Unit, Boolean>() {
    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg bitmaps: Bitmap): Boolean {
        val wallpaperManager = WallpaperManager.getInstance(context)
        try {
            wallpaperManager.setBitmap(bitmaps[0])
            return true
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    override fun onPostExecute(aBoolean: Boolean) {
        listener.onWallpaperSet(aBoolean)
        super.onPostExecute(aBoolean)
    }

}