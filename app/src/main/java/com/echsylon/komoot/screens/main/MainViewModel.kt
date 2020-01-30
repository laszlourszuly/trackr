package com.echsylon.komoot.screens.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.echsylon.komoot.TrackrApplication
import com.echsylon.komoot.storage.FlickrDatabase
import com.echsylon.komoot.storage.Picture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * This class orchestrates the different reactive data sources toggled by user
 * events and other flows (such as database updates etc).
 */
class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val _grant = MutableLiveData<Unit>()
    private val _enable = MutableLiveData<Unit>()


    /**
     * Returns a live data object for the cached pictures meta data. The
     * under laying database will update this live data object as entries
     * are added and removed.
     */
    val pictures: LiveData<List<Picture>> by lazy {
        FlickrDatabase.getInstance(app)
            .pictureDao()
            .getAllPictures()
    }

    /**
     * Returns the location tracking state changes.
     */
    val tracking: LiveData<Boolean>
        get() {
            val app: TrackrApplication = getApplication()
            return app.tracking
        }

    /**
     * Emits an event when a location updates subscription is prevented by
     * lacking permissions.
     */
    val grant: LiveData<Unit>
        get() = _grant

    /**
     * Emits an event when a location updates subscription is prevented by
     * disabled location services.
     */
    val enable: LiveData<Unit>
        get() = _enable

    /**
     * Clears the local pictures meta-data cache. The action is executed on
     * a worker thread.
     */
    fun clearAllPictures() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val context: Context = getApplication()
                FlickrDatabase.getInstance(context)
                    .pictureDao()
                    .deleteAll()
            }
        }
    }

    /**
     * Toggles the tracking state.
     */
    fun toggleTracking() {
        if (tracking.value == true) {
            stopTracking()
        } else {
            startTracking()
        }
    }

    /**
     * Starts tracking location updates independently of the current lifecycle
     * owner (typically an Activity). Fires of corresponding events if lacking
     * permissions or the location services are disabled.
     */
    fun startTracking() {
        val app: TrackrApplication = getApplication()
        if (app.hasLocationPermissions()) {
            if (app.isLocationEnabled()) {
                app.startLocationService()
            } else {
                _enable.postValue(null)
            }
        } else {
            _grant.postValue(null)
        }
    }

    /**
     * Stops tracking location updates.
     */
    fun stopTracking() {
        val app: TrackrApplication = getApplication()
        app.stopLocationService()
    }

}