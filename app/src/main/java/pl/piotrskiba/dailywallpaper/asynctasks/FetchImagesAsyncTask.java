package pl.piotrskiba.dailywallpaper.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URL;

import pl.piotrskiba.dailywallpaper.interfaces.AsyncTaskCompleteListener;
import pl.piotrskiba.dailywallpaper.models.ImageList;
import pl.piotrskiba.dailywallpaper.utils.NetworkUtils;
import timber.log.Timber;

public class FetchImagesAsyncTask extends AsyncTask<String, Void, ImageList>{

    private final Context context;
    private final AsyncTaskCompleteListener<ImageList> listener;

    public FetchImagesAsyncTask(Context context, AsyncTaskCompleteListener<ImageList> listener){
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

        URL url = NetworkUtils.buildUrl(category);
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
        listener.onTaskCompleted(imageList);
        super.onPostExecute(imageList);
    }
}
