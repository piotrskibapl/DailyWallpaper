package pl.piotrskiba.dailywallpaper.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.WindowManager;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.Random;

import pl.piotrskiba.dailywallpaper.R;
import pl.piotrskiba.dailywallpaper.asynctasks.FetchImagesAsyncTask;
import pl.piotrskiba.dailywallpaper.asynctasks.GetBitmapFromUrlAsyncTask;
import pl.piotrskiba.dailywallpaper.asynctasks.SetWallpaperAsyncTask;
import pl.piotrskiba.dailywallpaper.interfaces.BitmapLoadedListener;
import pl.piotrskiba.dailywallpaper.interfaces.ImageListLoadedListener;
import pl.piotrskiba.dailywallpaper.interfaces.WallpaperSetListener;
import pl.piotrskiba.dailywallpaper.models.ImageList;

public class AutoWallpaperFirebaseJobService extends JobService implements ImageListLoadedListener, BitmapLoadedListener, WallpaperSetListener
{
    private Random rnd = new Random();
    private JobParameters jobParameters;

    private FetchImagesAsyncTask fetchImagesAsyncTask = new FetchImagesAsyncTask(this, this);
    private GetBitmapFromUrlAsyncTask getBitmapFromUrlAsyncTask = new GetBitmapFromUrlAsyncTask(this);
    private SetWallpaperAsyncTask setWallpaperAsyncTask = new SetWallpaperAsyncTask(this, this);

    @Override
    public boolean onStartJob(JobParameters job) {
        jobParameters = job;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String category = sharedPreferences.getString(getString(R.string.pref_category_key), "");

        fetchImagesAsyncTask.execute(category);

        return true;
    }

    @Override
    public void onImageListLoaded(ImageList result) {
        int max = result.getHits().length;

        int randomInt = rnd.nextInt(max+1);
        String wallpaperURL = result.getHits()[randomInt].getLargeImageURL();

        getBitmapFromUrlAsyncTask.execute(wallpaperURL);
    }

    @Override
    public void onBitmapLoaded(Bitmap loadedBitmap) {

        // get screen dimensions
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // center crop the image
        Bitmap bitmap = ThumbnailUtils.extractThumbnail(loadedBitmap, size.x, size.y);

        // set image as wallpaper
        setWallpaperAsyncTask.execute(bitmap);
    }

    @Override
    public void onWallpaperSet(Boolean success) {
        jobFinished(jobParameters, false);
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if(fetchImagesAsyncTask != null)
            fetchImagesAsyncTask.cancel(true);

        if(getBitmapFromUrlAsyncTask != null)
            getBitmapFromUrlAsyncTask.cancel(true);

        if(setWallpaperAsyncTask != null)
            setWallpaperAsyncTask.cancel(true);

        return true;
    }
}
