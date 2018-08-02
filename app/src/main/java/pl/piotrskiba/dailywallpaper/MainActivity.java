package pl.piotrskiba.dailywallpaper;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.piotrskiba.dailywallpaper.asynctasks.FetchImagesAsyncTask;
import pl.piotrskiba.dailywallpaper.interfaces.AsyncTaskCompleteListener;
import pl.piotrskiba.dailywallpaper.models.ImageList;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements AsyncTaskCompleteListener<ImageList> {

    @BindView(R.id.rv_images)
    RecyclerView mRecyclerView;
    private ImageListAdapter mImageListAdapter;
    private GridLayoutManager layoutManager;

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
        mImageListAdapter = new ImageListAdapter();
        mRecyclerView.setAdapter(mImageListAdapter);
        mRecyclerView.setHasFixedSize(true);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            layoutManager = new GridLayoutManager(this, 2);
        else
            layoutManager = new GridLayoutManager(this, 4);

        mRecyclerView.setLayoutManager(layoutManager);

        new FetchImagesAsyncTask(this, this).execute();
    }

    @Override
    public void onImageListRetrieved(ImageList result) {
        Timber.d("Loaded %d images", result.getHits().length);
        mImageListAdapter.setData(result);
    }
}
