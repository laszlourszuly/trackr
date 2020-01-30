package com.echsylon.komoot

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.echsylon.komoot.location.LocationService
import java.util.concurrent.atomic.AtomicBoolean

class TrackrApplication : Application() {
    private val isTracking = AtomicBoolean(false)
    private val _tracking = MutableLiveData<Boolean>(isTracking.get())

    /**
     * Exposes the tracking state as a reactive data stream.
     */
    val tracking: LiveData<Boolean>
        get() = _tracking

    /**
     * Tries to start the LocationService as a foreground service, unless it's
     * already started. How it's actually started depends on the platform
     * version and is managed by the ContextCompat implementation.
     */
    fun startLocationService() {
        if (!isTracking.getAndSet(true)) {
            val intent = Intent(LocationService.ACTION_START)
            intent.setClass(this, LocationService::class.java)
            ContextCompat.startForegroundService(this, intent)
            _tracking.postValue(isTracking.get())
        }
    }

    /**
     * Tries to stop the LocationService, unless it's already stopped.
     */
    fun stopLocationService() {
        if (isTracking.getAndSet(false)) {
            val intent = Intent(LocationService.ACTION_STOP)
            intent.setClass(this, LocationService::class.java)
            ContextCompat.startForegroundService(this, intent)
            _tracking.postValue(isTracking.get())
        }
    }

    /**
     * Checks whether the user has granted location tracking permissions
     * to this app.
     *
     * @return Boolean true if there is user consent, otherwise false.
     */
    fun hasLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
    }

    /**
     * Checks whether the user has enabled location updates on the device
     * or not.
     *
     * @return Boolean true if the location setting is enabled, or false.
     */
    fun isLocationEnabled(): Boolean {
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled(GPS_PROVIDER) || manager.isProviderEnabled(NETWORK_PROVIDER)
    }

}