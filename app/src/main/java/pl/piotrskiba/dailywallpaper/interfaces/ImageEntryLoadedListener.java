package pl.piotrskiba.dailywallpaper.interfaces;

import pl.piotrskiba.dailywallpaper.database.ImageEntry;

public interface ImageEntryLoadedListener {

    void onImageEntryLoaded(ImageEntry imageEntry);
}
