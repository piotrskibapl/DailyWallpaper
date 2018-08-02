package pl.piotrskiba.dailywallpaper.interfaces;

public interface AsyncTaskCompleteListener<T> {

    void onImageListRetrieved(T result);
}
