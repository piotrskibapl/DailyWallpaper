package pl.piotrskiba.dailywallpaper.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import timber.log.Timber;

public class BitmapUtils {

    public final static String SUFFIX_PREVIEW = "_preview";
    public final static String SUFFIX_WEBFORMAT = "_webformat";
    public final static String SUFFIX_LARGEIMAGE = "_largeImage";
    public final static String IMAGE_EXTENSION = ".png";

    public static void saveBitmap(Context context, Bitmap bitmap, String imageName) {
        FileOutputStream foStream;
        try {
            foStream = context.openFileOutput(imageName, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, foStream);
            foStream.close();
        } catch (Exception e) {
            Timber.d("Could not save an image.");
            e.printStackTrace();
        }
    }

    public static Bitmap loadBitmap(Context context, String imageName) {
        Bitmap bitmap = null;
        FileInputStream fiStream;
        try {
            fiStream = context.openFileInput(imageName);
            bitmap = BitmapFactory.decodeStream(fiStream);
            fiStream.close();
        } catch (Exception e) {
            Timber.d("Could not load an image.");
            e.printStackTrace();
        }
        return bitmap;
    }
}