package pl.piotrskiba.dailywallpaper.models

import java.io.Serializable

class Image(val id: Int, val pageURL: String, val type: String, val tags: String, val previewURL: String,
            val previewWidth: Int, val previewHeight: Int, val webformatURL: String, val webformatWidth: Int,
            val webformatHeight: Int, val largeImageURL: String, val imageWidth: Int, val imageHeight: Int, val imageSize: Int,
            val views: Int, val downloads: Int, val favorites: Int, val likes: Int, val comments: Int, val user_id: Int, val user: String, val userImageURL: String) : Serializable