package pl.piotrskiba.dailywallpaper.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.preference.PreferenceManager
import pl.piotrskiba.dailywallpaper.Constants
import pl.piotrskiba.dailywallpaper.R
import pl.piotrskiba.dailywallpaper.interfaces.ImageListLoadedListener
import pl.piotrskiba.dailywallpaper.models.ImageList
import pl.piotrskiba.dailywallpaper.network.PixabayAPIClient
import pl.piotrskiba.dailywallpaper.network.PixabayAPIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object NetworkUtils {

    /*
        get Bitmap from URL
        source: https://stackoverflow.com/questions/8992964/android-load-from-url-to-bitmap
    */
    @JvmStatic
    fun getBitmapFromURL(src: String): Bitmap? {
        return try {
            val url = URL(src)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            null
        }
    }

    fun getAllImages(context: Context, listener: ImageListLoadedListener) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val safesearch = sharedPreferences.getBoolean(context.getString(R.string.pref_safesearch_key), true)

        val api = PixabayAPIClient.retrofit.create(PixabayAPIInterface::class.java)
        api.loadAllImages(
                Constants.DEFAULT_VALUE_API_KEY,
                Constants.DEFAULT_VALUE_ORIENTATION,
                Constants.DEFAULT_VALUE_EDITORS_CHOICE,
                safesearch
        ).enqueue(object: Callback<ImageList> {
            override fun onFailure(call: Call<ImageList>, t: Throwable) {
                t.printStackTrace()
                listener.onImageListLoadingError()
            }

            override fun onResponse(call: Call<ImageList>, response: Response<ImageList>) {
                response.body()?.let {
                    listener.onImageListLoaded(response.body()!!)
                } ?: kotlin.run {
                    listener.onImageListLoadingError()
                }
            }
        })
    }

    fun getCategoryImages(context: Context, category: Int, listener: ImageListLoadedListener) {
        val allCategories = context.resources.getStringArray(R.array.pref_category_values)
        val categoryName = allCategories[category]

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val safesearch = sharedPreferences.getBoolean(context.getString(R.string.pref_safesearch_key), true)

        val api = PixabayAPIClient.retrofit.create(PixabayAPIInterface::class.java)
        api.loadCategoryImages(
                Constants.DEFAULT_VALUE_API_KEY,
                Constants.DEFAULT_VALUE_ORIENTATION,
                Constants.DEFAULT_VALUE_EDITORS_CHOICE,
                safesearch,
                categoryName
        ).enqueue(object: Callback<ImageList> {
            override fun onFailure(call: Call<ImageList>, t: Throwable) {
                t.printStackTrace()
                listener.onImageListLoadingError()
            }

            override fun onResponse(call: Call<ImageList>, response: Response<ImageList>) {
                response.body()?.let {
                    listener.onImageListLoaded(response.body()!!)
                } ?: kotlin.run {
                    listener.onImageListLoadingError()
                }
            }
        })
    }
}