package pl.piotrskiba.dailywallpaper;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.piotrskiba.dailywallpaper.asynctasks.SetWallpaperAsyncTask;
import pl.piotrskiba.dailywallpaper.interfaces.AsyncTaskCompleteListener;
import pl.piotrskiba.dailywallpaper.models.Image;
import timber.log.Timber;

public class DetailActivity extends AppCompatActivity implements AsyncTaskCompleteListener<Boolean> {

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

    Snackbar mSnackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        Intent parentIntent = getIntent();
        if(parentIntent.hasExtra(MainActivity.KEY_IMAGE)){
            mImage = (Image) parentIntent.getSerializableExtra(MainActivity.KEY_IMAGE);

            populateUi();
        }

        // setup Toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
        Timber.d("Loading large image: %s", mImage.getLargeImageURL());
        Picasso.get()
                .load(mImage.getLargeImageURL())
                .into(mImageView);

        mAuthorTextView.setText(getString(R.string.info_author, mImage.getUser()));
        mDownloadsTextView.setText(getString(R.string.info_downloads, mImage.getDownloads()));
        mViewsTextView.setText(getString(R.string.info_views, mImage.getViews()));
        Timber.d(getString(R.string.info_views, mImage.getViews()));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);

        inflater.inflate(R.menu.menu_detail, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_set_as_wallpaper){
            Bitmap originalBitmap = ((BitmapDrawable)mImageView.getDrawable()).getBitmap();

            // get screen dimensions
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            // center crop the image
            Bitmap bitmap = ThumbnailUtils.extractThumbnail(originalBitmap, size.x, size.y);

            // set image as wallpaper
            new SetWallpaperAsyncTask(this, this).execute(bitmap);

            if(mSnackBar != null)
                mSnackBar.dismiss();
            mSnackBar = Snackbar.make(mMainView, R.string.setting_wallpaper, Snackbar.LENGTH_LONG);
            mSnackBar.show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTaskCompleted(Boolean success) {
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
}
