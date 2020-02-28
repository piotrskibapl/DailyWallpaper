package pl.piotrskiba.dailywallpaper.sync

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.media.ThumbnailUtils
import android.preference.PreferenceManager
import android.view.WindowManager
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import pl.piotrskiba.dailywallpaper.R
import pl.piotrskiba.dailywallpaper.asynctasks.GetBitmapFromUrlAsyncTask
import pl.piotrskiba.dailywallpaper.interfaces.BitmapLoadedListener
import pl.piotrskiba.dailywallpaper.interfaces.ImageListLoadedListener
import pl.piotrskiba.dailywallpaper.models.ImageList
import pl.piotrskiba.dailywallpaper.utils.NetworkUtils
import pl.piotrskiba.dailywallpaper.utils.WallpaperUtils
import java.util.*

class AutoWallpaperFirebaseJobService : JobService(), BitmapLoadedListener {
    private val rnd = Random()
    private var jobParameters: JobParameters? = null
    private val getBitmapFromUrlAsyncTask: GetBitmapFromUrlAsyncTask? = GetBitmapFromUrlAsyncTask(this)
    override fun onStartJob(job: JobParameters): Boolean {
        jobParameters = job
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        val category = sharedPreferences.getString(getString(R.string.pref_category_key), "")
        val allCategories = resources.getStringArray(R.array.pref_category_values)
        val categoryIndex = allCategories.indexOf(category)

        NetworkUtils.getCategoryImages(this, categoryIndex, object : ImageListLoadedListener{
            override fun onImageListLoaded(result: ImageList) {
                val max: Int = result.hits.size
                val randomInt = rnd.nextInt(max + 1)
                val wallpaperURL = result.hits[randomInt]!!.largeImageURL
                getBitmapFromUrlAsyncTask!!.execute(wallpaperURL)
            }

            override fun onImageListLoadingError() {
            }

        })

        return true
    }

    override fun onBitmapLoaded(loadedBitmap: Bitmap) { // get screen dimensions
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        // center crop the image
        val bitmap = ThumbnailUtils.extractThumbnail(loadedBitmap, size.x, size.y)
        // set image as wallpaper
        val context = this
        Completable.fromCallable {
            WallpaperUtils.changeWallpaper(context, bitmap)
            jobFinished(jobParameters!!, false)
        }.subscribeOn(Schedulers.io()).subscribe()
    }

    override fun onStopJob(job: JobParameters): Boolean {
        getBitmapFromUrlAsyncTask?.cancel(true)
        return true
    }
}