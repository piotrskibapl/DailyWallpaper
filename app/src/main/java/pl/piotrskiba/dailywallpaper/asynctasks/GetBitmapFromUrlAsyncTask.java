package pl.piotrskiba.dailywallpaper.asynctasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import pl.piotrskiba.dailywallpaper.interfaces.BitmapLoadedListener;
import pl.piotrskiba.dailywallpaper.utils.NetworkUtils;

public class GetBitmapFromUrlAsyncTask extends AsyncTask<String, Void, Bitmap> {

    private BitmapLoadedListener listener;

    public GetBitmapFromUrlAsyncTask(BitmapLoadedListener listener){
        this.listener = listener;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String url = strings[0];
        return NetworkUtils.getBitmapFromURL(url);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        listener.onBitmapLoaded(bitmap);
        super.onPostExecute(bitmap);
    }
}
