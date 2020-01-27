package com.echsylon.komoot.location

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import androidx.core.app.ActivityCompat

/**
 * This class offers convent ways of checking whether the user has granted
 * location update permissions or not, and if location updates are enabled
 * on the device.
 */
class LocationPermissionHelper {
    /**
     * Checks whether the user has granted location tracking permissions
     * to this app.
     *
     * @param context The context to check the permissions from.
     * @return Boolean true if there is user consent, otherwise false.
     */
    fun hasLocationPermissions(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
    }

    /**
     * Checks whether the user has enabled location updates on the device
     * or not.
     *
     * @param context The context to check the settings from.
     * @return Boolean true if the location setting is enabled, or false.
     */
    fun isLocationEnabled(context: Context): Boolean {
        val manager = context.getSystemService(LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled(GPS_PROVIDER) || manager.isProviderEnabled(NETWORK_PROVIDER)
    }
}