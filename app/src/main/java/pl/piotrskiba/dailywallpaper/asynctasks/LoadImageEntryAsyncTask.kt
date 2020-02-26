package pl.piotrskiba.dailywallpaper.asynctasks

import android.os.AsyncTask
import pl.piotrskiba.dailywallpaper.database.AppDatabase
import pl.piotrskiba.dailywallpaper.database.ImageEntry
import pl.piotrskiba.dailywallpaper.interfaces.ImageEntryLoadedListener
import timber.log.Timber

class LoadImageEntryAsyncTask(private val mDb: AppDatabase, private val listener: ImageEntryLoadedListener) : AsyncTask<Int, Void, ImageEntry>() {

    override fun doInBackground(vararg integers: Int?): ImageEntry? {
        val imageId = integers[0]!!
        val imageEntries = mDb.imageDao().loadImagesByImageId(imageId)

        if(imageEntries.isEmpty())
            return null;
        return imageEntries[0];
    }

    override fun onPostExecute(imageEntry: ImageEntry?) {
        listener.onImageEntryLoaded(imageEntry)
        super.onPostExecute(imageEntry)
    }
}