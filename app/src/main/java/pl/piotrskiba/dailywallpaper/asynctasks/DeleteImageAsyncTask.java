package pl.piotrskiba.dailywallpaper.asynctasks;

import android.os.AsyncTask;

import pl.piotrskiba.dailywallpaper.database.AppDatabase;
import pl.piotrskiba.dailywallpaper.database.ImageEntry;
import pl.piotrskiba.dailywallpaper.interfaces.ImageDeletedListener;

public class DeleteImageAsyncTask extends AsyncTask<ImageEntry, Void, Void> {

    private AppDatabase mDb;
    private ImageDeletedListener listener;

    public DeleteImageAsyncTask(AppDatabase db, ImageDeletedListener listener){
        this.mDb = db;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(ImageEntry... imageEntries) {
        ImageEntry imageEntry = imageEntries[0];
        if(imageEntry != null) {
            mDb.imageDao().deleteImage(imageEntry);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        listener.onImageDeleted();
        super.onPostExecute(aVoid);
    }
}
