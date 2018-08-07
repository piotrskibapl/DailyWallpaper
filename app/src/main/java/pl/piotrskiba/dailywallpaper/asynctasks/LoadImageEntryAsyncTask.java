package pl.piotrskiba.dailywallpaper.asynctasks;

import android.os.AsyncTask;

import java.util.List;

import pl.piotrskiba.dailywallpaper.database.AppDatabase;
import pl.piotrskiba.dailywallpaper.database.ImageEntry;
import pl.piotrskiba.dailywallpaper.interfaces.ImageEntryLoadedListener;

public class LoadImageEntryAsyncTask extends AsyncTask<Integer, Void, ImageEntry> {

    private AppDatabase mDb;
    private ImageEntryLoadedListener listener;

    public LoadImageEntryAsyncTask(AppDatabase db, ImageEntryLoadedListener listener){
        this.mDb = db;
        this.listener = listener;
    }

    @Override
    protected ImageEntry doInBackground(Integer... integers) {
        int imageId = integers[0];

        List<ImageEntry> imageEntries = mDb.imageDao().loadImagesByImageId(imageId);

        if(imageEntries.size() > 0)
            return imageEntries.get(0);
        else
            return null;
    }

    @Override
    protected void onPostExecute(ImageEntry imageEntry) {
        listener.onImageEntryLoaded(imageEntry);
        super.onPostExecute(imageEntry);
    }
}
