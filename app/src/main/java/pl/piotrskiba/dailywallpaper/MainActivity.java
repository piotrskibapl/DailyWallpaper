package pl.piotrskiba.dailywallpaper;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.piotrskiba.dailywallpaper.adapters.ImageListAdapter;
import pl.piotrskiba.dailywallpaper.asynctasks.FetchImagesAsyncTask;
import pl.piotrskiba.dailywallpaper.database.ImageEntry;
import pl.piotrskiba.dailywallpaper.interfaces.ImageListLoadedListener;
import pl.piotrskiba.dailywallpaper.interfaces.ImageClickListener;
import pl.piotrskiba.dailywallpaper.models.Image;
import pl.piotrskiba.dailywallpaper.models.ImageList;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements ImageListLoadedListener, ImageClickListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    @BindView(R.id.tv_no_internet)
    TextView mNoInternetTextView;

    @BindView(R.id.rv_images)
    RecyclerView mRecyclerView;
    private ImageListAdapter mImageListAdapter;
    private GridLayoutManager layoutManager;

    public static final String KEY_IMAGE = "image";
    public static final String KEY_IMAGE_LIST = "image_list";

    private ImageList mImages;
    private ImageList mFavoriteImages;

    private String mSelectedCategory = null;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setup Timber
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // setup ButterKnife
        ButterKnife.bind(this);

        // setup RecyclerView
        mImageListAdapter = new ImageListAdapter(this);
        mRecyclerView.setAdapter(mImageListAdapter);
        mRecyclerView.setHasFixedSize(true);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            layoutManager = new GridLayoutManager(this, 2);
        else
            layoutManager = new GridLayoutManager(this, 4);

        mRecyclerView.setLayoutManager(layoutManager);

        // setup Toolbar
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        // setup Navigation Drawer
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        mDrawerLayout.closeDrawers();
                        if(!item.isChecked()) {
                            switch (item.getItemId()) {
                                case R.id.item_settings:
                                    Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                                    startActivity(intent);
                                    break;
                                case R.id.item_category_favorite:
                                    mSelectedCategory = getString(R.string.key_category_favorite);
                                    break;
                                case R.id.item_category_all:
                                    mSelectedCategory = null;
                                    break;
                                case R.id.item_category_fashion:
                                    mSelectedCategory = getString(R.string.key_category_fashion);
                                    break;
                                case R.id.item_category_nature:
                                    mSelectedCategory = getString(R.string.key_category_nature);
                                    break;
                                case R.id.item_category_backgrounds:
                                    mSelectedCategory = getString(R.string.key_category_backgrounds);
                                    break;
                                case R.id.item_category_science:
                                    mSelectedCategory = getString(R.string.key_category_science);
                                    break;
                                case R.id.item_category_education:
                                    mSelectedCategory = getString(R.string.key_category_education);
                                    break;
                                case R.id.item_category_people:
                                    mSelectedCategory = getString(R.string.key_category_people);
                                    break;
                                case R.id.item_category_feelings:
                                    mSelectedCategory = getString(R.string.key_category_feelings);
                                    break;
                                case R.id.item_category_religion:
                                    mSelectedCategory = getString(R.string.key_category_religion);
                                    break;
                                case R.id.item_category_health:
                                    mSelectedCategory = getString(R.string.key_category_health);
                                    break;
                                case R.id.item_category_places:
                                    mSelectedCategory = getString(R.string.key_category_places);
                                    break;
                                case R.id.item_category_animals:
                                    mSelectedCategory = getString(R.string.key_category_animals);
                                    break;
                                case R.id.item_category_industry:
                                    mSelectedCategory = getString(R.string.key_category_industry);
                                    break;
                                case R.id.item_category_food:
                                    mSelectedCategory = getString(R.string.key_category_food);
                                    break;
                                case R.id.item_category_computer:
                                    mSelectedCategory = getString(R.string.key_category_computer);
                                    break;
                                case R.id.item_category_sports:
                                    mSelectedCategory = getString(R.string.key_category_sports);
                                    break;
                                case R.id.item_category_transportation:
                                    mSelectedCategory = getString(R.string.key_category_transportation);
                                    break;
                                case R.id.item_category_travel:
                                    mSelectedCategory = getString(R.string.key_category_travel);
                                    break;
                                case R.id.item_category_buildings:
                                    mSelectedCategory = getString(R.string.key_category_buildings);
                                    break;
                                case R.id.item_category_business:
                                    mSelectedCategory = getString(R.string.key_category_business);
                                    break;
                                case R.id.item_category_music:
                                    mSelectedCategory = getString(R.string.key_category_music);
                                    break;
                            }

                            if(item.getItemId() != R.id.item_settings)
                                loadImages();

                            // log event
                            Bundle bundle = new Bundle();
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, mSelectedCategory);
                            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "menu item");
                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                        }
                        return true;
                    }
                }
        );

        setupViewModel();

        if(savedInstanceState != null) {
            mSelectedCategory = savedInstanceState.getString(Intent.EXTRA_TEXT);
            mImages = (ImageList) savedInstanceState.getSerializable(KEY_IMAGE_LIST);

            onImageListLoaded(mImages);
        }
        else{
            loadImages();
        }
    }

    private void loadImages(){
        showLoadingIndicator();

        if(mSelectedCategory != null && mSelectedCategory.equals(getString(R.string.key_category_favorite))){
            loadFavoriteImages();
        }
        else {
            new FetchImagesAsyncTask(this, this).execute(mSelectedCategory);
        }
    }

    private void loadFavoriteImages(){
        onImageListLoaded(mFavoriteImages);
    }

    private void setupViewModel(){

        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        viewModel.getFavoriteImages().observe(this, new Observer<List<ImageEntry>>() {
            @Override
            public void onChanged(@Nullable List<ImageEntry> imageEntries) {
                Image[] images = new Image[imageEntries.size()];

                for(int i = 0; i < images.length; i++){
                    ImageEntry imageEntry = imageEntries.get(i);
                    images[i] = new Image(imageEntry.getImageId(), imageEntry.getPageURL(), imageEntry.getType(),
                            imageEntry.getTags(), imageEntry.getPreviewURL(), imageEntry.getPreviewWidth(),
                            imageEntry.getPreviewHeight(), imageEntry.getWebformatURL(), imageEntry.getWebformatWidth(),
                            imageEntry.getWebformatHeight(), imageEntry.getLargeImageURL(), imageEntry.getImageWidth(),
                            imageEntry.getImageHeight(), imageEntry.getImageSize(), imageEntry.getViews(),
                            imageEntry.getDownloads(), imageEntry.getFavorites(), imageEntry.getLikes(),
                            imageEntry.getComments(), imageEntry.getUser_id(), imageEntry.getUser(), imageEntry.getUserImageURL());
                }

                mFavoriteImages = new ImageList(images.length, images.length, images);

                if(mSelectedCategory != null && mSelectedCategory.equals(getString(R.string.key_category_favorite)))
                    loadFavoriteImages();
            }
        });
    }

    @Override
    public void onImageListLoaded(ImageList result) {
        mImages = result;
        if(mImages != null) {
            Timber.d("Loaded %d images", mImages.getHits().length);
            mImageListAdapter.setData(mImages);
            layoutManager.scrollToPosition(0);
            showDefaultLayout();
        }
        else{
            Timber.w("No images loaded!");
            showNoInternetLayout();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);

                // log event
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "android.R.id.home");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Home");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "menu item");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onImageClick(Image clickedImage) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(KEY_IMAGE, clickedImage);

        startActivity(intent);

        // log event
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Wallpaper image");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    private void showDefaultLayout(){
        mRecyclerView.setVisibility(View.VISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mNoInternetTextView.setVisibility(View.INVISIBLE);
    }

    private void showLoadingIndicator(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mNoInternetTextView.setVisibility(View.INVISIBLE);
    }

    private void showNoInternetLayout(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mNoInternetTextView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(Intent.EXTRA_TEXT, mSelectedCategory);
        outState.putSerializable(KEY_IMAGE_LIST, mImages);

        super.onSaveInstanceState(outState);
    }
}
