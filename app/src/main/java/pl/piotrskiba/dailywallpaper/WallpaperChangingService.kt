package pl.piotrskiba.dailywallpaper

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.media.ThumbnailUtils
import android.os.Handler
import android.view.WindowManager
import android.widget.Toast
import androidx.preference.PreferenceManager
import pl.piotrskiba.dailywallpaper.asynctasks.GetBitmapFromUrlAsyncTask
import pl.piotrskiba.dailywallpaper.asynctasks.SetWallpaperAsyncTask
import pl.piotrskiba.dailywallpaper.interfaces.BitmapLoadedListener
import pl.piotrskiba.dailywallpaper.interfaces.ImageListLoadedListener
import pl.piotrskiba.dailywallpaper.interfaces.WallpaperSetListener
import pl.piotrskiba.dailywallpaper.models.ImageList
import pl.piotrskiba.dailywallpaper.utils.NetworkUtils
import java.util.*

class WallpaperChangingService : IntentService, BitmapLoadedListener, WallpaperSetListener {
    private val rnd = Random()
    private var mToast: Toast? = null

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    constructor(name: String?) : super(name) {}

    constructor() : super("WallpaperChangingService") {}

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (action == ACTION_CHANGE_WALLPAPER) {
                handleActionChangeWallpaper()
            }
        }
    }

    private fun handleActionChangeWallpaper() {
        if (!inProgress) {
            inProgress = true
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

            val category = sharedPreferences.getString(getString(R.string.pref_category_key), "")
            val allCategories = resources.getStringArray(R.array.pref_category_values)
            val categoryIndex = allCategories.indexOf(category)

            val bitmapLoadedListener: BitmapLoadedListener = this

            NetworkUtils.getCategoryImages(this, categoryIndex, object : ImageListLoadedListener{
                override fun onImageListLoaded(result: ImageList) {
                    val max: Int = result.hits.size
                    val randomInt = rnd.nextInt(max + 1)
                    val wallpaperURL = result.hits[randomInt]!!.largeImageURL
                    GetBitmapFromUrlAsyncTask(bitmapLoadedListener).execute(wallpaperURL)
                }

                override fun onImageListLoadingError() {
                }

            })

            // create a handler to post messages to the main thread
            // source: https://stackoverflow.com/questions/20059188/java-lang-runtimeexception-handler-android-os-handler-sending-message-to-a-ha
            val mHandler = Handler(mainLooper)
            mHandler.post {
                mToast?.cancel()
                mToast = Toast.makeText(applicationContext, getString(R.string.setting_wallpaper), Toast.LENGTH_LONG)
                mToast?.show()
            }
        }
    }

    override fun onBitmapLoaded(loadedBitmap: Bitmap) {
        // get screen dimensions
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        // center crop the image
        val bitmap = ThumbnailUtils.extractThumbnail(loadedBitmap, size.x, size.y)
        // set image as wallpaper
        SetWallpaperAsyncTask(this, this).execute(bitmap)
    }

    override fun onWallpaperSet(success: Boolean) {
        mToast?.cancel()
        mToast = Toast.makeText(this, getString(R.string.wallpaper_set), Toast.LENGTH_SHORT)
        mToast?.show()
        inProgress = false
    }

    companion object {
        const val ACTION_CHANGE_WALLPAPER = "pl.piotrskiba.dailywallpaper.action.change_wallpaper"
        private var inProgress = false
    }
}