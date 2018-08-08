package pl.piotrskiba.dailywallpaper.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import pl.piotrskiba.dailywallpaper.R;
import pl.piotrskiba.dailywallpaper.sync.AutoWallpaperFirebaseJobService;
import timber.log.Timber;

public class AutoChangeUtils {

    private final static String TAG = "auto_wallpaper_change";

    private static int mInterval;

    synchronized public static void scheduleWallpaperChanger(Context context){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int interval = Integer.valueOf(sharedPreferences.getString(context.getString(R.string.pref_interval_key), "0"));

        if(mInterval == interval) return;
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        mInterval = interval;

        if(mInterval > 0) {
            dispatcher.cancel(TAG);

            Job changeWallpaperJob = dispatcher.newJobBuilder()
                    .setService(AutoWallpaperFirebaseJobService.class)
                    .setTag(TAG)
                    .setLifetime(Lifetime.FOREVER)
                    .setRecurring(true)
                    .setConstraints(Constraint.ON_ANY_NETWORK)
                    .setTrigger(Trigger.executionWindow(mInterval, mInterval + 300))
                    .setReplaceCurrent(true)
                    .build();

            dispatcher.schedule(changeWallpaperJob);
            Timber.d("Scheduled auto wallpaper change every %d seconds", mInterval);
        }
        else{
            dispatcher.cancel(TAG);
            Timber.d("Cancelled auto wallpaper change");
        }
    }
}
