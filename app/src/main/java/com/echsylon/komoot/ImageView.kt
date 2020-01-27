package com.echsylon.komoot

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

/**
 * This class offers convenient extensions to the default ImageView
 * implementation, some of which are made available to the data binding
 * infrastructure as well.
 */

/**
 * Downloads an image at the given URL and sets it as the ImageView source.
 *
 * This method can also be bound from the layout XML through the "app:url"
 * attribute on the ImageView.
 *
 * @param url The full URL of the picture to download.
 */
@BindingAdapter("url")
fun ImageView.bindUrl(url: String) {
    if (url.isEmpty()) {
        setImageDrawable(null)
    } else {
        Picasso.get().load(url).into(this)
    }
}