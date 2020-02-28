package pl.piotrskiba.dailywallpaper

import android.app.IntentService
import android.content.Intent
import android.graphics.Bitmap
import android.os.Handler
import android.widget.Toast
import androidx.preference.PreferenceManager
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import pl.piotrskiba.dailywallpaper.interfaces.ImageListLoadedListener
import pl.piotrskiba.dailywallpaper.models.ImageList
import pl.piotrskiba.dailywallpaper.utils.NetworkUtils
import pl.piotrskiba.dailywallpaper.utils.WallpaperUtils
import java.util.*

class WallpaperChangingService : IntentService {
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

            NetworkUtils.getCategoryImages(this, categoryIndex, object : ImageListLoadedListener{
                override fun onImageListLoaded(result: ImageList) {
                    val max: Int = result.hits.size
                    val randomInt = rnd.nextInt(max + 1)
                    val wallpaperURL = result.hits[randomInt]!!.largeImageURL

                    Observable.fromCallable {
                        adjustAndSetWallpaper(NetworkUtils.getBitmapFromURL(wallpaperURL)!!)
                    }.subscribeOn(Schedulers.io()).subscribe()
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

    private fun adjustAndSetWallpaper(loadedBitmap: Bitmap) {
        val adjustedBitmap = WallpaperUtils.adjustBitmapForWallpaper(this, loadedBitmap)

        Completable.fromCallable {
            if(WallpaperUtils.changeWallpaper(this, adjustedBitmap)){
                val mHandler = Handler(mainLooper)
                mHandler.post {
                    mToast?.cancel()
                    mToast = Toast.makeText(this, getString(R.string.wallpaper_set), Toast.LENGTH_SHORT)
                    mToast?.show()
                }
                inProgress = false
            }
        }.subscribeOn(Schedulers.io()).subscribe()
    }

    companion object {
        const val ACTION_CHANGE_WALLPAPER = "pl.piotrskiba.dailywallpaper.action.change_wallpaper"
        private var inProgress = false
    }
}