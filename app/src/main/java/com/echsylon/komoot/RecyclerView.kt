package com.echsylon.komoot

import android.graphics.Rect
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.echsylon.komoot.screens.main.FlickrImageAdapter
import com.echsylon.komoot.storage.Picture

/**
 * This class offers convenient extensions to the default RecyclerView
 * implementation, some of which are made available to the data binding
 * infrastructure as well.
 */

/**
 * Sets the picture data of the RecyclerView's adapter. This method will create
 * a new adapter of proper type if there isn't one set yet. Furthermore it will
 * ignore the data if the adapter isn't of the correct type.
 *
 * This method can also be bound from the layout XML through the "app:pictures"
 * attribute on the RecyclerView.
 *
 * @param items The new adapter data.
 */
@BindingAdapter("pictures")
fun RecyclerView.bindPictures(items: List<Picture>?) {
    val tmpAdapter = adapter
    if (tmpAdapter == null) {
        val newAdapter = FlickrImageAdapter()
        newAdapter.addPictures(items ?: listOf())
        adapter = newAdapter
    } else if (tmpAdapter is FlickrImageAdapter) {
        tmpAdapter.addPictures(items ?: listOf())
    }
}

/**
 * Sets the proper spacing between list items.
 *
 * This method can also be bound in the layout XML through the "app:itemSpacing"
 * attribute on the RecyclerView.
 *
 * @param resource The resource id of the spacing dimension to apply.
 */
@BindingAdapter("itemSpacing")
fun RecyclerView.bindItemSpacing(resource: Int) {
    val spacing = resources.getDimensionPixelSize(resource)
    addItemDecoration(ListItemMarginDecoration(spacing))
}

/**
 * This class knows how evenly distribute the spacing between RecyclerView
 * items in a way that is not possible or maintainable through layout XML
 * configurations only.
 */
class ListItemMarginDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        with(outRect) {
            if (parent.getChildAdapterPosition(view) == 0) {
                top = space
            }
            left = space
            right = space
            bottom = space
        }
    }
}