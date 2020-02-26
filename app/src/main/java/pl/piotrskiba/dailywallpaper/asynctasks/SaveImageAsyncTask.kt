package pl.piotrskiba.dailywallpaper.asynctasks

import android.os.AsyncTask
import pl.piotrskiba.dailywallpaper.database.AppDatabase
import pl.piotrskiba.dailywallpaper.database.ImageEntry
import pl.piotrskiba.dailywallpaper.interfaces.ImageSavedListener

class SaveImageAsyncTask(private val mDb: AppDatabase, private val listener: ImageSavedListener) : AsyncTask<ImageEntry, Unit, Unit>() {
    override fun doInBackground(vararg imageEntries: ImageEntry?) {
        val imageEntry = imageEntries[0]!!
        mDb.imageDao().insertImage(imageEntry)
    }

    override fun onPostExecute(result: Unit?) {
        listener.onImageSaved()
        super.onPostExecute(result)
    }
}