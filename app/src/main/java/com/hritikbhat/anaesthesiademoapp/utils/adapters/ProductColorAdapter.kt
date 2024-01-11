package com.hritikbhat.anaesthesiademoapp.utils.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.hritikbhat.anaesthesiademoapp.R
import com.hritikbhat.anaesthesiademoapp.models.Attribute


class ProductColorAdapter(private val myContext: Context, private val attributes: List<Attribute>) : RecyclerView.Adapter<ProductColorAdapter.ImageViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_color_item, parent, false)
        return ImageViewHolder(view)
    }

    interface OnItemClickListener {
        fun onSelectingColorItem(selectedColorAttribute: Attribute)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {

        val imageURL = attributes[position].swatch_url

        holder.colorIV.setOnClickListener {
            onItemClickListener?.onSelectingColorItem(attributes[position])
        }

        Glide.with(myContext)
            .load(imageURL)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .skipMemoryCache(true)
            .placeholder(R.drawable.color_placeholder_bg2)
            .circleCrop()
            .dontAnimate()
            .into(holder.colorIV)

    }

    override fun getItemCount() = attributes.size

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorIV: ImageView = itemView.findViewById(R.id.productColorItemIV)
    }

}