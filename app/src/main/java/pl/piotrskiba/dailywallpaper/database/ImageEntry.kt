package pl.piotrskiba.dailywallpaper.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "image")
data class ImageEntry (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var imageId: Int,
    var pageURL: String,
    var type: String,
    var tags: String,
    var previewURL: String,
    var previewWidth: Int,
    var previewHeight: Int,
    var webformatURL: String,
    var webformatWidth: Int,
    var webformatHeight: Int,
    var largeImageURL: String,
    var imageWidth: Int,
    var imageHeight: Int,
    var imageSize: Int,
    var views: Int,
    var downloads: Int,
    var favorites: Int,
    var likes: Int,
    var comments: Int,
    @ColumnInfo(name = "user_id")
    var userId: Int,
    var user: String,
    var userImageURL: String,
    var updatedAt: Date
)