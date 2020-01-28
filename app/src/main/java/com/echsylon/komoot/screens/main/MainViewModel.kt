package com.echsylon.komoot.screens.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.echsylon.komoot.isTracking
import com.echsylon.komoot.location.LocationPermissionHelper
import com.echsylon.komoot.location.LocationService
import com.echsylon.komoot.storage.FlickrDatabase
import com.echsylon.komoot.storage.Picture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.TestOnly

/**
 * This class orchestrates the different reactive data sources toggled by user
 * events and other flows (such as database updates etc).
 */
class MainViewModel(app: Application) : AndroidViewModel(app) {
    private var locationPermissionHelper = LocationPermissionHelper()
    private val _tracking = MutableLiveData<Boolean>()
    private val _grant = MutableLiveData<Unit>()
    private val _enable = MutableLiveData<Unit>()

    init {
        val preferences = PreferenceManager.getDefaultSharedPreferences(app)
        val isTracking = preferences.isTracking()
        _tracking.postValue(isTracking)
    }

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
        get() = _tracking

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
     * Forces the tracking state to a certain value.
     *
     * @param isTracking The new state value to enforce.
     */
    // TODO: This state handling isn't sufficient. Refactor to a more robust
    //       solution.
    fun forceTrackingState(isTracking: Boolean) {
        _tracking.postValue(isTracking)
    }

    /**
     * Starts tracking location updates independently of the current lifecycle
     * owner (typically an Activity). Fires of corresponding events if lacking
     * permissions or the location services are disabled.
     */
    fun startTracking() {
        val context: Context = getApplication()
        if (locationPermissionHelper.hasLocationPermissions(context)) {
            if (locationPermissionHelper.isLocationEnabled(context)) {
                LocationService.startSafely(context)
                _tracking.postValue(true)
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
        val context: Context = getApplication()
        LocationService.stopSafely(context)
        _tracking.postValue(false)
    }

    @TestOnly
    fun setLocationPermissionHelper(helper: LocationPermissionHelper) {
        locationPermissionHelper = helper
    }
}