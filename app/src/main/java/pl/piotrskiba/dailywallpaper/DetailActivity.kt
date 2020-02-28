package pl.piotrskiba.dailywallpaper

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.media.ThumbnailUtils
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import pl.piotrskiba.dailywallpaper.asynctasks.DownloadImagesAsyncTask
import pl.piotrskiba.dailywallpaper.asynctasks.SetWallpaperAsyncTask
import pl.piotrskiba.dailywallpaper.database.AppDatabase
import pl.piotrskiba.dailywallpaper.database.AppDatabase.Companion.getInstance
import pl.piotrskiba.dailywallpaper.database.ImageEntry
import pl.piotrskiba.dailywallpaper.interfaces.ImagesDownloadedListener
import pl.piotrskiba.dailywallpaper.interfaces.WallpaperSetListener
import pl.piotrskiba.dailywallpaper.models.Image
import pl.piotrskiba.dailywallpaper.utils.BitmapUtils
import pl.piotrskiba.dailywallpaper.utils.BitmapUtils.loadBitmap
import timber.log.Timber
import java.util.*

class DetailActivity : AppCompatActivity(), WallpaperSetListener, ImagesDownloadedListener {
    lateinit var mImage: Image
    @JvmField
    @BindView(R.id.main_detail_view)
    var mMainView: CoordinatorLayout? = null
    @JvmField
    @BindView(R.id.toolbar)
    var mToolbar: Toolbar? = null
    @JvmField
    @BindView(R.id.toolbar_container)
    var mToolbarContainer: FrameLayout? = null
    @JvmField
    @BindView(R.id.iv_wallpaper)
    var mImageView: ImageView? = null
    @JvmField
    @BindView(R.id.info_section)
    var mInfoSection: RelativeLayout? = null
    @JvmField
    @BindView(R.id.info_section_author)
    var mAuthorTextView: TextView? = null
    @JvmField
    @BindView(R.id.info_section_downloads)
    var mDownloadsTextView: TextView? = null
    @JvmField
    @BindView(R.id.info_section_views)
    var mViewsTextView: TextView? = null
    private var hiddenBars = false
    private var mSnackBar: Snackbar? = null
    private lateinit var mDb: AppDatabase
    private var mImageEntry: ImageEntry? = null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        ButterKnife.bind(this)
        mDb = getInstance(this)
        val parentIntent = intent
        if (parentIntent.hasExtra(MainActivity.KEY_IMAGE)) {
            mImage = parentIntent.getSerializableExtra(MainActivity.KEY_IMAGE) as Image
            populateUi()
        }
        // load image entry from db
        seekForDatabaseImage()
        // setup Toolbar
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = null
        // get status bar height
// source: https://gist.github.com/hamakn/8939eb68a920a6d7a498
        var statusBarHeight = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId)
        }
        // set toolbar padding to be under status bar
        mToolbarContainer!!.setPadding(0, statusBarHeight, 0, 0)
    }

    private fun populateUi() {
        val parentIntent = intent
        if (parentIntent.hasExtra(MainActivity.KEY_IMAGE_BITMAP)) {
            val smallBitmap = parentIntent.getParcelableExtra<Bitmap>(MainActivity.KEY_IMAGE_BITMAP)
            mImageView!!.setImageBitmap(smallBitmap)
        }
        mAuthorTextView!!.text = getString(R.string.info_author, mImage.user)
        mDownloadsTextView!!.text = getString(R.string.info_downloads, mImage.downloads)
        mViewsTextView!!.text = getString(R.string.info_views, mImage.views)
    }

    private fun hideUiElements() {
        val decorView = window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
        decorView.systemUiVisibility = uiOptions
        mToolbarContainer!!.animate().translationY(-mToolbarContainer!!.bottom.toFloat()).setInterpolator(AccelerateInterpolator()).start()
        mInfoSection!!.animate().translationY(mInfoSection!!.height.toFloat()).setInterpolator(AccelerateInterpolator()).start()
        hiddenBars = true
    }

    private fun showUiElements() {
        val decorView = window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_VISIBLE
        decorView.systemUiVisibility = uiOptions
        mToolbarContainer!!.animate().translationY(0f).setInterpolator(DecelerateInterpolator()).start()
        mInfoSection!!.animate().translationY(0f).setInterpolator(DecelerateInterpolator()).start()
        hiddenBars = false
    }

    fun onImageClick(view: View?) {
        if (hiddenBars) showUiElements() else hideUiElements()
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        if (mImageEntry == null && !settingAsFavorite) {
            menu.findItem(R.id.action_favorite).isVisible = true
            menu.findItem(R.id.action_unfavorite).isVisible = false
        } else {
            menu.findItem(R.id.action_favorite).isVisible = false
            menu.findItem(R.id.action_unfavorite).isVisible = true
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = MenuInflater(this)
        inflater.inflate(R.menu.menu_detail, menu)
        return true
    }

    var settingAsFavorite = false
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_set_as_wallpaper) {
            if (mImageView!!.drawable != null) {
                val originalBitmap = (mImageView!!.drawable as BitmapDrawable).bitmap
                // get screen dimensions
                val display = windowManager.defaultDisplay
                val size = Point()
                display.getSize(size)
                // center crop the image
                val bitmap = ThumbnailUtils.extractThumbnail(originalBitmap, size.x, size.y)
                // set image as wallpaper
                SetWallpaperAsyncTask(this, this).execute(bitmap)
                if (mSnackBar != null) mSnackBar!!.dismiss()
                mSnackBar = Snackbar.make(mMainView!!, R.string.setting_wallpaper, Snackbar.LENGTH_LONG)
                mSnackBar!!.show()
                // log event
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "android.R.id.action_set_as_wallpaper")
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Set wallpaper")
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "menu item")
                mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
            } else {
                Timber.e("originalBitmap was null while attempting to set a wallpaper")
            }
            return true
        } else if (item.itemId == R.id.action_favorite) {
            settingAsFavorite = true
            invalidateOptionsMenu()
            val previewUrl: String = mImage.id.toString() + BitmapUtils.SUFFIX_PREVIEW + BitmapUtils.IMAGE_EXTENSION
            if (loadBitmap(this, previewUrl) != null) {
                Timber.d("exists")
                onImagesDownloaded()
            } else {
                Timber.d("doesn't exist")
                DownloadImagesAsyncTask(this, this).execute(mImage.id.toString(),
                        mImage.previewURL,
                        mImage.webformatURL,
                        mImage.largeImageURL)
            }
            // log event
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "android.R.id.action_favorite")
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Mark as favorite")
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "menu item")
            mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
        } else if (item.itemId == R.id.action_unfavorite) {
            if(mImageEntry != null) {
                mDb.imageDao().deleteImage(mImageEntry!!).subscribeOn(Schedulers.io()).subscribe(object: CompletableObserver{
                    override fun onComplete() {
                        Timber.d("Image deleted")
                        mImageEntry = null
                        invalidateOptionsMenu()
                    }

                    override fun onSubscribe(d: Disposable?) {
                    }

                    override fun onError(e: Throwable?) {
                        Timber.d("Could not delete the image")
                        e?.printStackTrace()
                    }

                })
            }
            // log event
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "android.R.id.action_unfavorite")
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Unmark as favorite")
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "menu item")
            mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
        } else if (item.itemId == android.R.id.home) {
            super.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onWallpaperSet(success: Boolean) {
        if (mSnackBar != null) mSnackBar!!.dismiss()
        mSnackBar = if (success) {
            Snackbar.make(mMainView!!, R.string.wallpaper_set, Snackbar.LENGTH_SHORT)
        } else {
            Snackbar.make(mMainView!!, R.string.error_setting_wallpaper, Snackbar.LENGTH_SHORT)
        }
        mSnackBar!!.show()
    }

    override fun onImagesDownloaded() {
        Timber.d("Images downloaded")
        val date = Date()
        val previewUrl: String = mImage.id.toString() + BitmapUtils.SUFFIX_PREVIEW + BitmapUtils.IMAGE_EXTENSION
        val webformatUrl: String = mImage.id.toString() + BitmapUtils.SUFFIX_WEBFORMAT + BitmapUtils.IMAGE_EXTENSION
        val largeImageUrl: String = mImage.id.toString() + BitmapUtils.SUFFIX_LARGEIMAGE + BitmapUtils.IMAGE_EXTENSION
        val imageEntry = ImageEntry(mImage.id, -1, mImage.pageURL, mImage.type,
                mImage.tags, previewUrl, mImage.previewWidth, mImage.previewHeight,
                webformatUrl, mImage.webformatWidth, mImage.webformatHeight,
                largeImageUrl, mImage.imageWidth, mImage.imageHeight,
                mImage.imageSize, mImage.views, mImage.downloads, mImage.favorites,
                mImage.likes, mImage.comments, mImage.user_id, mImage.user,
                mImage.userImageURL, date)

        val imageEntryLoadedListener = this;

        mDb.imageDao().insertImage(imageEntry).subscribeOn(Schedulers.io()).subscribe(object: CompletableObserver{
            override fun onComplete() {
                Timber.d("Image saved")
                settingAsFavorite = false
            }

            override fun onSubscribe(d: Disposable?) {
            }

            override fun onError(e: Throwable?) {
                Timber.d("Could not save the image")
                e?.printStackTrace()
            }

        })
    }

    private fun seekForDatabaseImage(){
        val context = this

        mDb.imageDao().loadImagesByImageId(mImage.id).observe(this, androidx.lifecycle.Observer {
            mImageEntry = if(it.isNotEmpty())
                it[0]
            else
                null

            mImageEntry?.run {
                Timber.d("image is favorite")
                Timber.d("Loading large image: %s", mImageEntry!!.largeImageURL)
                mImageView!!.setImageBitmap(loadBitmap(context, mImageEntry!!.largeImageURL))
            } ?: run {
                Timber.d("image is not favorite")
                Timber.d("Loading large image: %s", mImage.largeImageURL)
                var requestOptions = RequestOptions()
                val parentIntent = intent
                if (parentIntent.hasExtra(MainActivity.KEY_IMAGE_BITMAP)) {
                    val smallBitmap = parentIntent.getParcelableExtra<Bitmap>(MainActivity.KEY_IMAGE_BITMAP)
                    requestOptions = RequestOptions().placeholder(BitmapDrawable(smallBitmap)).dontTransform()
                }
                Glide.with(this)
                        .setDefaultRequestOptions(requestOptions)
                        .load(mImage.largeImageURL)
                        .into(mImageView!!)
            }

            invalidateOptionsMenu()
        })
    }

    override fun onDestroy() { // delete cached images if the image was unfavorited
        if (mImageEntry == null && !settingAsFavorite) {
            val previewUrl: String = mImage.id.toString() + BitmapUtils.SUFFIX_PREVIEW + BitmapUtils.IMAGE_EXTENSION
            val webformatUrl: String = mImage.id.toString() + BitmapUtils.SUFFIX_WEBFORMAT + BitmapUtils.IMAGE_EXTENSION
            val largeImageUrl: String = mImage.id.toString() + BitmapUtils.SUFFIX_LARGEIMAGE + BitmapUtils.IMAGE_EXTENSION
            if (loadBitmap(this, previewUrl) != null) {
                Timber.d("deleting")
                deleteFile(previewUrl)
                deleteFile(webformatUrl)
                deleteFile(largeImageUrl)
            }
        }
        super.onDestroy()
    }
}