package com.example.mccfirebasestorage.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mccfirebasestorage.R
import com.example.mccfirebasestorage.models.Image
import kotlinx.android.synthetic.main.activity_add_image.*
import kotlinx.android.synthetic.main.image_item.view.*

class ImageAdapter(
    private var context: Context,
    private var data: ArrayList<Image>
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image = view.imageView!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Glide.with(context).load(Uri.parse(data[position].imageUri)).into(holder.image)
    }

}