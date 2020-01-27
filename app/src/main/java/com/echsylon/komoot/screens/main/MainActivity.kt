package com.echsylon.komoot.screens.main

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import com.echsylon.komoot.R
import com.echsylon.komoot.databinding.ActivityMainBinding

/**
 * This activity shows the collected pictures for our route. It knows how to
 * toggle the location tracking and how to request related permissions, when
 * needed. This activity delegates the actual data-to-view binding effort to
 * the Android data binding infrastructure. The actual mapping can be seen
 * in the main activity layout XML.
 */
class MainActivity : AppCompatActivity() {

    companion object {
        // The location permission request id, used to identify which
        // permission related callback is passed to us by the Android
        // system.
        private const val PERMISSION_ID = 2321
    }

    // This view model will be passed to the data binding infrastructure in
    // order to map data to the view and act on user interaction.
    private val viewModel by lazy { ViewModelProviders.of(this)[MainViewModel::class.java] }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // The tracking foreground service will pass on the "tracking=on" state
        // when showing this activity from the status bar notification.
        // TODO: Fix this state handling. Right now the activity won't be able
        //       to detect a running service when (re)created from the home
        //       launcher as a result of user interaction.
        viewModel.forceTrackingState(intent.getBooleanExtra("tracking", false))

        // Observe any permission request trigger-events. These will be sent
        // (through the data binding and view model infrastructures) when the
        // user taps the "Start" button.
        viewModel.enable.observe(this) { showEnableLocationsSettingsDialog() }
        viewModel.grant.observe(this) { showGrantLocationsPermissionsDialog() }

        // Configure the data binding.
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clear -> {
                viewModel.clearAllPictures()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if (grantResults.contains(PERMISSION_GRANTED)) {
                viewModel.startTracking()
            }
        }
    }


    // Asks the Android system to show the request-location-permissions dialog,
    // preceding it with showing a rationale dialog if needed.
    private fun showGrantLocationsPermissionsDialog() {
        val showCoarseRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_COARSE_LOCATION)
        val showFineRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)
        val shouldRationalize = showCoarseRationale || showFineRationale
        if (shouldRationalize) {
            LocationRationaleDialog { requestPermissions() }
                .show(supportFragmentManager, "RationaleDialog")
        } else {
            requestPermissions()
        }
    }

    // Asks the Android system to show the permission request dialog for coarse
    // and fine grained location updates.
    private fun requestPermissions() {
        val permissions = arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_ID)
    }

    // Shows a dialog informing the user that the location services need to be
    // enabled in the Settings app.
    private fun showEnableLocationsSettingsDialog() {
        LocationEnableDialog { startActivity(Intent(ACTION_LOCATION_SOURCE_SETTINGS)) }
            .show(supportFragmentManager, "EnableDialog")
    }

}
