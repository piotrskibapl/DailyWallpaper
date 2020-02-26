package pl.piotrskiba.dailywallpaper.asynctasks

import android.content.Context
import android.os.AsyncTask
import android.preference.PreferenceManager
import com.google.gson.Gson
import pl.piotrskiba.dailywallpaper.R
import pl.piotrskiba.dailywallpaper.interfaces.ImageListLoadedListener
import pl.piotrskiba.dailywallpaper.models.ImageList
import pl.piotrskiba.dailywallpaper.utils.NetworkUtils.buildUrl
import pl.piotrskiba.dailywallpaper.utils.NetworkUtils.getHttpResponse
import timber.log.Timber
import java.io.IOException

class FetchImagesAsyncTask(private val context: Context, private val listener: ImageListLoadedListener) : AsyncTask<String, Unit, ImageList>() {
    override fun onPreExecute() {
        super.onPreExecute()
    }

    protected override fun doInBackground(vararg strings: String): ImageList? {
        var category: String? = null
        if (strings.isNotEmpty()) category = strings[0]
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val safesearch = sharedPreferences.getBoolean(context.getString(R.string.pref_safesearch_key), true)
        val url = buildUrl(category, safesearch)
        if (category == null) Timber.d("Loading images from all categories, from URL: %s", url.toString()) else Timber.d("Loading images from category %s, from URL: %s", category, url.toString())
        return try {
            val json = getHttpResponse(url!!)
            val gson = Gson()
            gson.fromJson(json, ImageList::class.java)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    override fun onPostExecute(imageList: ImageList?) {
        listener.onImageListLoaded(imageList!!)
        super.onPostExecute(imageList)
    }

}