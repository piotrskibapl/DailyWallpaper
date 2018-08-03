package pl.piotrskiba.dailywallpaper;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.piotrskiba.dailywallpaper.adapters.ImageListAdapter;
import pl.piotrskiba.dailywallpaper.asynctasks.FetchImagesAsyncTask;
import pl.piotrskiba.dailywallpaper.interfaces.AsyncTaskCompleteListener;
import pl.piotrskiba.dailywallpaper.interfaces.ImageClickListener;
import pl.piotrskiba.dailywallpaper.models.Image;
import pl.piotrskiba.dailywallpaper.models.ImageList;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements AsyncTaskCompleteListener<ImageList>, ImageClickListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    @BindView(R.id.rv_images)
    RecyclerView mRecyclerView;
    private ImageListAdapter mImageListAdapter;
    private GridLayoutManager layoutManager;

    public static final String KEY_IMAGE = "image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setup Timber
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

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
                            item.setChecked(true);

                            String category = null;
                            switch (item.getItemId()) {
                                case R.id.item_category_all:
                                    category = null;
                                    break;
                                case R.id.item_category_fashion:
                                    category = getString(R.string.key_category_fashion);
                                    break;
                                case R.id.item_category_nature:
                                    category = getString(R.string.key_category_nature);
                                    break;
                                case R.id.item_category_backgrounds:
                                    category = getString(R.string.key_category_backgrounds);
                                    break;
                                case R.id.item_category_science:
                                    category = getString(R.string.key_category_science);
                                    break;
                                case R.id.item_category_education:
                                    category = getString(R.string.key_category_education);
                                    break;
                                case R.id.item_category_people:
                                    category = getString(R.string.key_category_people);
                                    break;
                                case R.id.item_category_feelings:
                                    category = getString(R.string.key_category_feelings);
                                    break;
                                case R.id.item_category_religion:
                                    category = getString(R.string.key_category_religion);
                                    break;
                                case R.id.item_category_health:
                                    category = getString(R.string.key_category_health);
                                    break;
                                case R.id.item_category_places:
                                    category = getString(R.string.key_category_places);
                                    break;
                                case R.id.item_category_animals:
                                    category = getString(R.string.key_category_animals);
                                    break;
                                case R.id.item_category_industry:
                                    category = getString(R.string.key_category_industry);
                                    break;
                                case R.id.item_category_food:
                                    category = getString(R.string.key_category_food);
                                    break;
                                case R.id.item_category_computer:
                                    category = getString(R.string.key_category_computer);
                                    break;
                                case R.id.item_category_sports:
                                    category = getString(R.string.key_category_sports);
                                    break;
                                case R.id.item_category_transportation:
                                    category = getString(R.string.key_category_transportation);
                                    break;
                                case R.id.item_category_travel:
                                    category = getString(R.string.key_category_travel);
                                    break;
                                case R.id.item_category_buildings:
                                    category = getString(R.string.key_category_buildings);
                                    break;
                                case R.id.item_category_business:
                                    category = getString(R.string.key_category_business);
                                    break;
                                case R.id.item_category_music:
                                    category = getString(R.string.key_category_music);
                                    break;
                            }
                            loadImages(category);
                        }
                        return true;
                    }
                }
        );

        loadImages(null);
    }

    public void loadImages(String category){
        new FetchImagesAsyncTask(this, this).execute(category);
    }

    @Override
    public void onImageListRetrieved(ImageList result) {
        if(result != null) {
            Timber.d("Loaded %d images", result.getHits().length);
            mImageListAdapter.setData(result);
            layoutManager.scrollToPosition(0);
        }
        else{
            Timber.w("No images loaded!");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onImageClick(Image clickedImage) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(KEY_IMAGE, clickedImage);

        startActivity(intent);
    }
}
