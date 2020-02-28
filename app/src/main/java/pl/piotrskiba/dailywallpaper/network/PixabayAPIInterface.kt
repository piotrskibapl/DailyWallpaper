package pl.piotrskiba.dailywallpaper.network

import pl.piotrskiba.dailywallpaper.Constants
import pl.piotrskiba.dailywallpaper.models.ImageList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayAPIInterface {

    @GET(".")
    fun loadAllImages(
            @Query(Constants.PARAM_API_KEY) apiKey: String,
            @Query(Constants.PARAM_ORIENTATION) orientation: String,
            @Query(Constants.PARAM_EDITORS_CHOICE) editorsChoice: Boolean,
            @Query(Constants.PARAM_SAFE_SEARCH) safeSearch: Boolean
    ): Call<ImageList>

    @GET(".")
    fun loadCategoryImages(
            @Query(Constants.PARAM_API_KEY) apiKey: String,
            @Query(Constants.PARAM_ORIENTATION) orientation: String,
            @Query(Constants.PARAM_EDITORS_CHOICE) editorsChoice: Boolean,
            @Query(Constants.PARAM_SAFE_SEARCH) safeSearch: Boolean,
            @Query(Constants.PARAM_CATEGORY) category: String
    ): Call<ImageList>
}