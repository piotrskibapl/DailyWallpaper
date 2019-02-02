package pl.piotrskiba.dailywallpaper.asynctasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import pl.piotrskiba.dailywallpaper.interfaces.ImagesDownloadedListener;
import pl.piotrskiba.dailywallpaper.utils.BitmapUtils;
import pl.piotrskiba.dailywallpaper.utils.NetworkUtils;

public class DownloadImagesAsyncTask extends AsyncTask<String, Void, Void> {

    private final Context context;
    private final ImagesDownloadedListener listener;

    public DownloadImagesAsyncTask(Context context, ImagesDownloadedListener listener){
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(String... strings) {

        Bitmap[] bitmaps = new Bitmap[3];
        String imageId = strings[0];

        bitmaps[0] = NetworkUtils.getBitmapFromURL(strings[1]);
        bitmaps[1] = NetworkUtils.getBitmapFromURL(strings[2]);
        bitmaps[2] = NetworkUtils.getBitmapFromURL(strings[3]);

        BitmapUtils.saveBitmap(context, bitmaps[0], imageId + BitmapUtils.SUFFIX_PREVIEW + BitmapUtils.IMAGE_EXTENSION);
        BitmapUtils.saveBitmap(context, bitmaps[1], imageId + BitmapUtils.SUFFIX_WEBFORMAT + BitmapUtils.IMAGE_EXTENSION);
        BitmapUtils.saveBitmap(context, bitmaps[2], imageId + BitmapUtils.SUFFIX_LARGEIMAGE + BitmapUtils.IMAGE_EXTENSION);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        listener.onImagesDownloaded();
        super.onPostExecute(aVoid);
    }
}
