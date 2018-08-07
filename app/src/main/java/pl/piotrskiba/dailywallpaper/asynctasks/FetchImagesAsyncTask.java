package pl.piotrskiba.dailywallpaper.asynctasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URL;

import pl.piotrskiba.dailywallpaper.R;
import pl.piotrskiba.dailywallpaper.interfaces.ImageListLoadedListener;
import pl.piotrskiba.dailywallpaper.models.ImageList;
import pl.piotrskiba.dailywallpaper.utils.NetworkUtils;
import timber.log.Timber;

public class FetchImagesAsyncTask extends AsyncTask<String, Void, ImageList>{

    private final Context context;
    private final ImageListLoadedListener listener;

    public FetchImagesAsyncTask(Context context, ImageListLoadedListener listener){
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ImageList doInBackground(String... strings) {

        String category = null;

        if(strings.length > 0)
            category = strings[0];

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean safesearch = sharedPreferences.getBoolean(context.getString(R.string.pref_safesearch_key), true);

        URL url = NetworkUtils.buildUrl(category, safesearch);
        if(category == null)
            Timber.d("Loading images from all categories, from URL: %s", url.toString());
        else
            Timber.d("Loading images from category %s, from URL: %s", category, url.toString());

        try {
            String json = NetworkUtils.getHttpResponse(url);

            Gson gson = new Gson();
            ImageList imageList = gson.fromJson(json, ImageList.class);

            return imageList;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(ImageList imageList) {
        listener.onImageListLoaded(imageList);
        super.onPostExecute(imageList);
    }
}
