package pl.piotrskiba.dailywallpaper.interfaces;

public interface AsyncTaskCompleteListener<T> {

    void onTaskCompleted(T result);
}
