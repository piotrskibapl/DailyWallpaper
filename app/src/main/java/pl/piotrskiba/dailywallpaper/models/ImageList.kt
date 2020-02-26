package pl.piotrskiba.dailywallpaper.models

import java.io.Serializable

class ImageList(val total: Int, val totalHits: Int, val hits: Array<Image?>) : Serializable