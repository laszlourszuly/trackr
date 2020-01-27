package com.echsylon.komoot.screens.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.echsylon.komoot.databinding.ItemPictureBinding
import com.echsylon.komoot.storage.Picture

/**
 * This adapter knows how to configure the Android data binding infrastructure
 * to show Picture data. The actual data-to-view binding can be seen in the
 * layout XML.
 */
class FlickrImageAdapter : RecyclerView.Adapter<FlickrImageAdapter.ViewHolder>() {

    class ViewHolder(private val binder: ItemPictureBinding) : RecyclerView.ViewHolder(binder.root) {
        fun bind(picture: Picture) {
            binder.picture = picture
            binder.executePendingBindings()
        }
    }

    private val content: MutableList<Picture> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binder = ItemPictureBinding.inflate(inflater, parent, false)
        return ViewHolder(binder)
    }

    override fun getItemCount(): Int {
        return content.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(content[position])
    }

    /**
     * Adds pictures to the beginning of the list. Only new pictures are added.
     *
     * @param pictures The new data to extract new pictures from.
     */
    fun addPictures(pictures: List<Picture>) {
        val delta: MutableList<Picture> = pictures.toMutableList()

        delta.removeAll(content)
        content.addAll(0, delta)
        notifyItemRangeInserted(0, delta.size)
    }

}