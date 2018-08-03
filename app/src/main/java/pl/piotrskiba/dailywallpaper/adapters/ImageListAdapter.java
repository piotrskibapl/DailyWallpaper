package pl.piotrskiba.dailywallpaper.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.piotrskiba.dailywallpaper.R;
import pl.piotrskiba.dailywallpaper.interfaces.ImageClickListener;
import pl.piotrskiba.dailywallpaper.models.Image;
import pl.piotrskiba.dailywallpaper.models.ImageList;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ImageViewHolder> {

    private ImageList mImageList;
    private ImageClickListener clickListener;

    public ImageListAdapter(ImageClickListener clickListener){
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

        Picasso.get()
                .load(image.getWebformatURL())
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
            clickListener.onImageClick(image);
        }
    }

    public void setData(ImageList imageList){
        this.mImageList = imageList;
        notifyDataSetChanged();
    }
}
