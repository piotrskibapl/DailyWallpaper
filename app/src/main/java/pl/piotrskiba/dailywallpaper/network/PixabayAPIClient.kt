package pl.piotrskiba.dailywallpaper.network

import com.google.gson.GsonBuilder
import pl.piotrskiba.dailywallpaper.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PixabayAPIClient {

    companion object {
        val retrofit: Retrofit by lazy {
            val gson = GsonBuilder()
                    .setLenient()
                    .create()

            Retrofit.Builder()
                    .baseUrl(Constants.BASE_API_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
        }
    }
}