package pl.piotrskiba.dailywallpaper.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "image")
public class ImageEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private final int imageId;
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
    private final Date updatedAt;

    @Ignore
    public ImageEntry(int imageId, String pageURL, String type, String tags, String previewURL,
                      int previewWidth, int previewHeight, String webformatURL, int webformatWidth,
                      int webformatHeight, String largeImageURL, int imageWidth, int imageHeight, int imageSize,
                      int views, int downloads, int favorites, int likes, int comments, int user_id, String user, String userImageURL, Date updatedAt){

        this.imageId = imageId;
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
        this.updatedAt = updatedAt;
    }

    public ImageEntry(int id, int imageId, String pageURL, String type, String tags, String previewURL,
                      int previewWidth, int previewHeight, String webformatURL, int webformatWidth,
                      int webformatHeight, String largeImageURL, int imageWidth, int imageHeight, int imageSize,
                      int views, int downloads, int favorites, int likes, int comments, int user_id, String user, String userImageURL, Date updatedAt){

        this.id = id;
        this.imageId = imageId;
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
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public int getImageId() {
        return imageId;
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

    public Date getUpdatedAt() {
        return updatedAt;
    }
}
