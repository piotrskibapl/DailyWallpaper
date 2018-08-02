package pl.piotrskiba.dailywallpaper.utils;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import pl.piotrskiba.dailywallpaper.BuildConfig;

public class NetworkUtils {

    private final static String API_KEY = BuildConfig.API_KEY;

    private final static String BASE_API_URL = "https://pixabay.com/api/";

    private final static String PARAM_API_KEY = "key";
    private final static String PARAM_IMAGE_TYPE = "image_type";
    private final static String PARAM_ORIENTATION = "orientation";
    private final static String PARAM_CATEGORY = "category";
    private final static String PARAM_EDITORS_CHOICE = "editors_choice";
    private final static String PARAM_SAFE_SEARCH = "safesearch";
    private final static String PARAM_ORDER = "order";
    private final static String PARAM_PAGE = "page";
    private final static String PARAM_PER_PAGE = "per_page";

    private final static String VALUE_ORIENTATION = "vertical";
    private final static String VALUE_EDITORS_CHOICE = "true";
    private final static String VALUE_SAFE_SEARCH = "true";
    private final static String VALUE_PER_PAGE = "200";

    public static URL buildUrl(String category){
        Uri.Builder uriBuilder = Uri.parse(BASE_API_URL).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .appendQueryParameter(PARAM_ORIENTATION, VALUE_ORIENTATION)
                .appendQueryParameter(PARAM_EDITORS_CHOICE, VALUE_EDITORS_CHOICE)
                .appendQueryParameter(PARAM_SAFE_SEARCH, VALUE_SAFE_SEARCH)
                .appendQueryParameter(PARAM_PER_PAGE, VALUE_PER_PAGE);

        if(category != null){
            uriBuilder.appendQueryParameter(PARAM_CATEGORY, category);
        }

        Uri uri = uriBuilder.build();

        URL builtUrl = null;

        try {
            builtUrl = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        return builtUrl;
    }

    public static String getHttpResponse(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            InputStream icStream = connection.getInputStream();

            Scanner scanner = new Scanner(icStream);
            scanner.useDelimiter("\\A");

            if (scanner.hasNext()) {
                return scanner.next();
            } else {
                return null;
            }
        }
        finally{
            connection.disconnect();
        }
    }

}
