package pl.piotrskiba.dailywallpaper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.Build;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.piotrskiba.dailywallpaper.asynctasks.DeleteImageAsyncTask;
import pl.piotrskiba.dailywallpaper.asynctasks.DownloadImagesAsyncTask;
import pl.piotrskiba.dailywallpaper.asynctasks.LoadImageEntryAsyncTask;
import pl.piotrskiba.dailywallpaper.asynctasks.SaveImageAsyncTask;
import pl.piotrskiba.dailywallpaper.asynctasks.SetWallpaperAsyncTask;
import pl.piotrskiba.dailywallpaper.database.AppDatabase;
import pl.piotrskiba.dailywallpaper.database.ImageEntry;
import pl.piotrskiba.dailywallpaper.interfaces.ImageDeletedListener;
import pl.piotrskiba.dailywallpaper.interfaces.ImageEntryLoadedListener;
import pl.piotrskiba.dailywallpaper.interfaces.ImageSavedListener;
import pl.piotrskiba.dailywallpaper.interfaces.ImagesDownloadedListener;
import pl.piotrskiba.dailywallpaper.interfaces.WallpaperSetListener;
import pl.piotrskiba.dailywallpaper.models.Image;
import pl.piotrskiba.dailywallpaper.utils.BitmapUtils;
import timber.log.Timber;

public class DetailActivity extends AppCompatActivity implements WallpaperSetListener, ImagesDownloadedListener, ImageSavedListener, ImageEntryLoadedListener , ImageDeletedListener{

    Image mImage;

    @BindView(R.id.main_detail_view)
    CoordinatorLayout mMainView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.toolbar_container)
    FrameLayout mToolbarContainer;

    @BindView(R.id.iv_wallpaper)
    ImageView mImageView;

    @BindView(R.id.info_section)
    RelativeLayout mInfoSection;

    @BindView(R.id.info_section_author)
    TextView mAuthorTextView;

    @BindView(R.id.info_section_downloads)
    TextView mDownloadsTextView;

    @BindView(R.id.info_section_views)
    TextView mViewsTextView;

    private boolean hiddenBars = false;

    private Snackbar mSnackBar;

    private AppDatabase mDb;

    private ImageEntry mImageEntry;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        ButterKnife.bind(this);

        mDb = AppDatabase.getInstance(this);

        Intent parentIntent = getIntent();
        if(parentIntent.hasExtra(MainActivity.KEY_IMAGE)){
            mImage = (Image) parentIntent.getSerializableExtra(MainActivity.KEY_IMAGE);

            populateUi();
        }

        // load image entry from db
        new LoadImageEntryAsyncTask(mDb, this).execute(mImage.getId());

        // setup Toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(null);

        // get status bar height
        // source: https://gist.github.com/hamakn/8939eb68a920a6d7a498
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        // set toolbar padding to be under status bar
        mToolbarContainer.setPadding(0, statusBarHeight, 0, 0);
    }

    private void populateUi(){
        Intent parentIntent = getIntent();
        if(parentIntent.hasExtra(MainActivity.KEY_IMAGE_BITMAP)) {
            Bitmap smallBitmap = parentIntent.getParcelableExtra(MainActivity.KEY_IMAGE_BITMAP);
            mImageView.setImageBitmap(smallBitmap);
        }

        mAuthorTextView.setText(getString(R.string.info_author, mImage.getUser()));
        mDownloadsTextView.setText(getString(R.string.info_downloads, mImage.getDownloads()));
        mViewsTextView.setText(getString(R.string.info_views, mImage.getViews()));
    }

    private void hideUiElements(){
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }

        mToolbarContainer.animate().translationY(-mToolbarContainer.getBottom()).setInterpolator(new AccelerateInterpolator()).start();

        mInfoSection.animate().translationY(mInfoSection.getHeight()).setInterpolator(new AccelerateInterpolator()).start();

        hiddenBars = true;
    }

    private void showUiElements(){
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
            decorView.setSystemUiVisibility(uiOptions);
        }

        mToolbarContainer.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();

        mInfoSection.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();

        hiddenBars = false;
    }

    public void onImageClick(View view){
        if(hiddenBars)
            showUiElements();
        else
            hideUiElements();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if(mImageEntry == null && !settingAsFavorite){
            menu.findItem(R.id.action_favorite).setVisible(true);
            menu.findItem(R.id.action_unfavorite).setVisible(false);
        }
        else{
            menu.findItem(R.id.action_favorite).setVisible(false);
            menu.findItem(R.id.action_unfavorite).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);

        inflater.inflate(R.menu.menu_detail, menu);

        return true;
    }


    boolean settingAsFavorite = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_set_as_wallpaper){
            if (mImageView.getDrawable() != null) {
                Bitmap originalBitmap = ((BitmapDrawable)mImageView.getDrawable()).getBitmap();
                // get screen dimensions
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);

                // center crop the image
                Bitmap bitmap = ThumbnailUtils.extractThumbnail(originalBitmap, size.x, size.y);

                // set image as wallpaper
                new SetWallpaperAsyncTask(this, this).execute(bitmap);

                if (mSnackBar != null)
                    mSnackBar.dismiss();
                mSnackBar = Snackbar.make(mMainView, R.string.setting_wallpaper, Snackbar.LENGTH_LONG);
                mSnackBar.show();

                // log event
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "android.R.id.action_set_as_wallpaper");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Set wallpaper");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "menu item");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }
            else{
                Timber.e("originalBitmap was null while attempting to set a wallpaper");
            }

            return true;
        }
        else if(item.getItemId() == R.id.action_favorite){
            settingAsFavorite = true;
            invalidateOptionsMenu();

            String previewUrl = mImage.getId() + BitmapUtils.SUFFIX_PREVIEW + BitmapUtils.IMAGE_EXTENSION;
            if(BitmapUtils.loadBitmap(this, previewUrl) != null) {
                Timber.d("exists");
                onImagesDownloaded();
            }
            else{
                Timber.d("doesn't exist");
                new DownloadImagesAsyncTask(this, this).execute(
                        String.valueOf(mImage.getId()),
                        mImage.getPreviewURL(),
                        mImage.getWebformatURL(),
                        mImage.getLargeImageURL());
            }

            // log event
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "android.R.id.action_favorite");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Mark as favorite");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "menu item");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        }
        else if(item.getItemId() == R.id.action_unfavorite){
            new DeleteImageAsyncTask(mDb, this).execute(mImageEntry);

            // log event
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "android.R.id.action_unfavorite");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Unmark as favorite");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "menu item");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        }
        else if(item.getItemId() == android.R.id.home){
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWallpaperSet(Boolean success) {
        if(mSnackBar != null)
            mSnackBar.dismiss();

        if(success){
            mSnackBar = Snackbar.make(mMainView, R.string.wallpaper_set, Snackbar.LENGTH_SHORT);
        }
        else{
            mSnackBar = Snackbar.make(mMainView, R.string.error_setting_wallpaper, Snackbar.LENGTH_SHORT);
        }

        mSnackBar.show();
    }

    @Override
    public void onImagesDownloaded() {
        Date date = new Date();

        String previewUrl = mImage.getId() + BitmapUtils.SUFFIX_PREVIEW + BitmapUtils.IMAGE_EXTENSION;
        String webformatUrl = mImage.getId() + BitmapUtils.SUFFIX_WEBFORMAT + BitmapUtils.IMAGE_EXTENSION;
        String largeImageUrl = mImage.getId() + BitmapUtils.SUFFIX_LARGEIMAGE + BitmapUtils.IMAGE_EXTENSION;

        ImageEntry imageEntry = new ImageEntry(mImage.getId(), mImage.getPageURL(), mImage.getType(),
                mImage.getTags(), previewUrl, mImage.getPreviewWidth(), mImage.getPreviewHeight(),
                webformatUrl, mImage.getWebformatWidth(), mImage.getWebformatHeight(),
                largeImageUrl, mImage.getImageWidth(), mImage.getImageHeight(),
                mImage.getImageSize(), mImage.getViews(), mImage.getDownloads(), mImage.getFavorites(),
                mImage.getLikes(), mImage.getComments(), mImage.getUser_id(), mImage.getUser(),
                mImage.getUserImageURL(), date);

        new SaveImageAsyncTask(mDb, this).execute(imageEntry);
    }

    @Override
    public void onImageSaved() {
        settingAsFavorite = false;
        new LoadImageEntryAsyncTask(mDb, this).execute(mImage.getId());
    }

    @Override
    public void onImageDeleted() {
        mImageEntry = null;
        invalidateOptionsMenu();
    }

    @Override
    public void onImageEntryLoaded(ImageEntry imageEntry) {
        mImageEntry = imageEntry;

        if(mImageEntry == null){
            Timber.d("image is not favorite");
            Timber.d("Loading large image: %s", mImage.getLargeImageURL());

            RequestOptions requestOptions = new RequestOptions();

            Intent parentIntent = getIntent();
            if(parentIntent.hasExtra(MainActivity.KEY_IMAGE_BITMAP)) {
                Bitmap smallBitmap = parentIntent.getParcelableExtra(MainActivity.KEY_IMAGE_BITMAP);
                requestOptions = new RequestOptions().placeholder(new BitmapDrawable(smallBitmap)).dontTransform();
            }

            Glide.with(this)
                    .setDefaultRequestOptions(requestOptions)
                    .load(mImage.getLargeImageURL())
                    .into(mImageView);
        }
        else{
            Timber.d("image is favorite");
            Timber.d("Loading large image: %s", mImageEntry.getLargeImageURL());
            mImageView.setImageBitmap(BitmapUtils.loadBitmap(this, mImageEntry.getLargeImageURL()));
        }

        invalidateOptionsMenu();
    }

    @Override
    protected void onDestroy() {

        // delete cached images if the image was unfavorited
        if(mImageEntry == null && !settingAsFavorite){
            String previewUrl = mImage.getId() + BitmapUtils.SUFFIX_PREVIEW + BitmapUtils.IMAGE_EXTENSION;
            String webformatUrl = mImage.getId() + BitmapUtils.SUFFIX_WEBFORMAT + BitmapUtils.IMAGE_EXTENSION;
            String largeImageUrl = mImage.getId() + BitmapUtils.SUFFIX_LARGEIMAGE + BitmapUtils.IMAGE_EXTENSION;

            if(BitmapUtils.loadBitmap(this, previewUrl) != null){
                Timber.d("deleting");
                deleteFile(previewUrl);
                deleteFile(webformatUrl);
                deleteFile(largeImageUrl);
            }
        }

        super.onDestroy();
    }
}
