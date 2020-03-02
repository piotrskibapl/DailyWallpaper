package pl.piotrskiba.dailywallpaper

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import pl.piotrskiba.dailywallpaper.interfaces.ImageListLoadedListener
import pl.piotrskiba.dailywallpaper.models.ImageList
import pl.piotrskiba.dailywallpaper.utils.NetworkUtils

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext

    private var allImages: MutableLiveData<ImageList>? = null
    private var categoryImages: ArrayList<MutableLiveData<ImageList>?> = ArrayList()

    init{
        val allCategories = context.resources.getStringArray(R.array.pref_category_values)

        repeat(allCategories.size) {
            categoryImages.add(null)
        }
    }

    fun getAllImages(): LiveData<ImageList> {
        allImages ?: run {
            allImages = MutableLiveData()
            loadAllImages()
        }

        return allImages!!
    }

    fun getCategoryImages(category: Int): LiveData<ImageList> {

        categoryImages[category] ?: run {
            categoryImages[category] = MutableLiveData()
            loadCategoryImages(category)
        }

        return categoryImages[category]!!
    }

    private fun loadAllImages() {
        NetworkUtils.getAllImages(context, object : ImageListLoadedListener{
            override fun onImageListLoaded(result: ImageList) {
                allImages?.value = result
            }

            override fun onImageListLoadingError() {
                allImages?.value = null
            }

        })
    }

    private fun loadCategoryImages(category: Int) {
        NetworkUtils.getCategoryImages(context, category, object : ImageListLoadedListener{
            override fun onImageListLoaded(result: ImageList) {
                categoryImages[category]?.value = result
            }

            override fun onImageListLoadingError() {
                categoryImages[category]?.value = null
            }

        })
    }
}