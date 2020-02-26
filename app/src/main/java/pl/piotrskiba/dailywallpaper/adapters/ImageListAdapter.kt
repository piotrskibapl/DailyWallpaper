package pl.piotrskiba.dailywallpaper.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import pl.piotrskiba.dailywallpaper.R
import pl.piotrskiba.dailywallpaper.adapters.ImageListAdapter.ImageViewHolder
import pl.piotrskiba.dailywallpaper.interfaces.ImageClickListener
import pl.piotrskiba.dailywallpaper.models.ImageList
import pl.piotrskiba.dailywallpaper.utils.BitmapUtils.loadBitmap

class ImageListAdapter(private val context: Context, private val clickListener: ImageClickListener) : RecyclerView.Adapter<ImageViewHolder>() {
    private var mImageList: ImageList? = null
    private var isFavorite = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.image_list_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = mImageList!!.hits[position]!!
        if (isFavorite) holder.mThumbnail!!.setImageBitmap(loadBitmap(context, image.webformatURL)) else Glide.with(context)
                .load(image.webformatURL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.mThumbnail!!)
    }

    override fun getItemCount(): Int {
        return if (mImageList == null) 0 else mImageList!!.hits.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        @JvmField
        @BindView(R.id.iv_thumbnail)
        var mThumbnail: ImageView? = null

        override fun onClick(v: View) {
            val clickedPos = adapterPosition
            val image = mImageList!!.hits[clickedPos]!!
            clickListener.onImageClick(image, v)
        }

        init {
            ButterKnife.bind(this, itemView)
            itemView.setOnClickListener(this)
        }
    }

    fun setData(imageList: ImageList?, favorite: Boolean) {
        mImageList = imageList
        isFavorite = favorite
        notifyDataSetChanged()
    }

}