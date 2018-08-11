package pl.piotrskiba.dailywallpaper.interfaces;

import android.view.View;

import pl.piotrskiba.dailywallpaper.models.Image;

public interface ImageClickListener {

    void onImageClick(Image clickedImage, View view);
}
