package pl.piotrskiba.dailywallpaper.asynctasks;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.IOException;

import pl.piotrskiba.dailywallpaper.interfaces.WallpaperSetListener;

public class SetWallpaperAsyncTask extends AsyncTask<Bitmap, Void, Boolean> {

    private final Context context;
    private final WallpaperSetListener listener;

    public SetWallpaperAsyncTask(Context context, WallpaperSetListener listener){
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
        listener.onWallpaperSet(aBoolean);
        super.onPostExecute(aBoolean);
    }
}
