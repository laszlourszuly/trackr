package com.echsylon.komoot.picture

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.echsylon.komoot.storage.FlickrDatabase
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * This broadcast receiver listens to location updates as configured by
 * this app and searches for corresponding pictures in the Flickr cloud.
 *
 * @see com.echsylon.komoot.location.LocationService
 */
class SearchReceiver : BroadcastReceiver() {

    companion object {
        /**
         * Mandatory Intent action to use in the broadcast that triggers
         * this receiver.
         */
        const val ACTION_SEARCH = "com.echsylon.komoot.ACTION_SEARCH"

        // Hardcoded constants. A more elegant way would be to expose
        // these through a settings UI or similar.
        private const val DEFAULT_API_KEY = "99e974ef1bd6c514486cb0d7f39bcb9a"
        private const val DEFAULT_BASE_URL = "https://www.flickr.com"
        private const val DEFAULT_RADIUS = 0.4f
    }

    // Defining the coroutine worker environment.
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action ?: "" != ACTION_SEARCH) {
            return
        }

        val result = LocationResult.extractResult(intent) ?: return
        val location = result.lastLocation ?: return
        val tmpContext = context ?: return
        scope.launch { getPictures(tmpContext, location.latitude, location.longitude,
            DEFAULT_RADIUS
        ) }
    }

    // Delegates the search process to a dedicated class and writes any
    // returned meta-data to a local storage.
    private suspend fun getPictures(context: Context, latitude: Double, longitude: Double, radius: Float) {
        val flickrClient = FlickrClient(
            DEFAULT_BASE_URL,
            DEFAULT_API_KEY
        )
        val pictures = flickrClient.getPictures(latitude, longitude, radius)

        if (pictures.isNotEmpty()) {
            val pictureDao = FlickrDatabase.getInstance(context).pictureDao()
            pictures.forEach { pictureDao.insert(it) }
        }
    }

}