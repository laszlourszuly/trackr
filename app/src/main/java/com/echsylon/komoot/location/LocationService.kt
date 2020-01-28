package com.echsylon.komoot.location

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.echsylon.komoot.R
import com.echsylon.komoot.isNotTracking
import com.echsylon.komoot.picture.SearchReceiver
import com.echsylon.komoot.screens.main.MainActivity
import com.echsylon.komoot.setNotTracking
import com.echsylon.komoot.setTracking
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import java.util.concurrent.TimeUnit

/**
 * This class configures the location update metrics as this app wants it
 * (e.g. no more frequent than 100 meters apart etc) and manages the
 * subscription state.
 */
class LocationService : Service() {

    companion object {
        private const val FOREGROUND_ID = 9341
        private const val NOTIFICATION_ID = 3242
        private const val BROADCAST_ID = 1232
        private const val ACTION_START = "com.echsylon.komoot.locationservice.ACTION_START"
        private const val ACTION_STOP = "com.echsylon.komoot.locationservice.ACTION_STOP"
        private const val CHANNEL_NAME = "Komoot Challenge Notification Channel"
        private const val CHANNEL_ID = "com.echsylon.komoot.location.NOTIFICATION_CHANNEL"

        /**
         * Tries to start this service as a foreground service, unless it's
         * already started.
         *
         * @param context The context to start the service in.
         */
        fun startSafely(context: Context) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            if (preferences.isNotTracking()) {
                val intent = Intent(ACTION_START, Uri.EMPTY, context, LocationService::class.java)
                ContextCompat.startForegroundService(context, intent)
            }
        }

        /**
         * Ensures a stop action is sent to this service, regardless which
         * state it is in.
         *
         * @param context The context used to send the stop action.
         */
        fun stopSafely(context: Context) {
            val intent = Intent(ACTION_STOP, Uri.EMPTY, context, LocationService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }
    }

    // This would be Android location client we're subscribing through.
    private val locationClient by lazy { LocationServices.getFusedLocationProviderClient(this) }

    // This is the Intent we're asking the Android location client to broadcast
    // on our behalf every time a matching location update is present.
    private val pendingIntent by lazy {
        val intent = Intent(SearchReceiver.ACTION_SEARCH)
        intent.setClass(this, SearchReceiver::class.java)
        PendingIntent.getBroadcast(this, BROADCAST_ID, intent, 0)
    }


    override fun onBind(intent: Intent?): IBinder? {
        // Don't support binding for now
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return when (intent?.action) {
            ACTION_START -> {
                startService()
                subscribeToLocationUpdates()
                PreferenceManager.getDefaultSharedPreferences(this).setTracking()
                START_STICKY
            }
            ACTION_STOP -> {
                unsubscribeFromLocationUpdates()
                stopService()
                PreferenceManager.getDefaultSharedPreferences(this).setNotTracking()
                START_NOT_STICKY
            }
            else -> {
                START_NOT_STICKY
            }
        }
    }

    override fun onDestroy() {
        // Make sure we reset properly in extraordinary situations as well.
        unsubscribeFromLocationUpdates()
        super.onDestroy()
    }

    // Starts this service as a foreground service, making sure a corresponding
    // notification is posted to the status bar.
    private fun startService() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext, NOTIFICATION_ID, intent, FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(getText(R.string.app_name))
            .setContentText(getText(R.string.tracking))
            .setSmallIcon(R.drawable.ic_burst)
            .setContentIntent(pendingIntent)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE_HIGH)
            val manager = getSystemService(NotificationManager::class.java) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        startForeground(FOREGROUND_ID, notification)
    }

    // Stops this foreground service and removes the status bar notification.
    private fun stopService() {
        stopForeground(true)
        stopSelf()
    }

    // Configures the location updates we want on subscribes accordingly with
    // the Android location client.
    private fun subscribeToLocationUpdates() {
        val locations = LocationPermissionHelper()
        if (locations.hasLocationPermissions(this) && locations.isLocationEnabled(this)) {
            val locationRequest = LocationRequest()
            locationRequest.priority = PRIORITY_HIGH_ACCURACY
            locationRequest.interval = TimeUnit.SECONDS.toMillis(20)
            locationRequest.fastestInterval = TimeUnit.SECONDS.toMillis(10)
            locationRequest.maxWaitTime = TimeUnit.MINUTES.toMillis(1)
            locationRequest.smallestDisplacement = 100.0f
            locationClient.requestLocationUpdates(locationRequest, pendingIntent)
        }
    }

    // Unsubscribes from previously subscribed location updates.
    private fun unsubscribeFromLocationUpdates() {
        locationClient.removeLocationUpdates(pendingIntent)
    }

}