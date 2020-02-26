package pl.piotrskiba.dailywallpaper.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import pl.piotrskiba.dailywallpaper.BuildConfig
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

object NetworkUtils {
    private const val API_KEY = BuildConfig.API_KEY
    private const val BASE_API_URL = "https://pixabay.com/api/"
    private const val PARAM_API_KEY = "key"
    private const val PARAM_ORIENTATION = "orientation"
    private const val PARAM_CATEGORY = "category"
    private const val PARAM_EDITORS_CHOICE = "editors_choice"
    private const val PARAM_SAFE_SEARCH = "safesearch"
    private const val PARAM_PER_PAGE = "per_page"
    private const val VALUE_ORIENTATION = "vertical"
    private const val VALUE_EDITORS_CHOICE = "true"
    private const val VALUE_PER_PAGE = "200"
    @JvmStatic
    fun buildUrl(category: String?, safesearch: Boolean): URL? {
        val uriBuilder = Uri.parse(BASE_API_URL).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .appendQueryParameter(PARAM_ORIENTATION, VALUE_ORIENTATION)
                .appendQueryParameter(PARAM_EDITORS_CHOICE, VALUE_EDITORS_CHOICE)
                .appendQueryParameter(PARAM_SAFE_SEARCH, safesearch.toString())
                .appendQueryParameter(PARAM_PER_PAGE, VALUE_PER_PAGE)
        if (category != null) {
            uriBuilder.appendQueryParameter(PARAM_CATEGORY, category)
        }
        val uri = uriBuilder.build()
        var builtUrl: URL? = null
        try {
            builtUrl = URL(uri.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return builtUrl
    }

    @JvmStatic
    @Throws(IOException::class)
    fun getHttpResponse(url: URL): String? {
        val connection = url.openConnection() as HttpURLConnection
        return try {
            val icStream = connection.inputStream
            val scanner = Scanner(icStream)
            scanner.useDelimiter("\\A")
            if (scanner.hasNext()) {
                scanner.next()
            } else {
                null
            }
        } finally {
            connection.disconnect()
        }
    }

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
}