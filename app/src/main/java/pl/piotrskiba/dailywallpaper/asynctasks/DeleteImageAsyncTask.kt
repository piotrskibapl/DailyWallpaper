package pl.piotrskiba.dailywallpaper.asynctasks

import android.os.AsyncTask
import pl.piotrskiba.dailywallpaper.database.AppDatabase
import pl.piotrskiba.dailywallpaper.database.ImageEntry
import pl.piotrskiba.dailywallpaper.interfaces.ImageDeletedListener

class DeleteImageAsyncTask(private val mDb: AppDatabase, private val listener: ImageDeletedListener) : AsyncTask<ImageEntry, Unit, Unit>() {
    override fun doInBackground(vararg imageEntries: ImageEntry) {
        val imageEntry = imageEntries[0]
        mDb.imageDao().deleteImage(imageEntry)
    }

    override fun onPostExecute(result: Unit?) {
        listener.onImageDeleted()
        super.onPostExecute(result)
    }

}