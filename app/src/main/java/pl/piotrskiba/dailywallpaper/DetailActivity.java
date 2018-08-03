package pl.piotrskiba.dailywallpaper;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.piotrskiba.dailywallpaper.models.Image;
import timber.log.Timber;

public class DetailActivity extends AppCompatActivity {

    Image mImage;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.iv_wallpaper)
    ImageView mImageView;

    private boolean hiddenBars = false;

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
    }

    private void populateUi(){
        Timber.d("Loading large image: %s", mImage.getLargeImageURL());
        Picasso.get()
                .load(mImage.getLargeImageURL())
                .into(mImageView);
    }

    private void hideBars(){
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }

        mToolbar.animate().translationY(-mToolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();

        hiddenBars = true;
    }

    private void showBars(){
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
            decorView.setSystemUiVisibility(uiOptions);
        }

        mToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();

        hiddenBars = false;
    }

    public void onImageClick(View view){
        if(hiddenBars)
            showBars();
        else
            hideBars();
    }
}
