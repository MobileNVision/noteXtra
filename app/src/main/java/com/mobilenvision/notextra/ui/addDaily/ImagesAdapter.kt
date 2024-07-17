package com.mobilenvision.notextra.ui.addDaily

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobilenvision.notextra.R

class ImagesAdapter : RecyclerView.Adapter<ImagesAdapter.ImageViewHolder>() {
    private lateinit var mImagesAdapterListener: ImagesAdapterListener

    private var images: List<String> = emptyList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }
    fun setListener(imagesAdapterListener: ImagesAdapterListener) {
        this.mImagesAdapterListener = imagesAdapterListener
    }
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = images[position]
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .into(holder.imageView)

        holder.imageView.setOnClickListener {
            mImagesAdapterListener.onImagesItemClick(imageUrl)
        }
        holder.imageView.setOnLongClickListener {
            mImagesAdapterListener.onImagesItemLongClick(imageUrl)
            true
        }
    }

    override fun getItemCount(): Int = images.size

    fun addImage(uri: List<String>) {
        images= uri
        notifyDataSetChanged()
    }
    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }
    interface ImagesAdapterListener {
        fun onImagesItemClick(imageUrl: String)
        fun onImagesItemLongClick(imageUrl: String)
    }
}
