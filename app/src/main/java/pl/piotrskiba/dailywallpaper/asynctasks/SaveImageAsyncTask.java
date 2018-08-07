package pl.piotrskiba.dailywallpaper.asynctasks;

import android.os.AsyncTask;

import pl.piotrskiba.dailywallpaper.database.AppDatabase;
import pl.piotrskiba.dailywallpaper.database.ImageEntry;
import pl.piotrskiba.dailywallpaper.interfaces.ImageSavedListener;

public class SaveImageAsyncTask extends AsyncTask<ImageEntry, Void, Void> {

    private AppDatabase mDb;
    private final ImageSavedListener listener;

    public SaveImageAsyncTask(AppDatabase db, ImageSavedListener listener){
        this.mDb = db;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(ImageEntry... imageEntries) {
        ImageEntry imageEntry = imageEntries[0];
        mDb.imageDao().insertImage(imageEntry);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        listener.onImageSaved();
        super.onPostExecute(aVoid);
    }
}
