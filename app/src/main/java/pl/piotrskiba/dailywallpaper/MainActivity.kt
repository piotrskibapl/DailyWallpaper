package pl.piotrskiba.dailywallpaper

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.navigation.NavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import pl.piotrskiba.dailywallpaper.adapters.ImageListAdapter
import pl.piotrskiba.dailywallpaper.interfaces.ImageClickListener
import pl.piotrskiba.dailywallpaper.models.Image
import pl.piotrskiba.dailywallpaper.models.ImageList
import timber.log.Timber
import timber.log.Timber.DebugTree

class MainActivity : AppCompatActivity(), ImageClickListener {
    @BindView(R.id.toolbar)
    lateinit var mToolbar: Toolbar

    @BindView(R.id.drawer_layout)
    lateinit var mDrawerLayout: DrawerLayout

    @BindView(R.id.nav_view)
    lateinit var mNavigationView: NavigationView

    @BindView(R.id.pb_loading_indicator)
    lateinit var mLoadingIndicator: ProgressBar

    @BindView(R.id.tv_no_internet)
    lateinit var mNoInternetTextView: TextView

    @BindView(R.id.rv_images)
    lateinit var mRecyclerView: RecyclerView

    private var mImageListAdapter: ImageListAdapter? = null
    private var layoutManager: GridLayoutManager? = null
    private lateinit var mImages: ImageList
    private var mSelectedCategory: String? = null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // setup Timber
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        // setup ButterKnife
        ButterKnife.bind(this)
        // setup RecyclerView
        mImageListAdapter = ImageListAdapter(this, this)
        mRecyclerView.adapter = mImageListAdapter
        mRecyclerView.setHasFixedSize(true)
        layoutManager = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) GridLayoutManager(this, 2) else GridLayoutManager(this, 4)
        mRecyclerView.layoutManager = layoutManager
        // setup Toolbar
        setSupportActionBar(mToolbar)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu)
        // setup Navigation Drawer
        mNavigationView.setNavigationItemSelectedListener { item ->
            mDrawerLayout.closeDrawers()
            if (!item.isChecked) {
                removeOldObservers(mSelectedCategory)

                when (item.itemId) {
                    R.id.item_settings -> {
                        val intent = Intent(applicationContext, SettingsActivity::class.java)
                        startActivity(intent)
                    }
                    R.id.item_category_favorite -> mSelectedCategory = getString(R.string.key_category_favorite)
                    R.id.item_category_all -> mSelectedCategory = null
                    R.id.item_category_fashion -> mSelectedCategory = getString(R.string.key_category_fashion)
                    R.id.item_category_nature -> mSelectedCategory = getString(R.string.key_category_nature)
                    R.id.item_category_backgrounds -> mSelectedCategory = getString(R.string.key_category_backgrounds)
                    R.id.item_category_science -> mSelectedCategory = getString(R.string.key_category_science)
                    R.id.item_category_education -> mSelectedCategory = getString(R.string.key_category_education)
                    R.id.item_category_people -> mSelectedCategory = getString(R.string.key_category_people)
                    R.id.item_category_feelings -> mSelectedCategory = getString(R.string.key_category_feelings)
                    R.id.item_category_religion -> mSelectedCategory = getString(R.string.key_category_religion)
                    R.id.item_category_health -> mSelectedCategory = getString(R.string.key_category_health)
                    R.id.item_category_places -> mSelectedCategory = getString(R.string.key_category_places)
                    R.id.item_category_animals -> mSelectedCategory = getString(R.string.key_category_animals)
                    R.id.item_category_industry -> mSelectedCategory = getString(R.string.key_category_industry)
                    R.id.item_category_food -> mSelectedCategory = getString(R.string.key_category_food)
                    R.id.item_category_computer -> mSelectedCategory = getString(R.string.key_category_computer)
                    R.id.item_category_sports -> mSelectedCategory = getString(R.string.key_category_sports)
                    R.id.item_category_transportation -> mSelectedCategory = getString(R.string.key_category_transportation)
                    R.id.item_category_travel -> mSelectedCategory = getString(R.string.key_category_travel)
                    R.id.item_category_buildings -> mSelectedCategory = getString(R.string.key_category_buildings)
                    R.id.item_category_business -> mSelectedCategory = getString(R.string.key_category_business)
                    R.id.item_category_music -> mSelectedCategory = getString(R.string.key_category_music)
                }
                if (item.itemId != R.id.item_settings)
                    seekForImages()
                // log event
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, mSelectedCategory)
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "menu item")
                mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
            }
            true
        }

        if (savedInstanceState != null) {
            mSelectedCategory = savedInstanceState.getString(Intent.EXTRA_TEXT)
        }

        seekForImages()
    }

    fun removeOldObservers(category: String?) {
        val viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        when(category){
            null ->
                viewModel.getAllImages().removeObservers(this)
            getString(R.string.key_category_favorite) ->
                viewModel.favoriteImages.removeObservers(this)
            else -> {
                val allCategories = resources.getStringArray(R.array.pref_category_values)
                val categoryIndex = allCategories.indexOf(category)
                viewModel.getCategoryImages(categoryIndex).removeObservers(this)
            }
        }
    }

    private fun seekForImages() {
        showLoadingIndicator()
        val viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        if (mSelectedCategory == getString(R.string.key_category_favorite)) {
            Timber.d("Seeking for favorite images")
            viewModel.favoriteImages.observe(this, Observer { imageEntries ->
                Timber.d("Received favorite images update")
                val images = arrayOfNulls<Image>(imageEntries!!.size)
                for (i in images.indices) {
                    val (_, imageId, pageURL, type, tags, previewURL, previewWidth, previewHeight, webformatURL, webformatWidth, webformatHeight, largeImageURL, imageWidth, imageHeight, imageSize, views, downloads, favorites, likes, comments, userId, user, userImageURL) = imageEntries[i]
                    images[i] = Image(imageId, pageURL, type,
                            tags, previewURL, previewWidth,
                            previewHeight, webformatURL, webformatWidth,
                            webformatHeight, largeImageURL, imageWidth,
                            imageHeight, imageSize, views,
                            downloads, favorites, likes,
                            comments, userId, user, userImageURL)
                }

                val imageList = ImageList(images.size, images.size, images)
                populateImageList(imageList)
            })
        } else {
            mSelectedCategory?.let {
                Timber.d("Seeking for category images ($mSelectedCategory)")
                val allCategories = resources.getStringArray(R.array.pref_category_values)
                val categoryIndex = allCategories.indexOf(mSelectedCategory)

                viewModel.getCategoryImages(categoryIndex).observe(this, Observer { imageList ->
                    Timber.d("Received category images update")
                    // check if the category is still the same
                    if (mSelectedCategory.equals(allCategories[categoryIndex]) && imageList != null)
                        populateImageList(imageList)
                })
            } ?: kotlin.run {
                Timber.d("Seeking for all images")
                viewModel.getAllImages().observe(this, Observer { imageList ->
                    Timber.d("Received all images update")
                    if(mSelectedCategory == null && imageList != null)
                        populateImageList(imageList)
                })
            }
        }
    }

    fun populateImageList(imageList: ImageList) {
        mImages = imageList
        val favorite = mSelectedCategory != null && mSelectedCategory == getString(R.string.key_category_favorite)
        Timber.d("Populating %d images (favorite: %b)", mImages.hits.size, favorite)
        mImageListAdapter!!.setData(mImages, favorite)
        layoutManager!!.scrollToPosition(0)
        showDefaultLayout()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                // log event
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "android.R.id.home")
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Home")
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "menu item")
                mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onImageClick(clickedImage: Image, view: View) {
        val clickedImageView = view.findViewById<ImageView>(R.id.iv_thumbnail)
        if (clickedImageView.drawable != null) {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(KEY_IMAGE, clickedImage)
            // scale an image and pass it to DetailActivity for a better animation look
            val originalBitmap: Bitmap
            originalBitmap = if (clickedImageView.drawable is BitmapDrawable) {
                (clickedImageView.drawable as BitmapDrawable).bitmap
            } else {
                ((clickedImageView.drawable as TransitionDrawable).getDrawable(1) as BitmapDrawable).bitmap
            }
            val scaledBitmap = Bitmap.createScaledBitmap(
                    originalBitmap,
                    originalBitmap.width / 2,
                    originalBitmap.height / 2,
                    false)
            intent.putExtra(KEY_IMAGE_BITMAP, scaledBitmap)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, clickedImageView, getString(R.string.image_transition_name))
                startActivity(intent, options.toBundle())
            } else {
                startActivity(intent)
            }
            // log event
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Wallpaper image")
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
            mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
        }
    }

    private fun showDefaultLayout() {
        mRecyclerView.visibility = View.VISIBLE
        mLoadingIndicator.visibility = View.INVISIBLE
        mNoInternetTextView.visibility = View.INVISIBLE
    }

    private fun showLoadingIndicator() {
        mRecyclerView.visibility = View.INVISIBLE
        mLoadingIndicator.visibility = View.VISIBLE
        mNoInternetTextView.visibility = View.INVISIBLE
    }

    private fun showNoInternetLayout() {
        mRecyclerView.visibility = View.INVISIBLE
        mLoadingIndicator.visibility = View.INVISIBLE
        mNoInternetTextView.visibility = View.VISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(Intent.EXTRA_TEXT, mSelectedCategory)
        super.onSaveInstanceState(outState)
    }

    companion object {
        const val KEY_IMAGE = "image"
        const val KEY_IMAGE_BITMAP = "image_bitmap"
    }
}