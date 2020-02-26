package pl.piotrskiba.dailywallpaper;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Random;

import pl.piotrskiba.dailywallpaper.asynctasks.FetchImagesAsyncTask;
import pl.piotrskiba.dailywallpaper.asynctasks.GetBitmapFromUrlAsyncTask;
import pl.piotrskiba.dailywallpaper.asynctasks.SetWallpaperAsyncTask;
import pl.piotrskiba.dailywallpaper.interfaces.BitmapLoadedListener;
import pl.piotrskiba.dailywallpaper.interfaces.ImageListLoadedListener;
import pl.piotrskiba.dailywallpaper.interfaces.WallpaperSetListener;
import pl.piotrskiba.dailywallpaper.models.ImageList;

public class WallpaperChangingService extends IntentService implements ImageListLoadedListener, BitmapLoadedListener, WallpaperSetListener {

    public static final String ACTION_CHANGE_WALLPAPER = "pl.piotrskiba.dailywallpaper.action.change_wallpaper";

    private Random rnd = new Random();

    private Toast mToast;

    private static boolean inProgress = false;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public WallpaperChangingService(String name) {
        super(name);
    }

    public WallpaperChangingService(){
        super("WallpaperChangingService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action.equals(ACTION_CHANGE_WALLPAPER)) {
                handleActionChangeWallpaper();
            }
        }
    }

    private void handleActionChangeWallpaper(){
        if(!inProgress) {
            inProgress = true;

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String category = sharedPreferences.getString(getString(R.string.pref_category_key), "");

            new FetchImagesAsyncTask(this, this).execute(category);

            // create a handler to post messages to the main thread
            // source: https://stackoverflow.com/questions/20059188/java-lang-runtimeexception-handler-android-os-handler-sending-message-to-a-ha
            Handler mHandler = new Handler(getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(mToast != null)
                        mToast.cancel();

                    mToast = Toast.makeText(getApplicationContext(), getString(R.string.setting_wallpaper), Toast.LENGTH_LONG);
                    mToast.show();
                }
            });
        }
    }

    @Override
    public void onImageListLoaded(ImageList result) {
        if(result != null) {
            int max = result.getHits().length;

            int randomInt = rnd.nextInt(max + 1);
            String wallpaperURL = result.getHits()[randomInt].getLargeImageURL();

            new GetBitmapFromUrlAsyncTask(this).execute(wallpaperURL);
        }
        else{
            inProgress = false;
        }
    }

    @Override
    public void onBitmapLoaded(Bitmap loadedBitmap) {
        if(loadedBitmap != null) {
            // get screen dimensions
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            // center crop the image
            Bitmap bitmap = ThumbnailUtils.extractThumbnail(loadedBitmap, size.x, size.y);

            // set image as wallpaper
            new SetWallpaperAsyncTask(this, this).execute(bitmap);
        }
        else{
            inProgress = false;
        }
    }

    @Override
    public void onWallpaperSet(Boolean success) {
        if(mToast != null)
            mToast.cancel();

        mToast = Toast.makeText(this, getString(R.string.wallpaper_set), Toast.LENGTH_SHORT);
        mToast.show();

        inProgress = false;
    }
}
