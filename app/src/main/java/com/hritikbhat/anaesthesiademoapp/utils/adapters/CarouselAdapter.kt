package com.hritikbhat.anaesthesiademoapp.utils.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hritikbhat.anaesthesiademoapp.R


class CarouselAdapter(private val myContext: Context, private val images: List<String>) : RecyclerView.Adapter<CarouselAdapter.ImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.carousel_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {

        Glide.with(myContext)
            .load(images[position])
            .placeholder(R.drawable.app_logo) // Replace placeholder_image with your placeholder resource
            .into(holder.carouselImage);


    }

    override fun getItemCount() = images.size

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val carouselImage: ImageView = itemView.findViewById(R.id.carouselItemImage)
    }

}