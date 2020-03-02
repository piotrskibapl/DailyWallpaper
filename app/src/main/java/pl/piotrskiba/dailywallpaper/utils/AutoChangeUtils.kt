package pl.piotrskiba.dailywallpaper.utils

import android.content.Context
import androidx.preference.PreferenceManager
import com.firebase.jobdispatcher.*
import pl.piotrskiba.dailywallpaper.R
import pl.piotrskiba.dailywallpaper.sync.AutoWallpaperFirebaseJobService
import timber.log.Timber

object AutoChangeUtils {
    private const val TAG = "auto_wallpaper_change"
    private var mInterval = 0
    @JvmStatic
    @Synchronized
    fun scheduleWallpaperChanger(context: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val interval = Integer.valueOf(sharedPreferences.getString(context.getString(R.string.pref_interval_key), "0")!!)
        if (mInterval == interval) return
        val driver: Driver = GooglePlayDriver(context)
        val dispatcher = FirebaseJobDispatcher(driver)
        mInterval = interval
        if (mInterval > 0) {
            dispatcher.cancel(TAG)
            val changeWallpaperJob = dispatcher.newJobBuilder()
                    .setService(AutoWallpaperFirebaseJobService::class.java)
                    .setTag(TAG)
                    .setLifetime(Lifetime.FOREVER)
                    .setRecurring(true)
                    .setConstraints(Constraint.ON_ANY_NETWORK)
                    .setTrigger(Trigger.executionWindow(mInterval, mInterval + 300))
                    .setReplaceCurrent(true)
                    .build()
            dispatcher.schedule(changeWallpaperJob)
            Timber.d("Scheduled auto wallpaper change every %d seconds", mInterval)
        } else {
            dispatcher.cancel(TAG)
            Timber.d("Cancelled auto wallpaper change")
        }
    }
}