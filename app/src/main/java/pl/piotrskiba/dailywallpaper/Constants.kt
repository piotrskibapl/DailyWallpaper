package pl.piotrskiba.dailywallpaper

object Constants {

    const val BASE_API_URL = "https://pixabay.com/api/"
    const val PARAM_API_KEY = "key"
    const val PARAM_ORIENTATION = "orientation"
    const val PARAM_EDITORS_CHOICE = "editors_choice"
    const val PARAM_CATEGORY = "category"
    const val PARAM_SAFE_SEARCH = "safesearch"

    const val DEFAULT_VALUE_API_KEY = BuildConfig.API_KEY
    const val DEFAULT_VALUE_ORIENTATION = "vertical"
    const val DEFAULT_VALUE_EDITORS_CHOICE = true
}