package pl.piotrskiba.dailywallpaper.asynctasks;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;

import java.io.IOException;

import pl.piotrskiba.dailywallpaper.R;
import pl.piotrskiba.dailywallpaper.interfaces.AsyncTaskCompleteListener;
import pl.piotrskiba.dailywallpaper.models.ImageList;

public class SetWallpaperAsyncTask extends AsyncTask<Bitmap, Void, Boolean> {

    private final Context context;
    private final AsyncTaskCompleteListener<Boolean> listener;

    public SetWallpaperAsyncTask(Context context, AsyncTaskCompleteListener<Boolean> listener){
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Bitmap... bitmaps) {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);

        try {
            wallpaperManager.setBitmap(bitmaps[0]);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        listener.onTaskCompleted(aBoolean);
        super.onPostExecute(aBoolean);
    }
}
