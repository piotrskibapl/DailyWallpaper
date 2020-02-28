package pl.piotrskiba.dailywallpaper.sync

import android.graphics.Bitmap
import android.preference.PreferenceManager
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import pl.piotrskiba.dailywallpaper.R
import pl.piotrskiba.dailywallpaper.interfaces.ImageListLoadedListener
import pl.piotrskiba.dailywallpaper.models.ImageList
import pl.piotrskiba.dailywallpaper.utils.NetworkUtils
import pl.piotrskiba.dailywallpaper.utils.WallpaperUtils
import java.util.*

class AutoWallpaperFirebaseJobService : JobService() {
    private val rnd = Random()
    private var jobParameters: JobParameters? = null

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

                Observable.fromCallable {
                    adjustAndSetWallpaper(NetworkUtils.getBitmapFromURL(wallpaperURL)!!)
                }.subscribeOn(Schedulers.io()).subscribe()
            }

            override fun onImageListLoadingError() {
            }

        })

        return true
    }

    private fun adjustAndSetWallpaper(loadedBitmap: Bitmap) { // get screen dimensions
        val adjustedBitmap = WallpaperUtils.adjustBitmapForWallpaper(this, loadedBitmap)

        Completable.fromCallable {
            WallpaperUtils.changeWallpaper(this, adjustedBitmap)
            jobFinished(jobParameters!!, false)
        }.subscribeOn(Schedulers.io()).subscribe()
    }

    override fun onStopJob(job: JobParameters?): Boolean {
        return true
    }
}