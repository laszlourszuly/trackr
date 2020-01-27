package com.echsylon.komoot.picture

import com.echsylon.komoot.storage.Picture
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * This class is responsible for the HTTP communication with the Flickr API.
 * It offers means of searching for public pictures at a given latitude
 * longitude location (within a given radius).
 *
 * This class operates on the calling thread. It's the responsibility of the
 * caller to spawn worker threads as needed.
 */
class FlickrClient(private val baseUrl: String, private val apiKey: String) {

    companion object {
        // Internal data class representing the Flickr response object on a
        // public photos search query.
        private data class Response(
            val photos: Photos
        )

        // Internal data class representing the meta data for a Flickr photos
        // query. Strangely the API provides the query result as well in this
        // entity.
        private data class Photos(
            val photo: List<Photo>
        )

        // Internal data class representing a subset of the Flickr photos query
        // result.
        data class Photo(
            val id: String,
            val title: String?,
            val owner: String?,
            val url_l: String?,
            val width_l: Int?,
            val height_l: Int?,
            val latitude: Double?,
            val longitude: Double?,
            val dateupload: Long?
        )
    }

    /**
     * Requests pictures at a given location, fenced by a radius. This class
     * has no knowledge of prior queries and will always perform a round trip
     * to the Flickr servers.
     *
     * @param latitude The decimal format latitude of the picture location.
     * @param longitude The decimal format longitude of the picture location.
     * @param radius The geo fence radius expressed in kilometers.
     * @return A list of washed picture data.
     */
    fun getPictures(latitude: Double, longitude: Double, radius: Float): List<Picture> {
        return getPhotos(baseUrl, apiKey, radius, latitude, longitude)
            .map {
                Picture(
                    it.id,
                    it.title ?: "",
                    it.owner ?: "",
                    it.latitude ?: 0.0,
                    it.longitude ?: 0.0,
                    it.url_l ?: "",
                    it.width_l ?: 0,
                    it.height_l ?: 0,
                    it.dateupload ?: 0
                )
            }
    }


    // Requests all public Flickr photos within a [radius] number of kilometers
    // from the given latitude + longitude location.
    //
    // NOTE!!! It's unclear how the Flickr API treats pictures with no location
    // EXIF data. The user might need to decorate the pictures with location
    // data BEFORE uploading them to the Flickr infrastructure in order to have
    // them show up here.
    private fun getPhotos(baseUrl: String, apiKey: String, radius: Float, lat: Double, lng: Double): List<Photo> {
        val url = "$baseUrl/services/rest/" +
                "?method=flickr.photos.search" +
                "&api_key=$apiKey" +
                "&lat=$lat" +
                "&lon=$lng" +
                "&radius=$radius" +
                "&radius_units=km" +
                "&extras=url_l,date_upload,geo" +
                "&format=json" +
                "&nojsoncallback=1"

        val request = Request.Builder()
            .url(url)
            .build()

        val json = OkHttpClient()
            .newCall(request)
            .execute()
            .takeIf { it.isSuccessful }
            .use { it?.body?.string() ?: "" }

        val response = Gson().fromJson(json, Response::class.java)
        return response
            ?.photos
            ?.photo
            .orEmpty()
    }

}