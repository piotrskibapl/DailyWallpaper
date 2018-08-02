package pl.piotrskiba.dailywallpaper.models;

public class ImageList {
    private final int total;
    private final int totalHits;
    private final Image[] hits;

    public ImageList(int total, int totalHits, Image[] hits) {
        this.total = total;
        this.totalHits = totalHits;
        this.hits = hits;
    }

    public int getTotal() {
        return total;
    }

    public int getTotalHits() {
        return totalHits;
    }

    public Image[] getHits() {
        return hits;
    }
}
