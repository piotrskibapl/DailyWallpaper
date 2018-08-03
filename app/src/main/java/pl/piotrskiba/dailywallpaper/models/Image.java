package pl.piotrskiba.dailywallpaper.models;

import java.io.Serializable;

public class Image implements Serializable {
    private final int id;
    private final String pageURL;
    private final String type;
    private final String tags;
    private final String previewURL;
    private final int previewWidth;
    private final int previewHeight;
    private final String webformatURL;
    private final int webformatWidth;
    private final int webformatHeight;
    private final String largeImageURL;
    private final int imageWidth;
    private final int imageHeight;
    private final int imageSize;
    private final int views;
    private final int downloads;
    private final int favorites;
    private final int likes;
    private final int comments;
    private final int user_id;
    private final String user;
    private final String userImageURL;

    public Image(int id, String pageURL, String type, String tags, String previewURL,
        int previewWidth, int previewHeight, String webformatURL, int webformatWidth,
        int webformatHeight, String largeImageURL, int imageWidth, int imageHeight, int imageSize,
        int views, int downloads, int favorites, int likes, int comments, int user_id, String user, String userImageURL){

        this.id = id;
        this.pageURL = pageURL;
        this.type = type;
        this.tags = tags;
        this.previewURL = previewURL;
        this.previewWidth = previewWidth;
        this.previewHeight = previewHeight;
        this.webformatURL = webformatURL;
        this.webformatWidth = webformatWidth;
        this.webformatHeight = webformatHeight;
        this.largeImageURL = largeImageURL;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.imageSize = imageSize;
        this.views = views;
        this.downloads = downloads;
        this.favorites = favorites;
        this.likes = likes;
        this.comments = comments;
        this.user_id = user_id;
        this.user = user;
        this.userImageURL = userImageURL;
    }

    public int getId() {
        return id;
    }

    public String getPageURL() {
        return pageURL;
    }

    public String getType() {
        return type;
    }

    public String getTags() {
        return tags;
    }

    public String getPreviewURL() {
        return previewURL;
    }

    public int getPreviewWidth() {
        return previewWidth;
    }

    public int getPreviewHeight() {
        return previewHeight;
    }

    public String getWebformatURL() {
        return webformatURL;
    }

    public int getWebformatWidth() {
        return webformatWidth;
    }

    public int getWebformatHeight() {
        return webformatHeight;
    }

    public String getLargeImageURL() {
        return largeImageURL;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public int getImageSize() {
        return imageSize;
    }

    public int getViews() {
        return views;
    }

    public int getDownloads() {
        return downloads;
    }

    public int getFavorites() {
        return favorites;
    }

    public int getLikes() {
        return likes;
    }

    public int getComments() {
        return comments;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getUser() {
        return user;
    }

    public String getUserImageURL() {
        return userImageURL;
    }
}
