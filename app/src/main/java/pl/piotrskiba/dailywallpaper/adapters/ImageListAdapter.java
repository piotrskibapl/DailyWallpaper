package pl.piotrskiba.dailywallpaper.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.piotrskiba.dailywallpaper.R;
import pl.piotrskiba.dailywallpaper.interfaces.ImageClickListener;
import pl.piotrskiba.dailywallpaper.models.Image;
import pl.piotrskiba.dailywallpaper.models.ImageList;
import pl.piotrskiba.dailywallpaper.utils.BitmapUtils;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ImageViewHolder> {

    private ImageList mImageList;
    private Context context;
    private ImageClickListener clickListener;
    private boolean isFavorite = false;

    public ImageListAdapter(Context context, ImageClickListener clickListener){
        this.context = context;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.image_list_item, parent, false);

        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Image image = mImageList.getHits()[position];

        if(isFavorite)
            holder.mThumbnail.setImageBitmap(BitmapUtils.loadBitmap(context, image.getWebformatURL()));
        else
            Glide.with(context)
                    .load(image.getWebformatURL())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.mThumbnail);
    }

    @Override
    public int getItemCount() {
        if(mImageList == null)
            return 0;
        else
            return mImageList.getHits().length;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.iv_thumbnail)
        ImageView mThumbnail;

        public ImageViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPos = getAdapterPosition();
            Image image = mImageList.getHits()[clickedPos];
            clickListener.onImageClick(image, v);
        }
    }

    public void setData(ImageList imageList, boolean favorite){
        this.mImageList = imageList;
        isFavorite = favorite;
        notifyDataSetChanged();
    }
}
